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

public class EnterPOHTask extends Task {
    public EnterPOHTask(GildedAltarPlugin plugin, Client client, ClientThread clientThread, GildedAltarConfig config) {
        super(plugin, client, clientThread, config);
    }

    @Override
    public int getDelay() {
        return 3;
    }

    @Override
    public boolean validate() {
        //if inside house
        if (MiscUtils.isInPOH(client)) {
            return false;
        }

        Widget widget = client.getWidget(WidgetInfo.CHATBOX_TITLE);

        if (widget != null && !widget.isHidden()) {
            if (widget.getText().equals("Enter name:")) {
                return false;
            }
        }

        Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);

        if (inventoryWidget == null) {
            return false;
        }

        if (inventoryWidget.getWidgetItems().stream().noneMatch(item -> item.getId() == config.boneId())) {
            return false;
        }

        QueryResults<GameObject> objectQueryResults = new GameObjectQuery()
                .nameEquals("Portal")
                .result(client);

        if (objectQueryResults == null || objectQueryResults.isEmpty()) {
            return false;
        }

        GameObject portalObject = objectQueryResults.first();

        if (portalObject == null) {
            return false;
        }

        return true;
    }

    @Override
    public void onGameTick(GameTick event) {
        QueryResults<GameObject> objectQueryResults = new GameObjectQuery()
                .nameEquals("Portal")
                .result(client);

        if (objectQueryResults == null || objectQueryResults.isEmpty()) {
            return;
        }

        GameObject portalObject = objectQueryResults.first();

        if (portalObject == null) {
            return;
        }

        clientThread.invoke(() ->
                client.invokeMenuAction(
                        "Build mode",
                        "<col=ffff>Portal",
                        portalObject.getId(),
                        MenuAction.GAME_OBJECT_FOURTH_OPTION.getId(),
                        portalObject.getSceneMinLocation().getX(),
                        portalObject.getSceneMinLocation().getY()
                )
        );
    }
}