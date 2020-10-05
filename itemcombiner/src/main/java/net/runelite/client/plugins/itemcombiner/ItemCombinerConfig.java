package net.runelite.client.plugins.itemcombiner;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

@ConfigGroup("itemcombiner")
public interface ItemCombinerConfig extends Config
{

	@ConfigItem(
		keyName = "itemId",
		name = "Item ID",
		description = "The ID of the first item.",
		position = 0
	)
	default int itemId()
	{
		return 821;
	}

	@ConfigItem(
		keyName = "itemId2",
		name = "Second Item ID",
		description = "The ID of the second item.",
		position = 1
	)
	default int itemId2()
	{
		return 314;
	}

	@ConfigItem(
		keyName = "useItemsKeybind",
		name = "Use Keybind",
		description = "The keybind to use the items",
		position = 2
	)
	default Keybind useItemsKeybind()
	{
		return new Keybind(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK);
	}

	@ConfigItem(
		keyName = "iterations",
		name = "Iterations",
		description = "The amount of times to perform the action",
		position = 3
	)
	default int iterations()
	{
		return 30;
	}

	@ConfigItem(
			keyName = "waitMin",
			name = "Delay Min",
			description = "Minimum time to delay",
			position = 4
	)
	default int waitMin() { return 5; }

	@ConfigItem(
			keyName = "waitMax",
			name = "Delay Max",
			description = "Maximum time to delay",
			position = 5
	)
	default int waitMax() { return 30; }
}
