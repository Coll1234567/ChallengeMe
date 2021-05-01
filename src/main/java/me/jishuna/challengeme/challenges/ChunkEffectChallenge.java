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

	public ChunkEffectChallenge(Plugin owner, YamlConfiguration challengeConfig) {
		this(owner, challengeConfig.getConfigurationSection("challenges.chunk-effects"));
	}

	private ChunkEffectChallenge(Plugin owner, ConfigurationSection challengeSection) {
		super(owner, "chunk-effects", challengeSection);

		this.effects = Arrays.asList(PotionEffectType.values()).stream().collect(Collectors.toList());

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

		if (oldCache == null || oldCache.getEffectType() != event.getModifiedType())
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
			int level = random.nextInt(3);

			ChunkEffectCache newCache = new ChunkEffectCache(chunk, newType, level);
			player.addPotionEffect(new PotionEffect(newType, Integer.MAX_VALUE, level, true, false));
			this.effectCache.put(player.getUniqueId(), newCache);

			if (oldCache != null) {
				PotionEffect activeEffect = player.getPotionEffect(oldCache.getEffectType());

				if (activeEffect != null && activeEffect.getAmplifier() == oldCache.getLevel())
					player.removePotionEffect(oldCache.getEffectType());
			}
		}
	}

	private int hashChunk(Chunk chunk) {
		int i = 1664525 * chunk.getX() + 1013904223;
		int j = 1664525 * (chunk.getZ() ^ -559038737) + 1013904223;

		return i ^ j;
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
