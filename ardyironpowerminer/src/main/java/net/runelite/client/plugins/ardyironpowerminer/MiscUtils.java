package net.runelite.client.plugins.ardyironpowerminer;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

import java.util.Random;

public class MiscUtils {

    private static final Random r = new Random();
    private static int dropAt = r.nextInt((27 - 3) + 1) + 3;

    public static boolean isInventoryFull(Client client) {
        Widget inventory = client.getWidget(WidgetInfo.INVENTORY);

        if (inventory == null) {
            return false;
        }

        if (inventory.getWidgetItems().size() > dropAt) {
            dropAt = r.nextInt((27 - 3) + 1) + 3;
            return true;
        }

        return false;
    }

    public static boolean isMining(Client client) {
        Player localPlayer = client.getLocalPlayer();

        if (localPlayer == null) {
            return false;
        }

        if (localPlayer.getAnimation() != -1) {
            return true;
        }

        if (localPlayer.getPoseAnimation() != 808) {
            return true;
        }

        return false;
    }

    public static GameObject findNearestGameObjectWithin(Client client, WorldPoint worldPoint, int dist, int id) {
        if (client.getLocalPlayer() == null) {
            return null;
        }

        return new GameObjectQuery()
                .idEquals(id)
                .isWithinDistance(worldPoint, dist)
                .result(client)
                .nearestTo(client.getLocalPlayer());
    }
}
