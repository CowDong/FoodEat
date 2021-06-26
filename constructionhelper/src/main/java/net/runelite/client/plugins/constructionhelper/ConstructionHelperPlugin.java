package net.runelite.client.plugins.constructionhelper;

import com.google.inject.Provides;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.constructionhelper.Tasks.*;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
@Extension
@PluginDescriptor(
        name = "Construction Helper",
        description = "Automated construction (via phials unnoting)",
        tags = {"construction", "helper", "ben93riggs", "con"},
        enabledByDefault = false
)
public class ConstructionHelperPlugin extends Plugin {
    static List<Class<?>> taskClassList = new ArrayList<>();

    static {
        taskClassList.add(RemoveDialogueTask.class);
        taskClassList.add(CraftOakLarderTask.class);
        taskClassList.add(RemoveLarderTask.class);
        taskClassList.add(BuildLarderTask.class);
        taskClassList.add(LeavePOHTask.class);
        taskClassList.add(UsePlankOnPhialsTask.class);
        taskClassList.add(EnterPOHTask.class);
        taskClassList.add(PhialsDialogueTask.class);

        taskClassList.add(StopConditionTask.class);
    }

    @Inject
    public Client client;

    @Inject
    public ConstructionHelperConfig config;

    @Inject
    public ClientThread clientThread;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ConstructionHelperOverlay overlay;

    @Inject
    private ChatMessageManager chatMessageManager;

    boolean pluginStarted = false;

    @Provides
    ConstructionHelperConfig provideConfig(final ConfigManager configManager) {
        return configManager.getConfig(ConstructionHelperConfig.class);
    }

    public String status = "initializing...";

    private final TaskSet tasks = new TaskSet();

    public int delay = 0;

    @Override
    protected void startUp() throws Exception {
        pluginStarted = false;
        status = "initializing...";
        overlayManager.add(overlay);
        tasks.clear();
        tasks.addAll(this, client, clientThread, config, taskClassList);
    }

    @Override
    protected void shutDown() throws Exception {
        pluginStarted = false;
        overlayManager.remove(overlay);
        tasks.clear();
    }



    @Subscribe
    public void onConfigButtonClicked(ConfigButtonClicked event) {
        if (!event.getGroup().equals("constructionhelper")) {
            return;
        }

        if (event.getKey().equals("startButton")) {
            pluginStarted = true;
        } else if (event.getKey().equals("stopButton")) {
            pluginStarted = false;
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!pluginStarted) {
            return;
        }

        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        if (delay > 0) {
            delay--;
            return;
        }

        Task task = tasks.getValidTask();

        if (task != null) {
            status = task.getTaskDescription();
            task.onGameTick(event);
            delay = task.getDelay() + getRandomWait();
        }
    }

    public int getRandomWait() {
        return (int) ((Math.random() * (config.maxWaitTicks() - config.minWaitTicks())) + config.minWaitTicks());
    }

    private void sendGameMessage(String message) {
        chatMessageManager
                .queue(QueuedMessage.builder()
                        .type(ChatMessageType.CONSOLE)
                        .runeLiteFormattedMessage(
                                new ChatMessageBuilder()
                                        .append(ChatColorType.HIGHLIGHT)
                                        .append(message)
                                        .build())
                        .build());
    }

    public void stopPlugin() {
        stopPlugin("");
    }

    public void stopPlugin(String reason) {
        pluginStarted = false;

        if (reason != null && !reason.isEmpty())
            sendGameMessage("ConstructionHelper Stopped: " + reason);
    }
}