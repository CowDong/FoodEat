package net.runelite.client.plugins.gildedaltar;

import com.openosrs.client.ui.overlay.components.table.TableAlignment;
import com.openosrs.client.ui.overlay.components.table.TableComponent;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;

import java.awt.*;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class GildedAltarOverlay extends Overlay {
    private final GildedAltarPlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    public GildedAltarOverlay(GildedAltarPlugin plugin) {
        this.plugin = plugin;

        this.setPriority(OverlayPriority.HIGHEST);
        this.setPosition(OverlayPosition.BOTTOM_LEFT);
        this.getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Gilded Altar Overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (plugin == null)
            return null;

        if (!plugin.pluginStarted)
            return null;

        panelComponent.getChildren().clear();

        TableComponent tableComponent = new TableComponent();
        tableComponent.setColumnAlignments(TableAlignment.LEFT);
        tableComponent.setDefaultColor(Color.ORANGE);

        tableComponent.addRow("Gilded Altar");
        tableComponent.addRow(plugin.status);
        tableComponent.addRow("Delay: " + plugin.delay);

        if (!tableComponent.isEmpty()) {
            panelComponent.getChildren().add(tableComponent);
        }

        panelComponent.setPreferredSize(new Dimension(175, 100));
        panelComponent.setBackgroundColor(Color.BLACK);

        return panelComponent.render(graphics);
    }
}
