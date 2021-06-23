package net.runelite.client.plugins.foodeater;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
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

@Extension
@PluginDescriptor(
        name = "Food Eater",
        description = "Automatically eats food",
        tags = {"combat", "notifications", "health", "food", "eat"},
        enabledByDefault = false
)
public class FoodEaterPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ItemManager itemManager;

    @Inject
    private FoodEaterConfig config;

    @Provides
    FoodEaterConfig provideConfig(final ConfigManager configManager) {
        return configManager.getConfig(FoodEaterConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
    }

    @Override
    protected void shutDown() throws Exception {
    }

    @Subscribe
    public void onGameTick(final GameTick event) {
        try {
            int health = this.client.getBoostedSkillLevel(Skill.HITPOINTS);

            if (health > this.config.minimumHealth()) {
                return;
            }

            Widget inventory = client.getWidget(WidgetInfo.INVENTORY);

            if (inventory == null) {
                return;
            }

            if (client.getItemContainer(InventoryID.BANK) != null) {
                return;
            }

            for (WidgetItem item : inventory.getWidgetItems()) {
                final String name = this.itemManager.getItemComposition(item.getId()).getName();

                if (name.equalsIgnoreCase(this.config.foodToEat())) {
                    MenuEntry entry = getConsumableEntry(name, item.getId(), item.getIndex());
                    clientThread.invoke(() ->
                            client.invokeMenuAction(
                                    entry.getOption(),
                                    entry.getTarget(),
                                    entry.getIdentifier(),
                                    entry.getOpcode(),
                                    entry.getParam0(),
                                    entry.getParam1()
                            )
                    );
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MenuEntry getConsumableEntry(String itemName, int itemId, int itemIndex) {
        return new MenuEntry("Eat", "<col=ff9040>" + itemName, itemId, MenuAction.ITEM_FIRST_OPTION.getId(), itemIndex, WidgetInfo.INVENTORY.getId(), false);
    }
}
