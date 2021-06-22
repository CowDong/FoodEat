package net.runelite.client.plugins.nmzhelper;

import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;

import javax.inject.Inject;

public abstract class Task {
    public Task(NMZHelperPlugin plugin, Client client, NMZHelperConfig config) {
        this.plugin = plugin;
        this.client = client;
        this.config = config;
    }

    @Inject
    public NMZHelperPlugin plugin;

    @Inject
    public Client client;

    @Inject
    public ClientThread clientThread;

    @Inject
    public NMZHelperConfig config;

    public abstract boolean validate();

    public String getTaskDescription() {
        return this.getClass().getSimpleName();
    }

    public void onGameTick(GameTick event) {
    }
}
