package net.runelite.client.plugins.nmzhelper;

import com.google.inject.Provides;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemID;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
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
import net.runelite.client.plugins.nmzhelper.Tasks.*;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
@Extension
@PluginDescriptor(
        name = "NMZ Helper",
        description = "An automation utility for NMZ",
        tags = {"combat", "potion", "overload", "absorption", "nmz", "nightmare", "zone", "helper"},
        enabledByDefault = false
)
public class NMZHelperPlugin extends Plugin {
	/*
		varbits
		absorptions - 3954 (doses in storage)
		overloads - 3953 (doses in storage)
	 */

    static List<Class<?>> taskClassList = new ArrayList<>();

    static {
        taskClassList.add(BreakTask.class);
        taskClassList.add(ResetIdleTask.class);

        taskClassList.add(SpecialAttackTask.class);
        taskClassList.add(OverloadTask.class);
        taskClassList.add(AbsorptionTask.class);
        taskClassList.add(RockCakeTask.class);
        taskClassList.add(PowerSurgeTask.class);

        //--------------------------
        taskClassList.add(SearchRewardsChestTask.class);
        taskClassList.add(BenefitsTabTask.class);
        taskClassList.add(BuyAbsorptionsTask.class);
        taskClassList.add(BuyOverloadsTask.class);
        //--------------------------

        taskClassList.add(WithdrawAbsorptionTask.class);
        taskClassList.add(WithdrawOverloadTask.class);
        taskClassList.add(OpenAbsorptionsBarrelTask.class);
        taskClassList.add(OpenOverloadsBarrel.class);

        taskClassList.add(DominicDreamTask.class);
        taskClassList.add(DominicDialogue1Task.class);
        taskClassList.add(DominicDialogue2Task.class);

        taskClassList.add(ContinueDialogTask.class);

        taskClassList.add(DrinkPotionTask.class);
        taskClassList.add(AcceptDreamTask.class);
    }

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private NMZHelperConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private NMZHelperOverlay overlay;

    @Inject
    private ChatMessageManager chatMessageManager;

    @Inject
    public ReflectBreakHandler chinBreakHandler;

    boolean pluginStarted;

    @Provides
    NMZHelperConfig provideConfig(final ConfigManager configManager) {
        return configManager.getConfig(NMZHelperConfig.class);
    }

    public String status = "initializing...";

    private final TaskSet tasks = new TaskSet();

    public static int rockCakeDelay = 0;

    @Override
    protected void startUp() {
        pluginStarted = false;
        chinBreakHandler.registerPlugin(this);
        overlayManager.add(overlay);
        status = "initializing...";
        tasks.clear();
        tasks.addAll(this, client, clientThread, config, taskClassList);
    }

    @Override
    protected void shutDown() {
        pluginStarted = false;
        chinBreakHandler.unregisterPlugin(this);
        overlayManager.remove(overlay);
        tasks.clear();
    }

    @Subscribe
    public void onConfigButtonClicked(ConfigButtonClicked event) {
        if (!event.getGroup().equals(NMZHelperConfig.class.getAnnotation(ConfigGroup.class).value())) {
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
        if (!pluginStarted) {
            return;
        }

        String msg = Text.removeTags(event.getMessage()); //remove color

        switch (event.getType()) {
            case SPAM:
                if (msg.contains("You drink some of your overload potion.")) {
                    rockCakeDelay = 12;
                }
                break;
            case GAMEMESSAGE:
                if (msg.contains("This barrel is empty.")
                        || msg.contains("There is no ammo left in your quiver.")
                        || msg.contains("Your blowpipe has run out of scales and darts.")
                        || msg.contains("Your blowpipe has run out of darts.")
                        || msg.contains("Your blowpipe needs to be charged with Zulrah's scales.")) {
                    stopPlugin("Received game message: " + msg);
                }
                break;
            default:
                break;
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!pluginStarted || chinBreakHandler.isBreakActive(this)) {
            return;
        }

        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        //if we don't have a rock cake, return...may need to stop the plugin but this is causing it to stop
        //	randomly for some reason
        Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);

        if (inventoryWidget == null)
            return;

        if (inventoryWidget.getWidgetItems()
                .stream().noneMatch(item -> item.getId() == ItemID.DWARVEN_ROCK_CAKE_7510)) {
            //pluginStarted = false;
            sendGameMessage("Rock cake not found...");
            return;
        }

        if (client.getVarbitValue(3948) < 26 && !MiscUtils.isInNightmareZone(client)) {
            stopPlugin("You need to put money in the coffer!");
            return;
        }

        // If the player already has a dream created that isn't supported.
        if (MiscUtils.getDreamType(client).equals(DreamType.NOT_SUPPORTED)) {
            stopPlugin("The created dream is not supported.");
            return;
        }

        Task task = tasks.getValidTask();

        if (task != null) {
            status = task.getTaskDescription();
            task.onGameTick(event);
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (!pluginStarted || chinBreakHandler.isBreakActive(this))
            return;

        if (!config.autoRelog())
            return;

        if (client.getGameState() != GameState.LOGIN_SCREEN)
            return;

        client.setUsername(config.email());
        client.setPassword(config.password());
        client.setGameState(GameState.LOGGING_IN);
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
            sendGameMessage("NMZHelper Stopped: " + reason);
    }
}
