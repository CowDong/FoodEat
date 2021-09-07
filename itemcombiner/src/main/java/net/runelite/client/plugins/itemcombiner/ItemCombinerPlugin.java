package net.runelite.client.plugins.itemcombiner;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.QueryResults;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.queries.InventoryWidgetItemQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

import javax.inject.Inject;

@Extension
@PluginDescriptor(
        name = "Item Combiner",
        description = "Automatically uses items on another item",
        tags = {"skilling", "item", "object", "combiner"},
        enabledByDefault = false
)
public class ItemCombinerPlugin extends Plugin {

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ConfigManager configManager;

    @Inject
    ItemCombinerConfig config;

    @Inject
    private ItemManager itemManager;

    private boolean pluginStarted;
    private int delay;
    private int iterations;

    @Provides
    ItemCombinerConfig provideConfig(final ConfigManager configManager) {
        return configManager.getConfig(ItemCombinerConfig.class);
    }

    @Override
    protected void startUp() {
        delay = 0;
        iterations = 0;
        pluginStarted = false;
    }

    @Override
    protected void shutDown() {
        delay = 0;
        iterations = 0;
        pluginStarted = false;
    }

    @Subscribe
    public void onClientTick(ClientTick event) {
        if (!pluginStarted) {
            return;
        }

        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        if (delay > 0) {
            delay--;
            return;
        }

        if (iterations < 1) {
            return;
        }

        clientThread.invoke(() -> {

            QueryResults<WidgetItem> widgetItemQueryResults = new InventoryWidgetItemQuery()
                    .idEquals(config.itemId())
                    .result(client);

            if (widgetItemQueryResults == null || widgetItemQueryResults.isEmpty()) {
                return;
            }

            WidgetItem firstItem = widgetItemQueryResults.first();

            if (firstItem == null) {
                return;
            }

            QueryResults<WidgetItem> widgetItem2QueryResults = new InventoryWidgetItemQuery()
                    .idEquals(config.itemId2())
                    .result(client);

            if (widgetItem2QueryResults == null || widgetItem2QueryResults.isEmpty()) {
                return;
            }

            WidgetItem secondItem = widgetItem2QueryResults.first();

            if (secondItem == null) {
                return;
            }

            client.setSelectedItemWidget(WidgetInfo.INVENTORY.getId());
            client.setSelectedItemSlot(firstItem.getIndex());
            client.setSelectedItemID(firstItem.getId());
            client.invokeMenuAction(
                    "Use",
                    "<col=ff9040>" + itemManager.getItemComposition(firstItem.getId()).getName() + "<col=ffffff> -> <col=ff9040>" + itemManager.getItemComposition(secondItem.getId()).getName(),
                    secondItem.getId(),
                    MenuAction.ITEM_USE_ON_WIDGET_ITEM.getId(),
                    secondItem.getIndex(),
                    WidgetInfo.INVENTORY.getId()
            );
        });

        delay = getRandomWait();
    }

    @Subscribe
    public void onConfigButtonClicked(ConfigButtonClicked event) {
        if (!event.getGroup().equals(ItemCombinerConfig.class.getAnnotation(ConfigGroup.class).value())) {
            return;
        }

        if (event.getKey().equals("startButton")) {
            pluginStarted = true;
            iterations = config.iterations();
        } else if (event.getKey().equals("stopButton")) {
            pluginStarted = false;
        }
    }

    public int getRandomWait() {
        return (int) ((Math.random() * (config.waitMax() - config.waitMin())) + config.waitMin());
    }
}
