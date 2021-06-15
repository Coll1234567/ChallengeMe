package me.jishuna.challengeme.challenges;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Action;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.jishuna.challengeme.ChallengeMe;
import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.challenge.TickingChallenge;
import me.jishuna.challengeme.api.challenge.ToggleChallenge;
import me.jishuna.challengeme.api.player.ChallengePlayer;

public class ReverseGravityChallenge extends Challenge implements ToggleChallenge, TickingChallenge {
	private final Map<UUID, Integer> challengeData = new HashMap<>();
	private static final String KEY = "reverse_gravity";

	private int height;

	public ReverseGravityChallenge(Plugin owner) {
		super(owner, KEY);

		addEventHandler(EntityPotionEffectEvent.class, this::onEffect);
		addEventHandler(PlayerRespawnEvent.class, this::onRespawn);
	}

	@Override
	protected void loadData(YamlConfiguration upgradeConfig) {
		super.loadData(upgradeConfig);

		this.height = upgradeConfig.getInt("damage-height", 256);
	}

	private void onRespawn(PlayerRespawnEvent event, ChallengePlayer challengePlayer) {
		Bukkit.getScheduler().runTask(this.getOwningPlugin(), () -> event.getPlayer()
				.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, Integer.MAX_VALUE, 2, true, false)));
	}

	private void onEffect(EntityPotionEffectEvent event, ChallengePlayer challengePlayer) {
		if (event.getAction() == Action.ADDED)
			return;

		if (event.getOldEffect().getType() == PotionEffectType.LEVITATION) {
			event.setCancelled(true);
		}
	}

	@Override
	public void onEnable(ChallengePlayer challengePlayer, Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, Integer.MAX_VALUE, 2, true, false));

	}

	@Override
	public void onDisable(ChallengePlayer challengePlayer, Player player) {
		player.removePotionEffect(PotionEffectType.LEVITATION);

	}

	@Override
	public void onTick(ChallengePlayer challengePlayer, Player player) {
		if (player.getLocation().getBlockY() > this.height) {
			int checks = this.challengeData.getOrDefault(player.getUniqueId(), 1);

			checks = (checks + 1) % 2;

			if (checks == 0) {
				ChallengeMe.getNMSAdapter().damageEntity(player, DamageCause.VOID, 5);
			}
			this.challengeData.put(player.getUniqueId(), checks);
		}

	}
}
