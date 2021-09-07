package net.runelite.client.plugins.gildedaltar.Tasks;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.MenuAction;
import net.runelite.api.QueryResults;
import net.runelite.api.events.GameTick;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.plugins.gildedaltar.GildedAltarConfig;
import net.runelite.client.plugins.gildedaltar.GildedAltarPlugin;
import net.runelite.client.plugins.gildedaltar.MiscUtils;
import net.runelite.client.plugins.gildedaltar.Task;

public class LeavePOHTask extends Task {
    public LeavePOHTask(GildedAltarPlugin plugin, Client client, ClientThread clientThread, GildedAltarConfig config) {
        super(plugin, client, clientThread, config);
    }

    @Override
    public int getDelay() {
        return 2;
    }

    @Override
    public boolean validate() {

        if (!MiscUtils.isInPOH(client)) {
            return false;
        }

        Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);

        if (inventoryWidget == null) {
            return false;
        }

        if (inventoryWidget.getWidgetItems().stream().anyMatch(item -> item.getId() == config.boneId())) {
            return false;
        }

        return true;
    }

    @Override
    public void onGameTick(GameTick event) {
        QueryResults<GameObject> results = new GameObjectQuery()
                .nameEquals("Portal")
                .result(client);

        if (results == null || results.isEmpty()) {
            return;
        }

        GameObject portalObject = results.first();

        if (portalObject == null) {
            return;
        }

        clientThread.invoke(() ->
                client.invokeMenuAction(
                        "Enter",
                        "<col=ffff>Portal",
                        portalObject.getId(),
                        MenuAction.GAME_OBJECT_FIRST_OPTION.getId(),
                        portalObject.getSceneMinLocation().getX(),
                        portalObject.getSceneMinLocation().getY()
                )
        );
    }
}
