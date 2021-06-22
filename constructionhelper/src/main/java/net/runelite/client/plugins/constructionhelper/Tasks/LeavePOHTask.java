package net.runelite.client.plugins.constructionhelper.Tasks;

import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.plugins.constructionhelper.ConstructionHelperConfig;
import net.runelite.client.plugins.constructionhelper.ConstructionHelperPlugin;
import net.runelite.client.plugins.constructionhelper.Task;

import java.util.Arrays;
import java.util.List;

public class LeavePOHTask extends Task {
    public LeavePOHTask(ConstructionHelperPlugin plugin, Client client, ClientThread clientThread, ConstructionHelperConfig config) {
        super(plugin, client, clientThread, config);
    }

    @Override
    public int getDelay() {
        return 2;
    }

    List<Integer> regions = Arrays.asList(7513, 7514, 7769, 7770);

    @Override
    public boolean validate() {

        //if (not inside house)
        if (Arrays.stream(client.getMapRegions()).noneMatch(r -> regions.contains(r))) {
            return false;
        }

        Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);

        if (inventoryWidget == null) {
            return false;
        }

        if (inventoryWidget.getWidgetItems().stream().filter(item -> item.getId() == config.mode().getPlankId()).count() >= config.mode().getPlankCost()) {
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
