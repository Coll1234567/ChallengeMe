package me.jishuna.challengeme.challenges;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Chunk;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Action;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.challenge.TickingChallenge;
import me.jishuna.challengeme.api.challenge.ToggleChallenge;

public class ChunkEffectChallenge extends Challenge implements TickingChallenge, ToggleChallenge {

	private final List<PotionEffectType> effects;
	private final Map<UUID, ChunkEffectCache> effectCache = new HashMap<>();
	private final int maxLevel;

	public ChunkEffectChallenge(Plugin owner, YamlConfiguration challengeConfig) {
		this(owner, challengeConfig.getConfigurationSection("chunk-effects"));
	}

	private ChunkEffectChallenge(Plugin owner, ConfigurationSection challengeSection) {
		super(owner, "chunk-effects", challengeSection);

		this.effects = Arrays.asList(PotionEffectType.values()).stream().collect(Collectors.toList());
		
		this.maxLevel = challengeSection.getInt("max-level", 3);

		for (String effect : challengeSection.getStringList("blacklisted-effects")) {
			PotionEffectType type = PotionEffectType.getByName(effect.toUpperCase());

			if (type != null) {
				this.effects.remove(type);
			}
		}

		addEventHandler(EntityPotionEffectEvent.class, this::onEffect);
	}

	private void onEffect(EntityPotionEffectEvent event, Player player) {
		if (event.getAction() == Action.ADDED)
			return;

		ChunkEffectCache oldCache = this.effectCache.get(player.getUniqueId());

		if (oldCache == null || oldCache.getEffectType() != event.getModifiedType()
				|| oldCache.getLevel() != event.getOldEffect().getAmplifier())
			return;

		event.setCancelled(true);
	}

	@Override
	public void onTick(Player player) {
		Chunk chunk = player.getLocation().getChunk();

		ChunkEffectCache oldCache = this.effectCache.get(player.getUniqueId());

		if (oldCache == null || oldCache.getX() != chunk.getX() || oldCache.getZ() != chunk.getZ()) {
			Random random = new Random(hashChunk(chunk));

			PotionEffectType newType = effects.get(random.nextInt(effects.size()));
			int level = random.nextInt(this.maxLevel);

			ChunkEffectCache newCache = new ChunkEffectCache(chunk, newType, level);
			this.effectCache.put(player.getUniqueId(), newCache);

			if (oldCache != null) {
				PotionEffect activeEffect = player.getPotionEffect(oldCache.getEffectType());
				if (activeEffect != null && activeEffect.getAmplifier() == oldCache.getLevel())
					player.removePotionEffect(oldCache.getEffectType());
			}
			player.addPotionEffect(new PotionEffect(newType, Integer.MAX_VALUE, level, true, false));
		}
	}

	private long hashChunk(Chunk chunk) {
		long hash = 3;

		hash = 19 * hash + chunk.getWorld().getSeed();
		hash = 19 * hash + chunk.getX() ^ (chunk.getX() >>> 32);
		hash = 19 * hash + chunk.getZ() ^ (chunk.getZ() >>> 32);

		return hash;
	}

	@Override
	public void onEnable(Player player) {
	}

	@Override
	public void onDisable(Player player) {
		Chunk chunk = player.getLocation().getChunk();
		Random random = new Random(hashChunk(chunk));

		PotionEffectType type = effects.get(random.nextInt(effects.size()));
		player.removePotionEffect(type);

		effectCache.remove(player.getUniqueId());
	}
}
