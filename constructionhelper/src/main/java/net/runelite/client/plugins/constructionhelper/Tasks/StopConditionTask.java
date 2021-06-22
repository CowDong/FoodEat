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
        plugin.stopPlugin("Stop condition met!");
    }
}
