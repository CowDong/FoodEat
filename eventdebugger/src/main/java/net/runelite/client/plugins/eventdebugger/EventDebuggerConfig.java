package net.runelite.client.plugins.eventdebugger;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigTitle;

@ConfigGroup("eventdebugger")
public interface EventDebuggerConfig extends Config
{
	@ConfigTitle(
			position = 0,
			keyName = "menuOptionClickedSection",
			name = "MenuOptionClicked",
			description = ""
	)
	String menuOptionClickedSection = "menuOptionClickedSection";

	@ConfigItem(
			position = 1,
			keyName = "menuOptionClicked",
			name = "Debug MenuOptionClicked",
			description = "",
			section = "menuEntryOptionClicked"
	)
	default boolean menuOptionClicked()
	{
		return false;
	}

	//////////////////////////////////////////////////////////////

	@ConfigTitle(
		position = 2,
		keyName = "menuEntryAddedSection",
		name = "MenuEntryAdded",
		description = ""
	)
	String menuEntryAddedSection = "menuEntryAddedSection";

	@ConfigItem(
		position = 3,
		keyName = "menuEntryAdded",
		name = "Debug MenuEntryAdded",
		description = "",
		section = "menuEntryAddedSection"
	)
	default boolean menuEntryAdded()
	{
		return false;
	}

	//////////////////////////////////////////////////////////////

	@ConfigItem(
		position = 4,
		keyName = "optionCheckbox",
		name = "Filter by option",
		description = "",
		section = "menuEntryAddedSection"
	)
	default boolean optionCheckbox()
	{
		return false;
	}

	@ConfigItem(
		position = 5,
		keyName = "optionFilter",
		name = "Option Filter",
		description = "case sensitive",
		unhide = "optionCheckbox",
		section = "menuEntryAddedSection",
		hidden = true
	)
	default String optionFilter()
	{
		return "";
	}

	//////////////////////////////////////////////////////////////

	@ConfigItem(
		position = 6,
		keyName = "targetCheckbox",
		name = "Filter by target",
		description = "",
		section = "menuEntryAddedSection"
	)
	default boolean targetCheckbox()
	{
		return false;
	}

	@ConfigItem(
		position = 7,
		keyName = "targetFilter",
		name = "Target Filter",
		description = "case sensitive",
		unhide = "targetCheckbox",
		section = "menuEntryAddedSection",
		hidden = true
	)
	default String targetFilter()
	{
		return "";
	}

	//////////////////////////////////////////////////////////////

	@ConfigItem(
		position = 8,
		keyName = "identifierCheckbox",
		name = "Filter by identifier",
		description = "",
		section = "menuEntryAddedSection"
	)
	default boolean identifierCheckbox()
	{
		return false;
	}

	@ConfigItem(
		position = 9,
		keyName = "identifierFilter",
		name = "Identifier Filter",
		description = "case sensitive",
		unhide = "identifierCheckbox",
		section = "menuEntryAddedSection",
		hidden = true
	)
	default int identifierFilter()
	{
		return -1;
	}

	//////////////////////////////////////////////////////////////

	@ConfigItem(
		position = 10,
		keyName = "opcodeCheckbox",
		name = "Filter by opcode",
		description = "",
		section = "menuEntryAddedSection"
	)
	default boolean opcodeCheckbox()
	{
		return false;
	}

	@ConfigItem(
		position = 11,
		keyName = "opcodeFilter",
		name = "Opcode Filter",
		description = "case sensitive",
		unhide = "opcodeCheckbox",
		section = "menuEntryAddedSection",
		hidden = true
	)
	default int opcodeFilter()
	{
		return -1;
	}

	//////////////////////////////////////////////////////////////

	@ConfigItem(
		position = 12,
		keyName = "param0Checkbox",
		name = "Filter by param0",
		description = "",
		section = "menuEntryAddedSection"
	)
	default boolean param0Checkbox()
	{
		return false;
	}

	@ConfigItem(
		position = 13,
		keyName = "param0Filter",
		name = "Param0 Filter",
		description = "case sensitive",
		unhide = "param0Checkbox",
		section = "menuEntryAddedSection",
		hidden = true
	)
	default int param0Filter()
	{
		return -1;
	}

	//////////////////////////////////////////////////////////////

	@ConfigItem(
		position = 14,
		keyName = "param1Checkbox",
		name = "Filter by param1",
		description = "",
		section = "menuEntryAddedSection"
	)
	default boolean param1Checkbox()
	{
		return false;
	}

	@ConfigItem(
		position = 15,
		keyName = "param1Filter",
		name = "Param1 Filter",
		description = "case sensitive",
		unhide = "param1Checkbox",
		section = "menuEntryAddedSection",
		hidden = true
	)
	default int param1Filter()
	{
		return -1;
	}

	//////////////////////////////////////////////////////////////
}
