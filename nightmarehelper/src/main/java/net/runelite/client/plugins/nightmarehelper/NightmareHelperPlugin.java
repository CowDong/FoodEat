package net.runelite.client.plugins.nightmarehelper;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

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

    public static final int NIGHTMARE_MELEE_ATTACK = 8594;
    public static final int NIGHTMARE_RANGE_ATTACK = 8596;
    public static final int NIGHTMARE_MAGIC_ATTACK = 8595;

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
        if (nm == null && npc.getName() != null && (npc.getName().equalsIgnoreCase("The Nightmare") || npc.getName().equalsIgnoreCase("Phosani's Nightmare"))) {
            //reset everything
            reset();
            nm = npc;
            inFight = true;
        }

        if (!inFight || !npc.equals(nm)) {
            return;
        }

        int animationId = npc.getAnimation();

        switch (animationId) {
            case NIGHTMARE_MAGIC_ATTACK:
                activatePrayer(cursed ?
                        Prayer.PROTECT_FROM_MELEE :
                        Prayer.PROTECT_FROM_MAGIC);
                break;
            case NIGHTMARE_MELEE_ATTACK:
                activatePrayer(cursed ?
                        Prayer.PROTECT_FROM_MISSILES :
                        Prayer.PROTECT_FROM_MELEE);
                break;
            case NIGHTMARE_RANGE_ATTACK:
                activatePrayer(cursed ?
                        Prayer.PROTECT_FROM_MAGIC :
                        Prayer.PROTECT_FROM_MISSILES);
                break;
            default:
                break;
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
    private void onChatMessage(ChatMessage event) {
        if (!inFight || nm == null || event.getType() != ChatMessageType.GAMEMESSAGE) {
            return;
        }

        if (event.getMessage().toLowerCase().contains("the nightmare has cursed you, shuffling your prayers!")) {
            cursed = true;
        }

        if (event.getMessage().toLowerCase().contains("you feel the effects of the nightmare's curse wear off.")) {
            cursed = false;
        }
    }

    @Subscribe
    private void onGameTick(final GameTick event) {
        if (!inFight || nm == null) {
            return;
        }

        //the fight has ended and everything should be reset
        if (nm.getId() == NpcID.THE_NIGHTMARE || nm.getId() == NpcID.PHOSANIS_NIGHTMARE) {
            reset();
        }

        /*if (!client.isPrayerActive(prayerToClick)) {
            activatePrayer(prayerToClick);
        }*/
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

        prayerToClick = prayer;
    }
}