package net.runelite.client.plugins.constructionhelper.Tasks;

import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.QueryResults;
import net.runelite.api.events.GameTick;
import net.runelite.api.queries.InventoryWidgetItemQuery;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.plugins.constructionhelper.ConstructionHelperConfig;
import net.runelite.client.plugins.constructionhelper.ConstructionHelperPlugin;
import net.runelite.client.plugins.constructionhelper.Task;

public class StopConditionTask extends Task {
    public StopConditionTask(ConstructionHelperPlugin plugin, Client client, ClientThread clientThread, ConstructionHelperConfig config) {
        super(plugin, client, clientThread, config);
    }

    @Override
    public int getDelay() {
        return 0;
    }

    @Override
    public boolean validate() {
        QueryResults<WidgetItem> sawResults = new InventoryWidgetItemQuery()
                .idEquals(ItemID.SAW, ItemID.CRYSTAL_SAW)
                .result(client);

        if (sawResults == null || sawResults.isEmpty()) {
            return true;
        }

        WidgetItem sawWidget = sawResults.first();

        if (sawWidget == null) {
            return true;
        }

        QueryResults<WidgetItem> hammerResults = new InventoryWidgetItemQuery()
                .idEquals(ItemID.HAMMER)
                .result(client);

        if (hammerResults == null || hammerResults.isEmpty()) {
            return true;
        }

        WidgetItem hammerWidget = sawResults.first();

        if (hammerWidget == null) {
            return true;
        }

        for (int req : config.mode().getOtherReqs()) {
            QueryResults<WidgetItem> reqResults = new InventoryWidgetItemQuery()
                    .idEquals(req)
                    .result(client);

            if (reqResults == null || reqResults.isEmpty()) {
                return true;
            }

            WidgetItem reqWidget = reqResults.first();

            if (reqWidget == null) {
                return true;
            }
        }

        QueryResults<WidgetItem> results = new InventoryWidgetItemQuery()
                .idEquals(config.mode().getPlankId() + 1) //noted plank is plankId + 1
                .result(client);

        if (results == null || results.isEmpty()) {
            return true;
        }

        WidgetItem notedPlanks = results.first();

        if (notedPlanks == null) {
            return true;
        }

        if (notedPlanks.getQuantity() < config.mode().getPlankCost()) {
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

        QueryResults<WidgetItem> sawResults = new InventoryWidgetItemQuery()
                .idEquals(ItemID.SAW, ItemID.CRYSTAL_SAW)
                .result(client);

        if (sawResults == null || sawResults.isEmpty()) {
            plugin.stopPlugin("Saw not found. (query)");
            return;
        }

        WidgetItem sawWidget = sawResults.first();

        if (sawWidget == null) {
            plugin.stopPlugin("Saw not found. (first)");
            return;
        }

        QueryResults<WidgetItem> hammerResults = new InventoryWidgetItemQuery()
                .idEquals(ItemID.HAMMER)
                .result(client);

        if (hammerResults == null || hammerResults.isEmpty()) {
            plugin.stopPlugin("Hammer not found. (query)");
            return;
        }

        WidgetItem hammerWidget = hammerResults.first();

        if (hammerWidget == null) {
            plugin.stopPlugin("Hammer not found. (first)");
            return;
        }

        for (int req : config.mode().getOtherReqs()) {
            QueryResults<WidgetItem> reqResults = new InventoryWidgetItemQuery()
                    .idEquals(req)
                    .result(client);

            if (reqResults == null || reqResults.isEmpty()) {
                plugin.stopPlugin("Missing requirement (query): " + client.getItemDefinition(req).getName());
                return;
            }

            WidgetItem reqWidget = reqResults.first();

            if (reqWidget == null) {
                plugin.stopPlugin("Missing requirement (first): " + client.getItemDefinition(req).getName());
                return;
            }
        }

        QueryResults<WidgetItem> results = new InventoryWidgetItemQuery()
                .idEquals(config.mode().getPlankId() + 1) //noted plank is plankId + 1
                .result(client);

        if (results == null || results.isEmpty()) {
            plugin.stopPlugin("Out of noted planks. (first)");
            return;
        }

        WidgetItem notedPlanks = results.first();

        if (notedPlanks == null) {
            plugin.stopPlugin("Out of noted planks. (query)");
            return;
        }

        if (notedPlanks.getQuantity() < config.mode().getPlankCost()) {
            plugin.stopPlugin("Less noted planks than required for crafting target object.");
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
