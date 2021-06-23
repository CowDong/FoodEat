package net.runelite.client.plugins.itemcombiner;

import net.runelite.client.config.*;

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
		keyName = "iterations",
		name = "Iterations",
		description = "The amount of times to perform the action",
		position = 2
	)
	default int iterations()
	{
		return 30;
	}

	@ConfigItem(
			keyName = "waitMin",
			name = "Delay Min",
			description = "Minimum frames to delay",
			position = 3
	)
	default int waitMin() { return 2; }

	@ConfigItem(
			keyName = "waitMax",
			name = "Delay Max",
			description = "Maximum frames to delay",
			position = 4
	)
	default int waitMax() { return 30; }

	@ConfigItem(keyName = "startButton",
			name = "Start",
			description = "",
			position = 12
	)
	default Button startButton() {
		return new Button();
	}

	@ConfigItem(
			keyName = "stopButton",
			name = "Stop",
			description = "",
			position = 13
	)
	default Button stopButton() {
		return new Button();
	}
}
