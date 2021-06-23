package net.runelite.client.plugins.jadautoprayer;

import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

import javax.inject.Inject;

@Extension
@PluginDescriptor(
	name = "Jad Auto Prayer",
	description = "Auto click proper prayers against Jad(s).",
	tags = {"bosses", "combat", "minigame", "overlay", "prayer", "pve", "pvm", "jad", "firecape", "fight", "cave", "caves"},
	enabledByDefault = false
)
public class JadAutoPrayerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	public static final int JALTOK_JAD_MAGE_ATTACK = 7592;
	public static final int JALTOK_JAD_RANGE_ATTACK = 7593;

	@Override
	protected void startUp() throws Exception
	{
	}

	@Override
	protected void shutDown() throws Exception
	{
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		Actor actor = event.getActor();

		if (actor == null)
		{
			return;
		}

		switch (actor.getAnimation())
		{
			case AnimationID.TZTOK_JAD_MAGIC_ATTACK:
			case JALTOK_JAD_MAGE_ATTACK:
				if (client.getVar(Prayer.PROTECT_FROM_MAGIC.getVarbit()) == 0)
				{
					activatePrayer(WidgetInfo.PRAYER_PROTECT_FROM_MAGIC);
				}
				break;
			case AnimationID.TZTOK_JAD_RANGE_ATTACK:
			case JALTOK_JAD_RANGE_ATTACK:
				if (client.getVar(Prayer.PROTECT_FROM_MISSILES.getVarbit()) == 0)
				{
					activatePrayer(WidgetInfo.PRAYER_PROTECT_FROM_MISSILES);
				}
				break;
			default:
				break;
		}
	}

	public void activatePrayer(WidgetInfo widgetInfo)
	{
		Widget prayer_widget = client.getWidget(widgetInfo);

		if (prayer_widget == null)
		{
			return;
		}

		if (client.getBoostedSkillLevel(Skill.PRAYER) <= 0)
		{
			return;
		}

		clientThread.invoke(() ->
				client.invokeMenuAction(
				"Activate",
						prayer_widget.getName(),
						1,
						MenuAction.CC_OP.getId(),
						prayer_widget.getItemId(),
						prayer_widget.getId()
				)
		);
	}
}