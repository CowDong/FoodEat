package net.runelite.client.plugins.gildedaltar;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectID;
import net.runelite.api.queries.GameObjectQuery;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class MiscUtils {
    private static final List<Integer> regions = Arrays.asList(7513, 7514, 7769, 7770);

    public static boolean isInPOH(Client client) {
        //if inside house
        return Arrays.stream(client.getMapRegions()).anyMatch(regions::contains);
    }

    @Nullable
    public static GameObject findNearestGameObject(Client client, String name) {
        assert client.isClientThread();

        if (client.getLocalPlayer() == null) {
            return null;
        }

        return new GameObjectQuery()
                .filter(o -> o.getName().contains(name))
                .result(client)
                .nearestTo(client.getLocalPlayer());
    }
}
