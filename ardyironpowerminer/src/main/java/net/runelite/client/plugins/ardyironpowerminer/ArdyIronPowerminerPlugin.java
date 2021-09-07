package net.runelite.client.plugins.ardyironpowerminer;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Extension
@PluginDescriptor(
        name = "Ardy Iron Powerminer",
        description = "Automatically powermines iron @ ardy mine",
        tags = {"mining", "mine", "powermine", "iron", "ardy", "ardougne", "skill", "skilling"},
        enabledByDefault = false
)
public class ArdyIronPowerminerPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ArdyIronPowerminerConfig config;

    @Inject
    public ReflectBreakHandler chinBreakHandler;

    private boolean pluginStarted = false;

    private int tickDelay = 0;
    private int frameDelay = 0;

    boolean isDropping = false;

    private final WorldPoint basePoint = new WorldPoint(2692, 3329, 0);

    @Provides
    ArdyIronPowerminerConfig provideConfig(final ConfigManager configManager) {
        return configManager.getConfig(ArdyIronPowerminerConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        chinBreakHandler.registerPlugin(this);
    }

    @Override
    protected void shutDown() throws Exception {
        chinBreakHandler.unregisterPlugin(this);
    }

    @Subscribe
    public void onConfigButtonClicked(ConfigButtonClicked event) {
        if (!event.getGroup().equals(ArdyIronPowerminerConfig.class.getAnnotation(ConfigGroup.class).value())) {
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
    public void onClientTick(ClientTick event) {
        if (chinBreakHandler.isBreakActive(this))
            return;

        if (frameDelay > 0) {
            frameDelay--;
            return;
        }

        if (!isDropping) {
            return;
        }

        Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);

        if (inventoryWidget == null) {
            return;
        }

        List<WidgetItem> list = inventoryWidget.getWidgetItems().stream().filter(item -> item.getId() == ItemID.IRON_ORE).collect(Collectors.toList());

        if (list.isEmpty()) {
            isDropping = false;
            return;
        }

        clientThread.invoke(() ->
                client.invokeMenuAction(
                        "Drop",
                        "<col=ff9040>Iron ore",
                        list.get(0).getId(),
                        MenuAction.ITEM_FIFTH_OPTION.getId(),
                        list.get(0).getIndex(),
                        WidgetInfo.INVENTORY.getId()
                )
        );

        frameDelay = 10;
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (tickDelay > 0) {
            tickDelay--;
            return;
        }

        if (!pluginStarted)
            return;

        if (chinBreakHandler.isBreakActive(this))
            return;

        if (chinBreakHandler.shouldBreak(this)) {
            //status = "Taking a break";
            chinBreakHandler.startBreak(this);
        }

        if (isDropping)
            return;

        if (MiscUtils.isInventoryFull(client)) {
            tickDelay = 1;
            isDropping = true;
            return;
        }

        if (MiscUtils.isMining(client)) {
            tickDelay = 1;
            return;
        }

        GameObject rock = MiscUtils.findNearestGameObjectWithin(client, basePoint, 2, ObjectID.ROCKS_11364);

        if (rock == null) {
            rock = MiscUtils.findNearestGameObjectWithin(client, basePoint, 2, ObjectID.ROCKS_11365);
        }

        if (rock == null) {
            tickDelay = 9;
            return;
        }

        GameObject finalRock = rock;
        clientThread.invoke(() ->
                client.invokeMenuAction(
                        "Mine",
                        "<col=ffff>Rocks",
                        finalRock.getId(),
                        MenuAction.GAME_OBJECT_FIRST_OPTION.getId(),
                        finalRock.getSceneMinLocation().getX(),
                        finalRock.getSceneMinLocation().getY()
                )
        );
        tickDelay = 1;
    }
}