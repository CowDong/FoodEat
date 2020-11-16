package net.runelite.client.plugins.nmzhelper;

import net.runelite.client.config.Button;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("nmzhelper")
public interface NMZHelperConfig extends Config
{
	@ConfigItem(
		keyName = "absorptionThresholdMin",
		name = "Absorption Threshold (min)",
		description = "The amount of absorption points to maintain.",
		position = 1
	)
	default int absorptionThresholdMin()
	{
		return 200;
	}

	@ConfigItem(
		keyName = "absorptionThresholdMax",
		name = "Absorption Threshold (max)",
		description = "The amount of absorption points to maintain.",
		position = 2
	)
	default int absorptionThresholdMax()
	{
		return 300;
	}

	@ConfigItem(
		keyName = "overloadDoses",
		name = "Overload Doses",
		description = "The amount of doses of overload to withdraw.",
		position = 3
	)
	default int overloadDoses() { return 20; }

	@ConfigItem(
		keyName = "absorptionDoses",
		name = "Absorption Doses",
		description = "The amount of doses of absorption to withdraw.",
		position = 4
	)
	default int absorptionDoses() { return 88; }

	@ConfigItem(
		keyName = "useSpecialAttack",
		name = "Use Special Attack",
		description = "Whether to use special attack or not",
		position = 5
	)
	default boolean useSpecialAttack() { return false; }

	@ConfigItem(
		keyName = "specialAttackValue",
		name = "Special Attack Value",
		description = "The value to use special attack at",
		position = 6,
		hidden = true,
		unhide = "useSpecialAttack"
	)
	default int specialAttackValue() { return 100; }

	@ConfigItem(
		keyName = "powerSurge",
		name = "Power Surge?",
		description = "Will activate power surge if one spawns on screen.",
		position = 7
	)
	default boolean powerSurge() { return false; }

	@ConfigItem(
		keyName = "autoRelog",
		name = "Auto Re-Log",
		description = "Log back in after 6 hour logout?",
		position = 8
	)
	default boolean autoRelog() { return false; }

	//username
	@ConfigItem(
		keyName = "email",
		name = "Login Email",
		description = "email",
		position = 9
	)
	default String email() { return ""; }

	//password
	@ConfigItem(
		keyName = "password",
		name = "Password",
		description = "password",
		position = 10,
		secret = true
	)
	default String password() { return ""; }

	@ConfigItem(keyName = "startButton",
		name = "Start",
		description = "",
		position = 11
	)
	default Button startButton() { return new Button(); }

	@ConfigItem(
		keyName = "stopButton",
		name = "Stop",
		description = "",
		position = 12
	)
	default Button stopButton() { return new Button(); }
}
