package net.runelite.client.plugins.pktools;

import com.openosrs.client.util.WeaponMap;
import com.openosrs.client.util.WeaponStyle;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.kit.KitType;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.Objects;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class PkToolsOverlay extends OverlayPanel {
	private final Client client;
	private final PkToolsPlugin pkToolsPlugin;
	private final PkToolsConfig config;
	private final SpriteManager spriteManager;

	private static final Color NOT_ACTIVATED_BACKGROUND_COLOR = new Color(150, 0, 0, 150);

	public static Point lastEnemyLocation;

	@Inject
	private PkToolsOverlay(Client client, PkToolsPlugin plugin, SpriteManager spriteManager, PkToolsConfig config) {
		super(plugin);
		this.client = client;
		this.pkToolsPlugin = plugin;
		this.spriteManager = spriteManager;
		this.config = config;

		this.setPosition(OverlayPosition.BOTTOM_RIGHT);
		this.setPriority(OverlayPriority.HIGH);
		getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "PK Tools Overlay"));
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		if (this.client.getGameState() != GameState.LOGGED_IN) {
			return null;
		}

		Player lastEnemy = pkToolsPlugin.lastEnemy;

		ImageComponent PROTECT_MELEE_IMG = new ImageComponent(spriteManager.getSprite(SpriteID.PRAYER_PROTECT_FROM_MELEE, 0));
		ImageComponent PROTECT_MISSILES_IMG = new ImageComponent(spriteManager.getSprite(SpriteID.PRAYER_PROTECT_FROM_MISSILES, 0));
		ImageComponent PROTECT_MAGIC_IMG = new ImageComponent(spriteManager.getSprite(SpriteID.PRAYER_PROTECT_FROM_MAGIC, 0));

		panelComponent.getChildren().clear();
		panelComponent.getChildren().add(TitleComponent.builder().text("PK").color(config.autoPrayerSwitcherEnabled() ? Color.GREEN : Color.red).build());
		panelComponent.setBackgroundColor(ComponentConstants.STANDARD_BACKGROUND_COLOR);
		panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth("PK") + 10, 0));

		if (lastEnemy == null) {
			PkToolsOverlay.lastEnemyLocation = null;
		} else {
			int WEAPON_INT = Objects.requireNonNull(lastEnemy.getPlayerComposition()).getEquipmentId(KitType.WEAPON);

			WeaponStyle style = WeaponMap.StyleMap.getOrDefault(WEAPON_INT, null);

			if (style == null) {
				return panelComponent.render(graphics);
			}

			if (config.prayerHelper()) {
				switch (style) {
					case MELEE:
						panelComponent.getChildren().add(PROTECT_MELEE_IMG);

						if (!client.isPrayerActive(Prayer.PROTECT_FROM_MELEE)) {
							panelComponent.setBackgroundColor(PkToolsOverlay.NOT_ACTIVATED_BACKGROUND_COLOR);
						}
						break;
					case RANGE:
						panelComponent.getChildren().add(PROTECT_MISSILES_IMG);

						if (!client.isPrayerActive(Prayer.PROTECT_FROM_MISSILES)) {
							panelComponent.setBackgroundColor(PkToolsOverlay.NOT_ACTIVATED_BACKGROUND_COLOR);
						}
						break;
					case MAGIC:
						panelComponent.getChildren().add(PROTECT_MAGIC_IMG);

						if (!client.isPrayerActive(Prayer.PROTECT_FROM_MAGIC)) {
							panelComponent.setBackgroundColor(PkToolsOverlay.NOT_ACTIVATED_BACKGROUND_COLOR);
						}
						break;
					default:
						break;
				}
			}
		}

		return super.render(graphics);
	}
}