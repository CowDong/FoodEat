package net.runelite.client.plugins.continueclicker;

import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Point;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.event.MouseEvent;

@Singleton
@Extension
@PluginDescriptor(
        name = "Continue Clicker",
        description = "Presses continue on dialogue when available",
        tags = {"continue", "chat", "dialogue", "clicker"},
        enabledByDefault = false
)
public class ContinueClickerPlugin extends Plugin {
    @Inject
    public Client client;

    @Inject
    public ClientThread clientThread;

    @Override
    protected void startUp() throws Exception {
    }

    @Override
    protected void shutDown() throws Exception {
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        Widget widget = getContinueWidget();

        if (widget != null && !widget.isHidden()) {
            Widget finalWidget1 = widget;
            this.clientThread.invoke(() ->
                    client.invokeMenuAction(
                            "Continue",
                            "",
                            finalWidget1.getType(),
                            MenuAction.WIDGET_TYPE_6.getId(),
                            -1,
                            finalWidget1.getId()));
            return;
        }

        //this widget requires it's own menu entry as it doesn't align with the others
        widget = client.getWidget(WidgetInfo.DIALOG_SPRITE);

        if (widget != null && !widget.isHidden()) {
            Widget finalWidget = widget;
            this.clientThread.invoke(() ->
                    client.invokeMenuAction("Continue",
                            "",
                            1,
                            MenuAction.CC_OP.getId(),
                            2,
                            finalWidget.getId()));
        }
    }

    public Widget getContinueWidget() {
        Widget widget = client.getWidget(WidgetInfo.LEVEL_UP_CONTINUE);

        if (widget != null && !widget.isHidden()) {
            return widget;
        }

        widget = client.getWidget(WidgetInfo.LEVEL_UP);

        if (widget != null && !widget.isHidden()) {
            return widget;
        }

        widget = client.getWidget(WidgetInfo.LEVEL_UP_SKILL);

        if (widget != null && !widget.isHidden()) {
            return widget;
        }

        widget = client.getWidget(WidgetInfo.LEVEL_UP_LEVEL);

        if (widget != null && !widget.isHidden()) {
            return widget;
        }

        widget = client.getWidget(231, 4);

        if (widget != null && !widget.isHidden()) {
            return widget;
        }

        widget = client.getWidget(WidgetInfo.DIALOG_PLAYER_CONTINUE);

        if (widget != null && !widget.isHidden()) {
            return widget;
        }

        widget = client.getWidget(WidgetInfo.DIALOG2_SPRITE_CONTINUE);

        if (widget != null && !widget.isHidden()) {
            return widget;
        }

        widget = client.getWidget(WidgetInfo.DIALOG_NOTIFICATION_CONTINUE);

        if (widget != null && !widget.isHidden()) {
            return widget;
        }

        return null;
    }
}