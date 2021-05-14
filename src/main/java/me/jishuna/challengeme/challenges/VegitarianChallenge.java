package me.jishuna.challengeme.challenges;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.plugin.Plugin;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.player.ChallengePlayer;
import me.jishuna.commonlib.MaterialSets;

public class VegitarianChallenge extends Challenge {

	public VegitarianChallenge(Plugin owner, YamlConfiguration challengeConfig) {
		super(owner, "vegitarian", challengeConfig);
		
		addEventHandler(PlayerInteractEvent.class, this::onInteract);
		addEventHandler(PlayerItemConsumeEvent.class, this::onConsume);
	}

	private void onInteract(PlayerInteractEvent event, ChallengePlayer challengePlayer) {
		if (MaterialSets.MEAT.contains(event.getItem().getType())) {
			event.getPlayer().sendMessage(this.getMessage());
		}
	}

	private void onConsume(PlayerItemConsumeEvent event, ChallengePlayer challengePlayer) {
		if (MaterialSets.MEAT.contains(event.getItem().getType())) {
			event.setCancelled(true);
		}
	}
}
