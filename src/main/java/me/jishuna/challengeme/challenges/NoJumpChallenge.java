package me.jishuna.challengeme.challenges;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Action;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.challenge.ToggleChallenge;

public class NoJumpChallenge extends Challenge implements ToggleChallenge {

	public NoJumpChallenge(Plugin owner, YamlConfiguration messageConfig) {
		super(owner, "no-jump", messageConfig);

		addEventHandler(EntityPotionEffectEvent.class, this::onEffect);
	}

	private void onEffect(EntityPotionEffectEvent event, Player player) {
		if (event.getAction() == Action.ADDED)
			return;

		Bukkit.broadcastMessage(event.getOldEffect().getType().toString());
		if (event.getOldEffect().getType() == PotionEffectType.JUMP) {
			event.setCancelled(true);
		}
	}

	@Override
	public void onEnable(Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128, true, false));

	}

	@Override
	public void onDisable(Player player) {
		player.removePotionEffect(PotionEffectType.JUMP);

	}
}
