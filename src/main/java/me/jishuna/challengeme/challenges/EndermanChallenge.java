package me.jishuna.challengeme.challenges;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.challenge.TickingChallenge;
import me.jishuna.challengeme.api.player.ChallengePlayer;

public class EndermanChallenge extends Challenge implements TickingChallenge {
	private final Random random = new Random();

	public EndermanChallenge(Plugin owner, YamlConfiguration messageConfig) {
		super(owner, "enderman", messageConfig);

		addEventHandler(EntityDamageEvent.class, this::onDamage);
	}

	private void onDamage(EntityDamageEvent event, ChallengePlayer challengePlayer) {
		teleport((Player) event.getEntity());
	}

	@Override
	public void onTick(ChallengePlayer challengePlayer, Player player) {
		if (player.isInWater()) {
			player.damage(1d);
		}
	}

	private void teleport(Player player) {
		for (int i = 0; i < 3; i++) {
			Location location = player.getLocation();

			location.setX(location.getBlockX() + this.random.nextInt(64) - 32);
			location.setY(location.getBlockY() + 64);
			location.setZ(location.getBlockZ() + this.random.nextInt(64) - 32);

			while (location.getBlockY() > 0 && !location.getBlock().getType().isSolid()) {
				location.subtract(0, 1, 0);
			}

			if (location.getBlock().getRelative(0, 1, 0).getType().isAir()
					&& location.getBlock().getRelative(0, 2, 0).getType().isAir()) {
				player.teleport(location.add(0.5, 1, 0.5));
				player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.HOSTILE, 1f, 1f);
				break;
			}
		}
	}
}
