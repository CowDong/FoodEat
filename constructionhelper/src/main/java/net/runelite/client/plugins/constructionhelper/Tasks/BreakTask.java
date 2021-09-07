package net.runelite.client.plugins.constructionhelper.Tasks;

import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.plugins.constructionhelper.ConstructionHelperConfig;
import net.runelite.client.plugins.constructionhelper.ConstructionHelperPlugin;
import net.runelite.client.plugins.constructionhelper.Task;

public class BreakTask extends Task {
    public BreakTask(ConstructionHelperPlugin plugin, Client client, ClientThread clientThread, ConstructionHelperConfig config) {
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
