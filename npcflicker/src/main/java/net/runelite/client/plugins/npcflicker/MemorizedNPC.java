package net.runelite.client.plugins.npcflicker;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldArea;

@Getter
@Setter
public class MemorizedNPC
{
	NPC npc;
	int npcIndex;
	String npcName;
	int attackSpeed;
	int combatTimerEnd;
	int timeLeft;
	int flinchTimerEnd;
	Status status;
	WorldArea lastnpcarea;
	Actor lastinteracted;

	MemorizedNPC(final NPC npc, final int attackSpeed, final WorldArea worldArea)
	{
		this.npc = npc;
		this.npcIndex = npc.getIndex();
		this.npcName = npc.getName();
		this.attackSpeed = attackSpeed;
		this.combatTimerEnd = -1;
		this.flinchTimerEnd = -1;
		this.timeLeft = 0;
		this.status = Status.OUT_OF_COMBAT;
		this.lastnpcarea = worldArea;
		this.lastinteracted = null;
	}

	enum Status
	{
		FLINCHING("Flinching"),
		IN_COMBAT_DELAY("In Combat Delay"),
		IN_COMBAT("In Combat"),
		OUT_OF_COMBAT("Out of Combat");

		private String name;

		Status(String name)
		{
			this.name = name;
		}
	}
}