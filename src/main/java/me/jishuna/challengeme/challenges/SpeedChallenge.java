package me.jishuna.challengeme.challenges;

import java.util.Iterator;

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
	private double change;
	private static final String KEY = "speed";

	public SpeedChallenge(Plugin owner) {
		super(owner, KEY);
		
		addEventHandler(PlayerDeathEvent.class, this::onDeath);
	}

	@Override
	protected void loadData(YamlConfiguration upgradeConfig) {
		super.loadData(upgradeConfig);
		
		this.change = upgradeConfig.getDouble("change-per-cycle", 0.00001);
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
				new AttributeModifier(MODIFIER_NAME, speedModifier.getAmount() + this.change, Operation.ADD_NUMBER));
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

		Iterator<AttributeModifier> iterator = attribute.getModifiers().iterator();

		while (iterator.hasNext()) {
			AttributeModifier modifier = iterator.next();
			if (modifier.getName().equals(MODIFIER_NAME)) {
				attribute.removeModifier(modifier);
				challengePlayer.setChallengeData(this, modifier.getAmount());
			}
		}
	}

	private double getSpeedValue(ChallengePlayer player) {
		Double speed = player.getChallengeData(this, Double.class);
		return speed == null ? 0 : speed;
	}
}
