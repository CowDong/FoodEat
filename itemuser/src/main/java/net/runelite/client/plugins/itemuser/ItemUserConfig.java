package net.runelite.client.plugins.itemuser;

import net.runelite.client.config.Button;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("itemuser")
public interface ItemUserConfig extends Config
{

	@ConfigItem(
		keyName = "itemId",
		name = "Item ID",
		description = "The ID of the item you want to use on the object.",
		position = 0
	)
	default int itemId()
	{
		return 536;
	}

	@ConfigItem(
		keyName = "objectId",
		name = "Object ID",
		description = "The ID of the object to use the item on.",
		position = 1
	)
	default int objectId()
	{
		return 13197;
	}

	@ConfigItem(
		keyName = "waitMin",
		name = "Delay Min",
		description = "Minimum time to delay",
		position = 3
	)
	default int waitMin() { return 5; }

	@ConfigItem(
		keyName = "waitMax",
		name = "Delay Max",
		description = "Maximum time to delay",
		position = 5
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
