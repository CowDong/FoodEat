package net.runelite.client.plugins.gildedaltar.Tasks;

import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.plugins.gildedaltar.GildedAltarConfig;
import net.runelite.client.plugins.gildedaltar.GildedAltarPlugin;
import net.runelite.client.plugins.gildedaltar.Task;

public class BreakTask extends Task {
    public BreakTask(GildedAltarPlugin plugin, Client client, ClientThread clientThread, GildedAltarConfig config) {
        super(plugin, client, clientThread, config);
    }

    @Override
    public int getDelay() {
        return 0;
    }

    @Override
    public boolean validate() {
        return plugin.chinBreakHandler.shouldBreak(plugin);
    }

    @Override
    public String getTaskDescription() {
        return "Taking a break";
    }

    @Override
    public void onGameTick(GameTick gameTick) {
        if (plugin.chinBreakHandler.shouldBreak(plugin)) {
            plugin.chinBreakHandler.startBreak(plugin);
        }
    }
}
