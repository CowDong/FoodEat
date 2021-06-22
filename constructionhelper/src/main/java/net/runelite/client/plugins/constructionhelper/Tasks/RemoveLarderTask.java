package net.runelite.client.plugins.constructionhelper.Tasks;

import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.plugins.constructionhelper.ConstructionHelperConfig;
import net.runelite.client.plugins.constructionhelper.ConstructionHelperPlugin;
import net.runelite.client.plugins.constructionhelper.Task;

public class RemoveLarderTask extends Task {
    public RemoveLarderTask(ConstructionHelperPlugin plugin, Client client, ClientThread clientThread, ConstructionHelperConfig config) {
        super(plugin, client, clientThread, config);
    }

    @Override
    public int getDelay() {
        return 1;
    }

    @Override
    public boolean validate() {
        QueryResults<GameObject> gameObjects = new GameObjectQuery()
                .idEquals(config.mode().getObjectId())
                .result(client);

        if (!gameObjects.isEmpty()) {
            return true;
        }

        return false;
    }

    @Override
    public void onGameTick(GameTick event) {
        QueryResults<GameObject> gameObjects = new GameObjectQuery()
                .idEquals(config.mode().getObjectId())
                .result(client);

        if (gameObjects == null || gameObjects.isEmpty())
            return;

        GameObject builtObject = gameObjects.first();

        if (builtObject == null)
            return;

        clientThread.invoke(() ->
                client.invokeMenuAction(
                        "Remove",
                        "",
                        config.mode().getObjectId(),
                        MenuAction.GAME_OBJECT_FIFTH_OPTION.getId(),
                        builtObject.getSceneMinLocation().getX(),
                        builtObject.getSceneMinLocation().getY()
                )
        );
    }
}
