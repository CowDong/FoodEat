package net.runelite.client.plugins.constructionhelper;

import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;

import javax.inject.Inject;

public abstract class Task {
    public Task(ConstructionHelperPlugin plugin, Client client, ClientThread clientThread, ConstructionHelperConfig config) {
        this.plugin = plugin;
        this.client = client;
        this.clientThread = clientThread;
        this.config = config;
    }

    @Inject
    public ConstructionHelperPlugin plugin;

    @Inject
    public Client client;

    @Inject
    public ClientThread clientThread;

    @Inject
    public ConstructionHelperConfig config;

    public abstract int getDelay();

    public abstract boolean validate();

    public String getTaskDescription() {
        return this.getClass().getSimpleName();
    }

    public void onGameTick(GameTick event) { }
}
