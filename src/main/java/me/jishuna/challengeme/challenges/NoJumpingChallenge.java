package me.jishuna.challengeme.challenges;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Action;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.challenge.ToggleChallenge;
import me.jishuna.challengeme.api.player.ChallengePlayer;

public class NoJumpingChallenge extends Challenge implements ToggleChallenge {
	private static final String KEY = "no_jumping";

	public NoJumpingChallenge(Plugin owner) {
		super(owner, KEY, loadConfig(owner, KEY));

		addEventHandler(EntityPotionEffectEvent.class, this::onEffect);
		addEventHandler(PlayerRespawnEvent.class, this::onRespawn);
	}

	private void onRespawn(PlayerRespawnEvent event, ChallengePlayer challengePlayer) {
		Bukkit.getScheduler().runTask(this.getOwningPlugin(), () -> event.getPlayer()
				.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128, true, false)));
	}

	private void onEffect(EntityPotionEffectEvent event, ChallengePlayer challengePlayer) {
		if (event.getAction() == Action.ADDED)
			return;

		if (event.getOldEffect().getType() == PotionEffectType.JUMP) {
			event.setCancelled(true);
		}
	}

	@Override
	public void onEnable(ChallengePlayer challengePlayer, Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128, true, false));

	}

	@Override
	public void onDisable(ChallengePlayer challengePlayer, Player player) {
		player.removePotionEffect(PotionEffectType.JUMP);

	}
}
