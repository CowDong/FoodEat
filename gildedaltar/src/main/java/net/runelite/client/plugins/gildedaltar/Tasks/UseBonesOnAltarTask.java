package net.runelite.client.plugins.gildedaltar.Tasks;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.MenuAction;
import net.runelite.api.QueryResults;
import net.runelite.api.events.GameTick;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.api.queries.InventoryWidgetItemQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.plugins.gildedaltar.GildedAltarConfig;
import net.runelite.client.plugins.gildedaltar.GildedAltarPlugin;
import net.runelite.client.plugins.gildedaltar.MiscUtils;
import net.runelite.client.plugins.gildedaltar.Task;

public class UseBonesOnAltarTask extends Task {
    public UseBonesOnAltarTask(GildedAltarPlugin plugin, Client client, ClientThread clientThread, GildedAltarConfig config) {
        super(plugin, client, clientThread, config);
    }

    @Override
    public int getDelay() {
        return 0;
    }

    @Override
    public boolean validate() {
        //if inside house
        if (!MiscUtils.isInPOH(client)) {
            return false;
        }

        Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);

        if (inventoryWidget == null) {
            return false;
        }

        if (inventoryWidget.getWidgetItems()
                .stream().noneMatch(item -> item.getId() == config.boneId())) {
            return false;
        }

        GameObject gameObject = MiscUtils.findNearestGameObject(client, "Altar");

        if (gameObject == null) {
            return false;
        }

        return true;
    }

    @Override
    public void onGameTick(GameTick event) {
        QueryResults<GameObject> gameObjects = new GameObjectQuery()
                .filter(o -> o.getName().contains("Altar"))
                .result(client);

        if (gameObjects == null || gameObjects.isEmpty()) {
            return;
        }

        GameObject altarObject = gameObjects.first();

        if (altarObject == null) {
            return;
        }

        clientThread.invoke(() -> {
                    QueryResults<WidgetItem> widgetItemQueryResults = new InventoryWidgetItemQuery()
                            .idEquals(config.boneId())
                            .result(client);

                    if (widgetItemQueryResults == null || widgetItemQueryResults.isEmpty()) {
                        return;
                    }

                    WidgetItem firstItem = widgetItemQueryResults.first();

                    if (firstItem == null) {
                        return;
                    }

                    GameObject object = MiscUtils.findNearestGameObject(client, "Altar");

                    if (object == null) {
                        return;
                    }

                    client.setSelectedItemWidget(WidgetInfo.INVENTORY.getId());
                    client.setSelectedItemSlot(firstItem.getIndex());
                    client.setSelectedItemID(firstItem.getId());
                    client.invokeMenuAction(
                            "Offer",
                            "",
                            altarObject.getId(),
                            MenuAction.ITEM_USE_ON_GAME_OBJECT.getId(),
                            altarObject.getSceneMinLocation().getX(),
                            altarObject.getSceneMinLocation().getY()
                    );
                }
        );
    }


}
