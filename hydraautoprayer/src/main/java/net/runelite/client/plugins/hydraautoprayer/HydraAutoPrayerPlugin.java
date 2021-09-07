package net.runelite.client.plugins.hydraautoprayer;

import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.hydraautoprayer.entity.Hydra;
import net.runelite.client.plugins.hydraautoprayer.entity.HydraPhase;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;

@Singleton
@Extension
@PluginDescriptor(
        name = "Hydra Auto Prayer (unfinished)",
        enabledByDefault = false,
        description = "Swaps prayer for hydra",
        tags = {"hydra", "helper", "baby", "small", "normal", "regular", "auto", "prayer", "swapper", "ben93riggs"},
        hidden = false
)
public class HydraAutoPrayerPlugin extends Plugin {
    private static final int[] HYDRA_REGIONS = {5279, 5280, 5535, 5536};

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    private boolean atHydra;

    @Getter
    private Hydra hydra;

    public static final int HYDRA_1_1 = 8237;
    public static final int HYDRA_1_2 = 8238;
    public static final int HYDRA_2_1 = 8244;
    public static final int HYDRA_2_2 = 8245;
    public static final int HYDRA_3_1 = 8251;
    public static final int HYDRA_3_2 = 8252;
    public static final int HYDRA_4_1 = 8257;
    public static final int HYDRA_4_2 = 8258;

    private int lastAttackTick = -1;

    @Override
    protected void startUp() {
        if (client.getGameState() == GameState.LOGGED_IN && isInHydraRegion()) {
            init();
        }
    }

    private void init() {
        atHydra = true;

        for (final NPC npc : client.getNpcs()) {
            onNpcSpawned(new NpcSpawned(npc));
        }
    }

    @Override
    protected void shutDown() {
        atHydra = false;

        hydra = null;
        lastAttackTick = -1;
    }

    @Subscribe
    private void onGameStateChanged(final GameStateChanged event) {
        final GameState gameState = event.getGameState();

        switch (gameState) {
            case LOGGED_IN:
                if (isInHydraRegion()) {
                    if (!atHydra) {
                        init();
                    }
                } else {
                    if (atHydra) {
                        shutDown();
                    }
                }
                break;
            case HOPPING:
            case LOGIN_SCREEN:
                if (atHydra) {
                    shutDown();
                }
            default:
                break;
        }
    }

    @Subscribe
    private void onNpcSpawned(final NpcSpawned event) {
        final NPC npc = event.getNpc();

        if (npc.getId() == NpcID.ALCHEMICAL_HYDRA) {
            hydra = new Hydra(npc);
        }
    }

    @Subscribe
    private void onAnimationChanged(final AnimationChanged event) {
        final Actor actor = event.getActor();

        if (hydra == null || actor != hydra.getNpc()) {
            return;
        }

        final HydraPhase phase = hydra.getPhase();

        final int animationId = actor.getAnimation();

        if ((animationId == phase.getDeathAnimation2() && phase != HydraPhase.FLAME)
                || (animationId == phase.getDeathAnimation1() && phase == HydraPhase.FLAME)) {
            switch (phase) {
                case POISON:
                    hydra.changePhase(HydraPhase.LIGHTNING);
                    break;
                case LIGHTNING:
                    hydra.changePhase(HydraPhase.FLAME);
                    break;
                case FLAME:
                    hydra.changePhase(HydraPhase.ENRAGED);
                    break;
                case ENRAGED:
                    // NpcDespawned event does not fire for Hydra inbetween kills; must use death animation.
                    hydra = null;
                    break;
            }
        }
    }

    @Subscribe
    private void onProjectileMoved(final ProjectileMoved event) {
        final Projectile projectile = event.getProjectile();

        if (hydra == null || client.getGameCycle() >= projectile.getStartMovementCycle()) {
            return;
        }

        final int projectileId = projectile.getId();

        if (client.getTickCount() != lastAttackTick
                && (projectileId == Hydra.AttackStyle.MAGIC.getProjectileID() || projectileId == Hydra.AttackStyle.RANGED.getProjectileID())) {
            hydra.handleProjectile(projectileId);

            lastAttackTick = client.getTickCount();
        }

        activatePrayer(hydra.getNextAttack().getPrayer());
    }

    private boolean isInHydraRegion() {
        return client.isInInstancedRegion() && Arrays.equals(client.getMapRegions(), HYDRA_REGIONS);
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