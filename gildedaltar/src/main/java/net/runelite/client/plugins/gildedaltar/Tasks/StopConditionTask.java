package net.runelite.client.plugins.gildedaltar.Tasks;

import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.QueryResults;
import net.runelite.api.events.GameTick;
import net.runelite.api.queries.InventoryWidgetItemQuery;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.plugins.gildedaltar.GildedAltarConfig;
import net.runelite.client.plugins.gildedaltar.GildedAltarPlugin;
import net.runelite.client.plugins.gildedaltar.Task;

public class StopConditionTask extends Task {
    public StopConditionTask(GildedAltarPlugin plugin, Client client, ClientThread clientThread, GildedAltarConfig config) {
        super(plugin, client, clientThread, config);
    }

    @Override
    public int getDelay() {
        return 0;
    }

    @Override
    public boolean validate() {
        QueryResults<WidgetItem> results = new InventoryWidgetItemQuery()
                .idEquals(config.boneId() + 1) //noted bone is boneID + 1
                .result(client);

        if (results == null || results.isEmpty()) {
            return true;
        }

        WidgetItem notedBones = results.first();

        if (notedBones == null) {
            return true;
        }

        QueryResults<WidgetItem> gpResults = new InventoryWidgetItemQuery()
                .idEquals(ItemID.COINS_995)
                .result(client);

        if (gpResults == null || gpResults.isEmpty()) {
            return true;
        }

        WidgetItem gp = gpResults.first();

        if (gp == null) {
            return true;
        }

        if (gp.getQuantity() < 1000) {
            return true;
        }

        return false;
    }

    @Override
    public void onGameTick(GameTick event) {

        QueryResults<WidgetItem> results = new InventoryWidgetItemQuery()
                .idEquals(config.boneId() + 1) //noted bone is boneId + 1
                .result(client);

        if (results == null || results.isEmpty()) {
            plugin.stopPlugin("Out of bones. (first)");
            return;
        }

        WidgetItem notedBones = results.first();

        if (notedBones == null) {
            plugin.stopPlugin("Out of bones. (query)");
            return;
        }

        QueryResults<WidgetItem> gpResults = new InventoryWidgetItemQuery()
                .idEquals(ItemID.COINS_995)
                .result(client);

        if (gpResults == null || gpResults.isEmpty()) {
            plugin.stopPlugin("GP not found. (query)");
            return;
        }

        WidgetItem gp = gpResults.first();

        if (gp == null) {
            plugin.stopPlugin("GP not found (first)");
            return;
        }

        if (gp.getQuantity() < 1000) {
            plugin.stopPlugin("GP < 1000");
            return;
        }

        plugin.stopPlugin("Stop condition met (unspecified).");
    }
}
