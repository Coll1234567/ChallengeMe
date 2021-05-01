package me.jishuna.challengeme.challenges;

import org.bukkit.Chunk;
import org.bukkit.potion.PotionEffectType;

public class ChunkEffectCache {

	private final int x;
	private final int z;
	private final int level;
	private final PotionEffectType effectType;

	public ChunkEffectCache(Chunk chunk, PotionEffectType effectType, int level) {
		this.x = chunk.getX();
		this.z = chunk.getZ();
		this.level = level;
		this.effectType = effectType;
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	public PotionEffectType getEffectType() {
		return effectType;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ChunkEffectCache other = (ChunkEffectCache) obj;

		return other.getX() == getX() && other.getZ() == getZ();
	}

	public int getLevel() {
		return level;
	}

}
