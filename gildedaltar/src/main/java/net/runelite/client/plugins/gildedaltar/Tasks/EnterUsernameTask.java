package net.runelite.client.plugins.gildedaltar.Tasks;

import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.plugins.gildedaltar.GildedAltarConfig;
import net.runelite.client.plugins.gildedaltar.GildedAltarPlugin;
import net.runelite.client.plugins.gildedaltar.MiscUtils;
import net.runelite.client.plugins.gildedaltar.Task;

public class EnterUsernameTask extends Task {
    public EnterUsernameTask(GildedAltarPlugin plugin, Client client, ClientThread clientThread, GildedAltarConfig config) {
        super(plugin, client, clientThread, config);
    }

    @Override
    public int getDelay() {
        return 0;
    }

    @Override
    public boolean validate() {
        //not in house
        if (MiscUtils.isInPOH(client))
            return false;

        Widget widget = client.getWidget(WidgetInfo.CHATBOX_TITLE);

        if (widget == null || widget.isHidden())
            return false;

        if (widget.getText().equals("Enter name:"))
            return true;

        return false;
    }

    @Override
    public String getTaskDescription() {
        return "Enter host name";
    }

    @Override
    public void onGameTick(GameTick event) {
        client.setVar(VarClientInt.INPUT_TYPE, 8);
        client.setVar(VarClientStr.INPUT_TEXT, String.valueOf(config.hostName()));
        client.runScript(681);
        //client.runScript(ScriptID.MESSAGE_LAYER_CLOSE, 0, 1, 1);
    }
}
