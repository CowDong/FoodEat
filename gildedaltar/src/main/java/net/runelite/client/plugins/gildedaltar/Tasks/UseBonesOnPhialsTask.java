package net.runelite.client.plugins.gildedaltar.Tasks;

import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.queries.InventoryWidgetItemQuery;
import net.runelite.api.queries.NPCQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.plugins.gildedaltar.GildedAltarConfig;
import net.runelite.client.plugins.gildedaltar.GildedAltarPlugin;
import net.runelite.client.plugins.gildedaltar.MiscUtils;
import net.runelite.client.plugins.gildedaltar.Task;

public class UseBonesOnPhialsTask extends Task {
    public UseBonesOnPhialsTask(GildedAltarPlugin plugin, Client client, ClientThread clientThread, GildedAltarConfig config) {
        super(plugin, client, clientThread, config);
    }

    @Override
    public int getDelay() {
        return 4;
    }

    @Override
    public boolean validate() {
        //if inside house
        if (MiscUtils.isInPOH(client)) {
            return false;
        }

        QueryResults<NPC> results = new NPCQuery()
                .idEquals(NpcID.PHIALS)
                .result(client);

        if (results == null || results.isEmpty()) {
            return false;
        }

        Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);

        if (inventoryWidget == null) {
            return false;
        }

        if (inventoryWidget.getWidgetItems().stream().anyMatch(item -> item.getId() == config.boneId())) {
            return false;
        }

        QueryResults<WidgetItem> notedBonesQueryResults = new InventoryWidgetItemQuery()
                .idEquals(config.boneId() + 1)
                .result(client);

        if (notedBonesQueryResults == null || notedBonesQueryResults.isEmpty()) {
            return false;
        }

        Widget dialogueWidget = client.getWidget(WidgetInfo.DIALOG_OPTION_OPTIONS);

        if (dialogueWidget != null) {
            return false;
        }

        return true;
    }

    @Override
    public void onGameTick(GameTick event) {
        QueryResults<NPC> results = new NPCQuery()
                .idEquals(NpcID.PHIALS)
                .result(client);

        if (results == null || results.isEmpty()) {
            return;
        }

        NPC phials = results.first();

        if (phials == null) {
            return;
        }

        QueryResults<WidgetItem> widgetItemQueryResults = new InventoryWidgetItemQuery()
                .idEquals(config.boneId() + 1)
                .result(client);

        if (widgetItemQueryResults == null || widgetItemQueryResults.isEmpty()) {
            return;
        }

        WidgetItem notedBones = widgetItemQueryResults.first();

        if (notedBones == null) {
            return;
        }

        clientThread.invoke(() -> {
            client.setSelectedItemWidget(WidgetInfo.INVENTORY.getId());
            client.setSelectedItemSlot(notedBones.getIndex());
            client.setSelectedItemID(notedBones.getId());
            client.invokeMenuAction(
                    "Use",
                    "<col=00ffff>Bones -> Phials",
                    phials.getIndex(),
                    MenuAction.ITEM_USE_ON_NPC.getId(),
                    0,
                    0
            );
        });
    }
}
