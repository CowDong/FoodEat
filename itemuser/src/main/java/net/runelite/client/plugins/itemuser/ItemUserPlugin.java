package net.runelite.client.plugins.itemuser;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameTick;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.api.queries.InventoryWidgetItemQuery;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

import javax.annotation.Nullable;
import javax.inject.Inject;

@Extension
@PluginDescriptor(
        name = "Item User",
        description = "Automatically uses items on an object",
        tags = {"skilling", "item", "object", "user"},
        enabledByDefault = false
)
public class ItemUserPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ConfigManager configManager;

    @Inject
    ItemUserConfig config;

    private boolean pluginStarted;

    private int delay;

    @Override
    protected void startUp() throws Exception {
        pluginStarted = false;
        delay = 0;
    }

    @Override
    protected void shutDown() throws Exception {
        pluginStarted = false;
        delay = 0;
    }

    @Subscribe
    private void onGameTick(final GameTick event) {
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

            GameObject object = findNearestGameObject(config.objectId());

            if (object == null) {
                return;
            }

            client.setSelectedItemWidget(WidgetInfo.INVENTORY.getId());
            client.setSelectedItemSlot(firstItem.getIndex());
            client.setSelectedItemID(firstItem.getId());
            client.invokeMenuAction(
                    "Use",
                    "<col=ff9040>Item<col=ffffff> -> <col=ffff>GameObject",
                    object.getId(),
                    MenuAction.ITEM_USE_ON_GAME_OBJECT.getId(),
                    object.getSceneMinLocation().getX(),
                    object.getSceneMinLocation().getY()
            );

        });

        delay = getRandomWait();
    }

    @Nullable
    public GameObject findNearestGameObject(int... ids) {
        assert client.isClientThread();

        if (client.getLocalPlayer() == null) {
            return null;
        }

        return new GameObjectQuery()
                .idEquals(ids)
                .result(client)
                .nearestTo(client.getLocalPlayer());
    }

    @Provides
    ItemUserConfig provideConfig(final ConfigManager configManager) {
        return configManager.getConfig(ItemUserConfig.class);
    }

    @Subscribe
    public void onConfigButtonClicked(ConfigButtonClicked event) {
        if (!event.getGroup().equals("itemuser")) {
            return;
        }

        if (event.getKey().equals("startButton")) {
            pluginStarted = true;
        } else if (event.getKey().equals("stopButton")) {
            pluginStarted = false;
        }
    }

    public int getRandomWait() {
        return (int) ((Math.random() * (config.waitMax() - config.waitMin())) + config.waitMin());
    }
}
