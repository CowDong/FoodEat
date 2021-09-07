package net.runelite.client.plugins.gildedaltar;

import javax.inject.Inject;
import com.google.inject.Provides;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.gildedaltar.Tasks.*;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import java.util.ArrayList;
import java.util.List;

@Extension
@PluginDescriptor(
        name = "Gilded Altar",
        description = "",
        tags = {"ben", "ben93riggs", "93", "riggs", "prayer", "poh", "gilded", "altar"},
        enabledByDefault = false
)
public class GildedAltarPlugin extends Plugin {
    static List<Class<?>> taskClassList = new ArrayList<>();

    static {
        taskClassList.add(ResetIdleTask.class);
        taskClassList.add(BreakTask.class);
        taskClassList.add(StopConditionTask.class);
        taskClassList.add(ToggleRunTask.class);
        taskClassList.add(UseBonesOnAltarTask.class);
        taskClassList.add(LeavePOHTask.class);
        taskClassList.add(UseBonesOnPhialsTask.class);
        taskClassList.add(EnterPOHTask.class);
        taskClassList.add(EnterUsernameTask.class);
        taskClassList.add(PhialsDialogueTask.class);
    }

    @Inject
    private Client client;

    @Inject
    public ClientThread clientThread;

    @Inject
    private GildedAltarConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private GildedAltarOverlay overlay;

    @Inject
    private ChatMessageManager chatMessageManager;

    @Inject
    public ReflectBreakHandler chinBreakHandler;

    boolean pluginStarted = false;
    public String status = "initializing...";
    private final TaskSet tasks = new TaskSet();
    public int delay = 0;

    @Provides
    GildedAltarConfig provideConfig(final ConfigManager configManager) {
        return configManager.getConfig(GildedAltarConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        pluginStarted = false;
        chinBreakHandler.registerPlugin(this);
        status = "initializing...";
        overlayManager.add(overlay);
        tasks.clear();
        tasks.addAll(this, client, clientThread, config, taskClassList);
    }

    @Override
    protected void shutDown() throws Exception {
        pluginStarted = false;
        chinBreakHandler.unregisterPlugin(this);
        overlayManager.remove(overlay);
        tasks.clear();
    }

    @Subscribe
    public void onConfigButtonClicked(ConfigButtonClicked event) {
        if (!event.getGroup().equals(GildedAltarConfig.class.getAnnotation(ConfigGroup.class).value())) {
            return;
        }

        if (event.getKey().equals("startButton")) {
            pluginStarted = true;
            chinBreakHandler.startPlugin(this);
        } else if (event.getKey().equals("stopButton")) {
            pluginStarted = false;
            chinBreakHandler.stopPlugin(this);
        }
    }

    @Subscribe
    private void onChatMessage(ChatMessage event) {
        if (event.getMessage().equals("That player is offline, or has privacy mode enabled."))
            stopPlugin("Host has gone offline!");
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (!pluginStarted) {
            return;
        }

        if (chinBreakHandler.isBreakActive(this)) {
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
        chinBreakHandler.stopPlugin(this);

        if (reason != null && !reason.isEmpty())
            sendGameMessage("GildedAltar Stopped: " + reason);
    }
}
