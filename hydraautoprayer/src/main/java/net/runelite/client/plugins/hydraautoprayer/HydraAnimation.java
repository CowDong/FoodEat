package net.runelite.client.plugins.hydraautoprayer;

import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Prayer;

@RequiredArgsConstructor
public enum HydraAnimation
{
	RANGE(8261, Prayer.PROTECT_FROM_MISSILES),
	MAGIC(8262, Prayer.PROTECT_FROM_MAGIC);

	@Getter(AccessLevel.PACKAGE)
	private final int id;

	@Getter(AccessLevel.PACKAGE)
	private final Prayer prayer;

	public static HydraAnimation fromId(final int id)
	{
		for (final HydraAnimation hydraAnimation : HydraAnimation.values())
		{
			if (Objects.equals(hydraAnimation.id, id))
			{
				return hydraAnimation;
			}
		}

		throw new IllegalArgumentException();
	}
}
