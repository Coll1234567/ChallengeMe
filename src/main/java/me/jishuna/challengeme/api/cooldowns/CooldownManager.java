package me.jishuna.challengeme.api.cooldowns;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.jishuna.challengeme.api.challenge.Challenge;

public class CooldownManager {

	private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

	public long getCooldown(Player player, Challenge challenge) {
		return getCooldown(player.getUniqueId(), challenge);
	}

	public long getCooldown(UUID uuid, Challenge challenge) {
		Map<String, Long> cooldownMap = this.cooldowns.computeIfAbsent(uuid, key -> new HashMap<>());
		Long time = cooldownMap.get(challenge.getKey());
		return time == null ? 0 : time;
	}

	public void setCooldown(Player player, Challenge challenge, int time) {
		setCooldown(player.getUniqueId(), challenge, time);
	}

	public void setCooldown(UUID uuid, Challenge challenge, int time) {
		Map<String, Long> cooldownMap = this.cooldowns.computeIfAbsent(uuid, key -> new HashMap<>());
		cooldownMap.put(challenge.getKey(), System.currentTimeMillis() + time * 1000);
	}

}
