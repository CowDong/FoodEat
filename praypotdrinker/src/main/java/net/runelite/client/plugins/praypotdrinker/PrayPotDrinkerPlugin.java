package net.runelite.client.plugins.praypotdrinker;

import com.google.inject.Provides;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuAction;
import net.runelite.api.Point;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Prayer Pot Drinker",
	description = "Automatically drink pray pots",
	tags = {"combat", "notifications", "prayer"},
	enabledByDefault = false
)
public class PrayPotDrinkerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private PrayPotDrinkerConfig config;

	@Inject
	private ItemManager itemManager;

	private Random r = new Random();
	private int nextRestoreVal = 0;

	@Provides
	PrayPotDrinkerConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(PrayPotDrinkerConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		nextRestoreVal = r.nextInt(config.maxPrayerLevel() - config.minPrayerLevel()) + config.minPrayerLevel();
	}

	@Override
	protected void shutDown() throws Exception
	{
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("praypotdrinker"))
		{
			return;
		}

		nextRestoreVal = r.nextInt(config.maxPrayerLevel() - config.minPrayerLevel()) + config.minPrayerLevel();
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		try
		{
			WidgetItem restoreItem = getRestoreItem();

			if (restoreItem == null)
			{
				return;
			}

			int currentPrayerPoints = client.getBoostedSkillLevel(Skill.PRAYER);
			int prayerLevel = client.getRealSkillLevel(Skill.PRAYER);
			int boostAmount = getBoostAmount(restoreItem, prayerLevel);

			if (currentPrayerPoints + boostAmount > prayerLevel)
			{
				return;
			}

			if (currentPrayerPoints <= nextRestoreVal)
			{
				clientThread.invoke(() ->
						client.invokeMenuAction(
								"Drink",
								"<col=ff9040>Potion",
								restoreItem.getId(),
								MenuAction.ITEM_FIRST_OPTION.getId(),
								restoreItem.getIndex(),
								WidgetInfo.INVENTORY.getId()
						)
				);
				nextRestoreVal = r.nextInt(config.maxPrayerLevel() - config.minPrayerLevel()) + config.minPrayerLevel();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private MenuEntry getConsumableEntry(String itemName, int itemId, int itemIndex)
	{
		return new MenuEntry("Drink", "<col=ff9040>" + itemName, itemId, MenuAction.ITEM_FIRST_OPTION.getId(), itemIndex, WidgetInfo.INVENTORY.getId(), false);
	}

	public WidgetItem getRestoreItem()
	{
		WidgetItem item;

		item = PrayerRestoreType.PRAYER_POTION.getItemFromInventory(client);

		if (item != null)
		{
			return item;
		}

		item = PrayerRestoreType.SANFEW_SERUM.getItemFromInventory(client);

		if (item != null)
		{
			return item;
		}

		item = PrayerRestoreType.SUPER_RESTORE.getItemFromInventory(client);

		return item;
	}

	public int getBoostAmount(WidgetItem restoreItem, int prayerLevel)
	{
		if (PrayerRestoreType.PRAYER_POTION.containsId(restoreItem.getId()))
		{
			return 7 + (int) Math.floor(prayerLevel * .25);
		}
		else if (PrayerRestoreType.SANFEW_SERUM.containsId(restoreItem.getId()))
		{
			return 4 + (int) Math.floor(prayerLevel * (double)(3 / 10));
		}
		else if (PrayerRestoreType.SUPER_RESTORE.containsId(restoreItem.getId()))
		{
			return 8 + (int) Math.floor(prayerLevel * .25);
		}

		return 0;
	}
}
