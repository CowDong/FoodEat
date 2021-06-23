package net.runelite.client.plugins.specialattackuser;

import com.google.inject.Provides;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuAction;
import net.runelite.api.Point;
import net.runelite.api.VarPlayer;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Special Attack User",
	description = "Automatically enables special attack",
	tags = {"combat", "special", "attack", "spec"},
	enabledByDefault = false
)
public class SpecialAttackUserPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private SpecialAttackUserConfig config;

	@Provides
	SpecialAttackUserConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(SpecialAttackUserConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{

	}

	@Override
	protected void shutDown() throws Exception
	{
	}

	public boolean isBankOpen()
	{
		Widget widget = client.getWidget(WidgetInfo.BANK_CONTAINER);

		if (widget != null && !widget.isHidden())
		{
			return true;
		}

		return false;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		boolean spec_enabled = (client.getVar(VarPlayer.SPECIAL_ATTACK_ENABLED) == 1);

		if (spec_enabled)
		{
			return;
		}

		//value returns 1000 for 100% spec, 500 for 50%, etc
		int spec_percent = client.getVar(VarPlayer.SPECIAL_ATTACK_PERCENT);

		if (spec_percent < config.specialPercent() * 10)
		{
			return;
		}

		if (isBankOpen())
		{
			return;
		}

		Widget specialOrb = client.getWidget(160, 30);

		if (specialOrb == null || specialOrb.isHidden())
		{
			return;
		}

		try
		{
			clientThread.invoke(() ->
					client.invokeMenuAction(
							"Use <col=00ff00>Special Attack</col>",
							"",
							1,
							MenuAction.CC_OP.getId(),
							-1,
							38862884
					)
			);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
