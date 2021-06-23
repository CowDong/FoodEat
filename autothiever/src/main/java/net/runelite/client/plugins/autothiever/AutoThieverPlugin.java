package net.runelite.client.plugins.autothiever;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.queries.NPCQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Extension
@PluginDescriptor(
        name = "Auto Thiever",
        description = "Automatically thieves npcs",
        tags = {"auto", "thiever", "thieving", "skill", "skilling"},
        enabledByDefault = false
)
public class AutoThieverPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private AutoThieverConfig config;

    @Inject
    private ItemManager itemManager;

    private MenuEntry entry;

    private Random r = new Random();

    private int nextOpenPouchCount;
    private boolean emptyPouches = false;

    private boolean pluginStarted = false;
    private int tickDelay = 0;

    @Provides
    AutoThieverConfig provideConfig(final ConfigManager configManager) {
        return configManager.getConfig(AutoThieverConfig.class);
    }

    @Override
    protected void startUp() {
        nextOpenPouchCount = getRandom(1, 28);
    }

    @Override
    protected void shutDown() {
    }

    @Subscribe
    public void onConfigButtonClicked(ConfigButtonClicked event) {
        if (!event.getGroup().equals("autothiever")) {
            return;
        }

        if (event.getKey().equals("startButton")) {
            pluginStarted = true;
            nextOpenPouchCount = getRandom(1, 28);
        } else if (event.getKey().equals("stopButton")) {
            pluginStarted = false;
        }
    }

    @Subscribe
    private void onChatMessage(ChatMessage event) {
        final String message = event.getMessage();

        if (event.getType() == ChatMessageType.SPAM) {
            if (message.startsWith("You pickpocket") || message.startsWith("You pick-pocket") || message.startsWith("You steal") || message.startsWith("You successfully pick-pocket") || message.startsWith("You successfully pick") || message.startsWith("You successfully steal") || message.startsWith("You pick the knight") || message.startsWith("You pick the Elf")) {
                tickDelay = 0;
            } else if (message.startsWith("You fail to pick") || message.startsWith("You fail to steal")) {
                tickDelay = getRandom(config.clickDelayMin(), config.clickDelayMax());
            } else if (message.startsWith("You open all of the pouches")) {
                emptyPouches = false;
                nextOpenPouchCount = getRandom(1, 28);
            }
        } else if (event.getType() == ChatMessageType.GAMEMESSAGE) {
            if (message.startsWith("You need to empty your")) {
                emptyPouches = true;
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (tickDelay > 0) {
            tickDelay--;
            return;
        }

        if (!pluginStarted)
            return;

        if (shouldEat()) {
            eat();
            return;
        }

        handleRandomPouchOpening();

        if (emptyPouches) {
            openPouches();
            return;
        }

        NPC npc = new NPCQuery()
                .idEquals(config.npcId())
                .result(client)
                .nearestTo(client.getLocalPlayer());

        if (npc == null) {
            return;
        }

        clientThread.invoke(() ->
                client.invokeMenuAction(
                        "Pickpocket",
                        "<col=ffff00>" + npc.getName() + "<col=ff00>  (level-" + npc.getCombatLevel() + ")",
                        npc.getIndex(),
                        MenuAction.NPC_THIRD_OPTION.getId(),
                        0,
                        0
                )
		);

        tickDelay = 1;
    }

    public void handleRandomPouchOpening() {
        WidgetItem item = getInventoryItem(ItemID.COIN_POUCH, ItemID.COIN_POUCH_22522, ItemID.COIN_POUCH_22523, ItemID.COIN_POUCH_22524,
                ItemID.COIN_POUCH_22525, ItemID.COIN_POUCH_22526, ItemID.COIN_POUCH_22527, ItemID.COIN_POUCH_22528,
                ItemID.COIN_POUCH_22529, ItemID.COIN_POUCH_22530, ItemID.COIN_POUCH_22531, ItemID.COIN_POUCH_22532,
                ItemID.COIN_POUCH_22533, ItemID.COIN_POUCH_22534, ItemID.COIN_POUCH_22535, ItemID.COIN_POUCH_22536,
                ItemID.COIN_POUCH_22537, ItemID.COIN_POUCH_22538);

        if (item == null) {
            return;
        }

        if (item.getQuantity() >= nextOpenPouchCount) {
            emptyPouches = true;
        }
    }

    public void openPouches() {
        WidgetItem item = getInventoryItem(ItemID.COIN_POUCH, ItemID.COIN_POUCH_22522, ItemID.COIN_POUCH_22523, ItemID.COIN_POUCH_22524,
                ItemID.COIN_POUCH_22525, ItemID.COIN_POUCH_22526, ItemID.COIN_POUCH_22527, ItemID.COIN_POUCH_22528,
                ItemID.COIN_POUCH_22529, ItemID.COIN_POUCH_22530, ItemID.COIN_POUCH_22531, ItemID.COIN_POUCH_22532,
                ItemID.COIN_POUCH_22533, ItemID.COIN_POUCH_22534, ItemID.COIN_POUCH_22535, ItemID.COIN_POUCH_22536,
                ItemID.COIN_POUCH_22537, ItemID.COIN_POUCH_22538);

        if (item == null) {
            return;
        }

        clientThread.invoke(() ->
				client.invokeMenuAction(
						"Open-all",
						"<col=ff9040>" + itemManager.getItemComposition(item.getId()).getName(),
						item.getId(),
						MenuAction.ITEM_FIRST_OPTION.getId(),
						item.getIndex(),
						WidgetInfo.INVENTORY.getId()
				)
		);

        tickDelay = 1;
    }

    public void eat() {
        Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);

        if (inventoryWidget == null) {
            return;
        }

        List<WidgetItem> list = inventoryWidget.getWidgetItems().stream().filter(item -> config.itemId() == item.getId()).collect(Collectors.toList());

        if (list.isEmpty()) {
            return;
        }

        WidgetItem item = list.get(0);

        if (item == null) {
            return;
        }

        clientThread.invoke(() ->
				client.invokeMenuAction(
						"Eat",
						"<col=ff9040>" + this.itemManager.getItemComposition(item.getId()).getName(),
						item.getId(),
						MenuAction.ITEM_FIRST_OPTION.getId(),
						item.getIndex(),
						WidgetInfo.INVENTORY.getId()
				)
		);

        tickDelay = 4;
    }

    public boolean shouldEat() {
        switch (config.hpCheckStyle()) {
            case EXACT_HEALTH:
                return client.getBoostedSkillLevel(Skill.HITPOINTS) <= config.hpToEat();

            case PERCENTAGE:
                return (((float) client.getBoostedSkillLevel(Skill.HITPOINTS) / (float) client.getRealSkillLevel(Skill.HITPOINTS)) * 100.f) <= (float) config.hpToEat();
        }

        return false;
    }

    public int getRandom(int min, int max) {
        return r.nextInt((max - min) + 1) + min;
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        if (entry != null) {
            event.setMenuEntry(entry);
        }

        entry = null;
    }

    public WidgetItem getInventoryItem(int... ids) {
        Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);

        if (inventoryWidget == null) {
            return null;
        }

        for (WidgetItem item : inventoryWidget.getWidgetItems()) {
            if (Arrays.stream(ids).anyMatch(i -> i == item.getId())) {
                return item;
            }
        }

        return null;
    }
}