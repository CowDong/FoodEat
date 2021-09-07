package net.runelite.client.plugins.nmzhelper.Tasks;

import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.plugins.nmzhelper.MiscUtils;
import net.runelite.client.plugins.nmzhelper.NMZHelperConfig;
import net.runelite.client.plugins.nmzhelper.NMZHelperPlugin;
import net.runelite.client.plugins.nmzhelper.Task;

public class BreakTask extends Task {
    public BreakTask(NMZHelperPlugin plugin, Client client, ClientThread clientThread, NMZHelperConfig config) {
        super(plugin, client, clientThread, config);
    }

    @Override
    public boolean validate() {
        if (MiscUtils.isInNightmareZone(client)) {
            return false;
        }

        if (!plugin.chinBreakHandler.shouldBreak(plugin)) {
           return false;
        }

        return true;
    }

    @Override
    public String getTaskDescription() {
        return "Taking a break";
    }

    @Override
    public void onGameTick(GameTick gameTick) {
        if (!MiscUtils.isInNightmareZone(client)) {
            if (plugin.chinBreakHandler.shouldBreak(plugin)) {
                plugin.chinBreakHandler.startBreak(plugin);
            }
        }
    }
}
