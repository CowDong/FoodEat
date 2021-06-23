package net.runelite.client.plugins.nightmarehelper;

import com.google.inject.Provides;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.Point;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
        name = "Nightmare Auto Prayer",
        enabledByDefault = false,
        description = "Automatically swap prayers in Nightmare of Ashihama",
        tags = {"bosses", "combat", "nm", "overlay", "nightmare", "pve", "pvm", "ashihama", "prayer", "pray", "ben", "ben93riggs"}
)

@Slf4j
@Singleton
public class NightmareHelperPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private NightmareHelperConfig config;

    @Nullable
    private NPC nm;

    private boolean inFight;
    private boolean cursed;
    private int attacksSinceCurse;

    private int timeout;
    private boolean swapMage;
    private boolean swapRange;
    private boolean swapMelee;
    private Prayer prayerToClick;

    public NightmareHelperPlugin() {
        inFight = false;
    }

    @Provides
    NightmareHelperConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(NightmareHelperConfig.class);
    }

    @Override
    protected void startUp() {
        reset();
    }

    @Override
    protected void shutDown() {
        reset();
    }

    private void reset() {
        inFight = false;
        nm = null;
        cursed = false;
        attacksSinceCurse = 0;
        swapMage = false;
        swapRange = false;
        swapMelee = false;
        timeout = 0;
        prayerToClick = null;
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        Actor actor = event.getActor();
        if (!(actor instanceof NPC)) {
            return;
        }

        NPC npc = (NPC) actor;

        //this will trigger once when the fight begins
        if (npc.getId() == NpcID.THE_NIGHTMARE_9432) {
            //reset everything
            reset();
            nm = npc;
            inFight = true;
        }

        if (!inFight || nm == null) {
            return;
        }

        int animationId = npc.getAnimation();

        switch (animationId) {
            case NightmareAttackAnimations.NIGHTMARE_MAGIC_ATTACK:
                attacksSinceCurse++;
                timeout = config.ticksSleepRangeMage();
                if (cursed) {
                    swapMelee = true;
                } else {
                    swapMage = true;
                }
                break;
            case NightmareAttackAnimations.NIGHTMARE_MELEE_ATTACK:
                attacksSinceCurse++;
                timeout = config.ticksSleepMelee();
                if (cursed) {
                    swapRange = true;
                } else {
                    swapMelee = true;
                }
                break;
            case NightmareAttackAnimations.NIGHTMARE_RANGE_ATTACK:
                attacksSinceCurse++;
                timeout = config.ticksSleepRangeMage();
                if (cursed) {
                    swapMage = true;
                } else {
                    swapRange = true;
                }
                break;
            case NightmareAttackAnimations.NIGHTMARE_CURSE:
                cursed = true;
                attacksSinceCurse = 0;
                break;
            default:
                break;
        }

        if (cursed && attacksSinceCurse == 5) {
            //curse is removed when she phases, or does 5 attacks
            cursed = false;
            attacksSinceCurse = -1;
        }
    }

    @Subscribe
    public void onNpcChanged(NpcChanged event) {
        //log.info("NpcChanged event triggered." + event.getNpc().getId());
        final NPC npc = event.getNpc();

        if (npc == null) {
            return;
        }

        //if ID changes to 9431 (3rd phase) and is cursed, remove the curse
        if (cursed && npc.getId() == NpcID.THE_NIGHTMARE_9431) {
            cursed = false;
            attacksSinceCurse = -1;
        }
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged event) {
        GameState gamestate = event.getGameState();

        //if loading happens while inFight, the user has left the area (either via death or teleporting).
        if (gamestate == GameState.LOADING && inFight) {
            reset();
        }
    }

    @Subscribe
    private void onGameTick(final GameTick event) {
        if (!inFight || nm == null) {
            return;
        }

        //if nightmare's id is 9433, the fight has ended and everything should be reset
        if (nm.getId() == NpcID.THE_NIGHTMARE_9433) {
            reset();
        }
        if (swapMage && timeout == 0) {
            activatePrayer(Prayer.PROTECT_FROM_MAGIC);
            swapMage = false;
        } else if (swapRange && timeout == 0) {
            activatePrayer(Prayer.PROTECT_FROM_MISSILES);
            swapRange = false;
        } else if (config.swapNightmareMelee() && swapMelee && timeout == 0) {
            activatePrayer(Prayer.PROTECT_FROM_MELEE);
            swapMelee = false;
        }

        if (timeout != 0) {
            timeout--;
        }
    }

    public void activatePrayer(Prayer prayer) {
        if (client.isPrayerActive(prayer)) {
            return;
        }

        Widget prayer_widget = client.getWidget(prayer.getWidgetInfo());

        if (prayer_widget == null) {
            return;
        }

        if (client.getBoostedSkillLevel(Skill.PRAYER) <= 0) {
            return;
        }

        clientThread.invoke(() ->
                client.invokeMenuAction("Activate",
                        prayer_widget.getName(),
                        1,
                        MenuAction.CC_OP.getId(),
                        prayer_widget.getItemId(),
                        prayer_widget.getId()));
    }
}