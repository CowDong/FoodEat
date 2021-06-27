package net.runelite.client.plugins.hydraautoprayer.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static net.runelite.client.plugins.hydraautoprayer.HydraAutoPrayerPlugin.*;

@Getter
@RequiredArgsConstructor
public enum HydraPhase
{
	POISON(3, HYDRA_1_1, HYDRA_1_2),
	LIGHTNING(3, HYDRA_2_1, HYDRA_2_2),
	FLAME(3, HYDRA_3_1, HYDRA_3_2),
	ENRAGED(1, HYDRA_4_1, HYDRA_4_2);

	private final int attacksPerSwitch;
	private final int deathAnimation1;
	private final int deathAnimation2;
}
