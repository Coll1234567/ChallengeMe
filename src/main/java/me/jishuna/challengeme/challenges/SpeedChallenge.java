package me.jishuna.challengeme.challenges;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.challenge.TickingChallenge;
import me.jishuna.challengeme.api.challenge.ToggleChallenge;
import me.jishuna.challengeme.api.player.ChallengePlayer;

public class SpeedChallenge extends Challenge implements ToggleChallenge, TickingChallenge {
	private static final String MODIFIER_NAME = "challengeme_speedboost";

	public SpeedChallenge(Plugin owner, YamlConfiguration messageConfig) {
		super(owner, "speed", messageConfig);

		addEventHandler(PlayerDeathEvent.class, this::onDeath);
	}

	private void onDeath(PlayerDeathEvent event, ChallengePlayer challengePlayer) {
		Player player = event.getEntity();

		AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
		AttributeModifier speedModifier = null;

		for (AttributeModifier modifier : attribute.getModifiers()) {
			if (modifier.getName().equals(MODIFIER_NAME)) {
				speedModifier = modifier;
				break;
			}
		}

		if (speedModifier != null) {
			attribute.removeModifier(speedModifier);
			challengePlayer.setChallengeData(this, 0);
		}
	}

	@Override
	public void onTick(ChallengePlayer challengePlayer, Player player) {
		if (player.isDead())
			return;

		AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);

		AttributeModifier speedModifier = null;

		for (AttributeModifier modifier : attribute.getModifiers()) {
			if (modifier.getName().equals(MODIFIER_NAME)) {
				speedModifier = modifier;
				break;
			}
		}

		if (speedModifier == null) {
			speedModifier = new AttributeModifier(MODIFIER_NAME, getSpeedValue(challengePlayer), Operation.ADD_NUMBER);
		}

		attribute.removeModifier(speedModifier);
		attribute.addModifier(
				new AttributeModifier(MODIFIER_NAME, speedModifier.getAmount() + 0.0001, Operation.ADD_NUMBER));
	}

	@Override
	public void onEnable(ChallengePlayer challengePlayer, Player player) {
		AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
		attribute.addModifier(
				new AttributeModifier(MODIFIER_NAME, getSpeedValue(challengePlayer), Operation.ADD_NUMBER));

	}

	@Override
	public void onDisable(ChallengePlayer challengePlayer, Player player) {
		AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
		AttributeModifier speedModifier = null;

		for (AttributeModifier modifier : attribute.getModifiers()) {
			if (modifier.getName().equals(MODIFIER_NAME)) {
				speedModifier = modifier;
				break;
			}
		}

		if (speedModifier != null) {
			attribute.removeModifier(speedModifier);
			challengePlayer.setChallengeData(this, speedModifier.getAmount());
		}
	}

	private double getSpeedValue(ChallengePlayer player) {
		Double speed = player.getChallengeData(this, Double.class);
		return speed == null ? 0 : speed;
	}
}
