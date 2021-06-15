package me.jishuna.challengeme.challenges;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.player.ChallengePlayer;

public class BlockEffectChallenge extends Challenge {

	private List<PotionEffectType> effects;
	private int maxLevel;
	private int duration;
	private int chance;
	private final Random random = new Random();
	private static final String KEY = "block_effects";

	public BlockEffectChallenge(Plugin owner) {
		super(owner, KEY);

		addEventHandler(BlockBreakEvent.class, this::onBreakBlock);
	}

	@Override
	protected void loadData(YamlConfiguration upgradeConfig) {
		super.loadData(upgradeConfig);

		this.effects = Arrays.asList(PotionEffectType.values()).stream().collect(Collectors.toList());

		this.maxLevel = upgradeConfig.getInt("max-level", 3);
		this.duration = upgradeConfig.getInt("duration", 100);
		this.chance = upgradeConfig.getInt("chance", 10);

		for (String effect : upgradeConfig.getStringList("blacklisted-effects")) {
			PotionEffectType type = PotionEffectType.getByName(effect.toUpperCase());

			if (type != null) {
				this.effects.remove(type);
			}
		}
	}

	private void onBreakBlock(BlockBreakEvent event, ChallengePlayer challengePlayer) {
		if (random.nextInt(100) < this.chance) {
			Player player = challengePlayer.getPlayer();

			player.addPotionEffect(new PotionEffect(this.effects.get(random.nextInt(this.effects.size())),
					this.duration, random.nextInt(this.maxLevel), true));
		}
	}

}
