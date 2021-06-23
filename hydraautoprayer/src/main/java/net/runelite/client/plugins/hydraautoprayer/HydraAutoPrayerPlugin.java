/*
 * Copyright (c) 2018, https://openosrs.com
 * Copyright (c) 2020, Dutta64 <https://github.com/dutta64>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.hydraautoprayer;

import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.*;

@Extension
@PluginDescriptor(
        name = "Hydra Auto Prayer",
        enabledByDefault = false,
        description = "Swaps prayer for hydra",
        tags = {"hydra", "helper", "baby", "small", "normal", "regular", "auto", "prayer", "swapper", "ben93riggs"},
        hidden = true
)
public class HydraAutoPrayerPlugin extends Plugin {
    static final Set<HydraAnimation> VALID_HYDRA_ANIMATIONS = EnumSet.of(HydraAnimation.RANGE, HydraAnimation.MAGIC);

    private static final String NPC_NAME_HYDRA = "Hydra";

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    private final Map<Integer, Hydra> hydras = new HashMap<>();

    @Getter(AccessLevel.PACKAGE)
    private NPC interactingNpc = null;

    @Override
    protected void startUp() {
        resetHydras();
    }

    @Override
    protected void shutDown() {
        resetHydras();
    }

    @Subscribe
    private void onNpcSpawned(final NpcSpawned event) {
        final NPC npc = event.getNpc();

        if (isActorHydra(npc)) {
            addHydra(npc);
        }
    }

    @Subscribe
    private void onNpcDespawned(final NpcDespawned event) {
        final NPC npc = event.getNpc();

        if (isActorHydra(npc)) {
            removeHydra(npc);
        }
    }

    @Subscribe
    private void onInteractingChanged(final InteractingChanged event) {
        final Actor source = event.getSource();

        if (!isActorHydra(source)) {
            return;
        }

        final NPC npc = (NPC) source;

        addHydra(npc);
        updateInteractingNpc(npc);
    }

    @Subscribe
    private void onAnimationChanged(final AnimationChanged event) {
        final Actor actor = event.getActor();

        if (!isActorHydra(actor)) {
            return;
        }

        final NPC npc = (NPC) event.getActor();

        addHydra(npc);
        updateInteractingNpc(npc);

        HydraAnimation hydraAnimation;

        try {
            hydraAnimation = HydraAnimation.fromId(npc.getAnimation());
        } catch (final IllegalArgumentException e) {
            hydraAnimation = null;
        }

        if (hydraAnimation == null || !VALID_HYDRA_ANIMATIONS.contains(hydraAnimation)) {
            // If the animation is not range/magic then do nothing.
            return;
        }

        final Hydra hydra = hydras.get(npc.getIndex());

        if (hydra.getHydraAnimation() == null) {
            // If this is the first observed animation then set it
            hydra.setHydraAnimation(hydraAnimation);
        } else {
            if (!Objects.equals(hydra.getHydraAnimation(), hydraAnimation)) {
                // If the animation switched from range/magic then set it and reset attack count
                hydra.setHydraAnimation(hydraAnimation);
                hydra.resetAttackCount();
            }
        }

        hydra.updateAttackCount();
    }

    @Subscribe
    private void onClientTick(final ClientTick event) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        if (interactingNpc == null) {
            return;
        }

        final Hydra hydra = hydras.get(interactingNpc.getIndex());

        final boolean attackCountIsMax = hydra.getAttackCount() == Hydra.MAX_ATTACK_COUNT;

        switch (hydra.getHydraAnimation()) {
            case RANGE:
                activatePrayer(attackCountIsMax ? HydraAnimation.MAGIC.getPrayer() : HydraAnimation.RANGE.getPrayer());
            case MAGIC:
                activatePrayer(attackCountIsMax ? HydraAnimation.RANGE.getPrayer() : HydraAnimation.MAGIC.getPrayer());
            default:
                break;
        }
    }

    private static boolean isActorHydra(final Actor actor) {
        return Objects.equals(actor.getName(), NPC_NAME_HYDRA);
    }

    private void updateInteractingNpc(final NPC npc) {
        if (!Objects.equals(interactingNpc, npc) && Objects.equals(npc.getInteracting(), client.getLocalPlayer())) {
            interactingNpc = npc;
        }
    }

    private void addHydra(final NPC npc) {
        final int npcIndex = npc.getIndex();

        if (!hydras.containsKey(npcIndex)) {
            hydras.put(npcIndex, new Hydra(npc));
        }
    }

    private void removeHydra(final NPC npc) {
        final int npcIndex = npc.getIndex();

        hydras.remove(npcIndex);

        if (Objects.equals(interactingNpc, npc)) {
            interactingNpc = null;
        }
    }

    private void resetHydras() {
        hydras.clear();
        interactingNpc = null;
    }

    public void activatePrayer(Prayer prayer) {
        if (prayer == null) {
            return;
        }

        //check if prayer is already active this tick
        if (client.isPrayerActive(prayer)) {
            return;
        }

        WidgetInfo widgetInfo = prayer.getWidgetInfo();

        if (widgetInfo == null) {
            return;
        }

        Widget prayer_widget = client.getWidget(widgetInfo);

        if (prayer_widget == null) {
            return;
        }

        if (client.getBoostedSkillLevel(Skill.PRAYER) <= 0) {
            return;
        }

        clientThread.invoke(() ->
                client.invokeMenuAction(
                        "Activate",
                        prayer_widget.getName(),
                        1,
                        MenuAction.CC_OP.getId(),
                        prayer_widget.getItemId(),
                        prayer_widget.getId()
                )
        );
    }
}