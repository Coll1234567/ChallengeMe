package me.jishuna.challengeme.challenges;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.challenge.TickingChallenge;

public class AlwaysFlyingChallenge extends Challenge implements TickingChallenge {

	public AlwaysFlyingChallenge(Plugin owner, YamlConfiguration messageConfig) {
		super(owner, "always-flying", messageConfig);

		addEventHandler(EntityToggleGlideEvent.class, this::onToggleGlide);
	}

	private void onToggleGlide(EntityToggleGlideEvent event, Player player) {
		event.setCancelled(true);
	}

	@Override
	public void onTick(Player player) {
		if (!player.isGliding())
			player.setGliding(true);

		Vector velocity = player.getVelocity();
		if (velocity.length() < 0.3D && !player.getLocation().getBlock().isLiquid())
			player.setVelocity(velocity.multiply(1.5D));

	}
}
