package me.jishuna.challengeme.challenges;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.challenge.TickingChallenge;
import me.jishuna.challengeme.api.challenge.ToggleChallenge;

public class NoJumpChallenge extends Challenge implements TickingChallenge, ToggleChallenge {

	public NoJumpChallenge(Plugin owner, YamlConfiguration messageConfig) {
		super(owner, "no-jump", messageConfig);
	}

	@Override
	public void onTick(Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128, true, false));
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
