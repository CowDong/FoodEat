package net.runelite.client.plugins.gildedaltar.Tasks;

import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.queries.NPCQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.plugins.gildedaltar.GildedAltarConfig;
import net.runelite.client.plugins.gildedaltar.GildedAltarPlugin;
import net.runelite.client.plugins.gildedaltar.MiscUtils;
import net.runelite.client.plugins.gildedaltar.Task;

import java.util.Arrays;

public class PhialsDialogueTask extends Task {

    public PhialsDialogueTask(GildedAltarPlugin plugin, Client client, ClientThread clientThread, GildedAltarConfig config) {
        super(plugin, client, clientThread, config);
    }

    @Override
    public int getDelay() {
        return 0;
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

        Widget dialogueWidget = client.getWidget(WidgetInfo.DIALOG_OPTION_OPTIONS);

        if (dialogueWidget == null) {
            return false;
        }

        Widget[] children = dialogueWidget.getChildren();

        if (children == null) {
            return false;
        }

        if (Arrays.stream(children).noneMatch(w -> w.getText().contains("Exchange All"))) {
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

        Widget dialogueWidget = client.getWidget(WidgetInfo.DIALOG_OPTION_OPTIONS);

        if (dialogueWidget == null) {
            return;
        }

        Widget[] children = dialogueWidget.getChildren();

        if (children != null) {
            if (Arrays.stream(children).anyMatch(w -> w.getText().contains("Exchange All"))) {
                clientThread.invoke(() ->
                        client.invokeMenuAction("Continue", "",
                                0,
                                MenuAction.WIDGET_TYPE_6.getId(),
                                children[3].getIndex(),
                                children[3].getId()
                        )
                );
            }
        }
    }
}
