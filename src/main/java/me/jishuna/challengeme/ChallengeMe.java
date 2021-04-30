package me.jishuna.challengeme;

import java.io.File;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.jishuna.challengeme.api.challenge.ChallengeManager;
import me.jishuna.challengeme.api.player.PlayerManager;
import me.jishuna.challengeme.challenges.AnimalLoverChallenge;
import me.jishuna.challengeme.challenges.NoDamageChallenge;
import me.jishuna.challengeme.challenges.VampireChallenge;
import me.jishuna.challengeme.challenges.VegitarianChallenge;
import me.jishuna.challengeme.commands.ChallengeCommand;
import me.jishuna.challengeme.listeners.ChallengeListener;
import me.jishuna.challengeme.listeners.CustomInventoryManager;
import me.jishuna.challengeme.runnables.TickingChallengeRunnable;
import me.jishuna.commonlib.FileUtils;
import net.md_5.bungee.api.ChatColor;

public class ChallengeMe extends JavaPlugin {

	private ChallengeManager challengeManager;
	private PlayerManager playerManager;
	private CustomInventoryManager inventoryManager;

	private YamlConfiguration challengeConfig;
	private YamlConfiguration config;
	private YamlConfiguration messageConfig;
	
	private TickingChallengeRunnable challengeRunnable;

	@Override
	public void onEnable() {
		loadConfiguration();
		PluginKeys.initialize(this);

		this.inventoryManager = new CustomInventoryManager(this);
		Bukkit.getPluginManager().registerEvents(this.inventoryManager, this);

		this.challengeManager = new ChallengeManager();

		this.playerManager = new PlayerManager(this);
		this.playerManager.registerListeners();

		Bukkit.getPluginManager().registerEvents(new ChallengeListener(this.playerManager), this);
		
		this.challengeRunnable = new TickingChallengeRunnable(this);
		int delay = this.config.getInt("ticks-per-check", 10);
		
		this.challengeRunnable.runTaskTimer(this, delay, delay);

		getCommand("challenges").setExecutor(new ChallengeCommand(this));

		registerDefaultChallenges();
	}

	@Override
	public void onDisable() {
		this.playerManager.saveAllPlayers();
		
		this.challengeRunnable.cancel();
	}

	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	public CustomInventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public ChallengeManager getChallengeManager() {
		return challengeManager;
	}

	public YamlConfiguration getConfiguration() {
		return config;
	}

	private void registerDefaultChallenges() {
		ChallengeManager manager = this.challengeManager;
		YamlConfiguration challengeConfig = this.challengeConfig;

		manager.registerChallenge(new NoDamageChallenge(this, challengeConfig), challengeConfig);
		manager.registerChallenge(new VegitarianChallenge(this, challengeConfig), challengeConfig);
		manager.registerChallenge(new AnimalLoverChallenge(this, challengeConfig), challengeConfig);
		manager.registerChallenge(new VampireChallenge(this, challengeConfig), challengeConfig);
	}

	private void loadConfiguration() {
		if (!this.getDataFolder().exists())
			this.getDataFolder().mkdirs();

		Optional<File> challengeOptional = FileUtils.copyResource(this, "challenges.yml");
		challengeOptional.ifPresent(file -> this.challengeConfig = YamlConfiguration.loadConfiguration(file));

		Optional<File> configOptional = FileUtils.copyResource(this, "config.yml");
		configOptional.ifPresent(file -> this.config = YamlConfiguration.loadConfiguration(file));

		Optional<File> messageOptional = FileUtils.copyResource(this, "messages.yml");
		messageOptional.ifPresent(file -> this.messageConfig = YamlConfiguration.loadConfiguration(file));
	}

	public String getMessage(String key) {
		return ChatColor.translateAlternateColorCodes('&', this.messageConfig.getString(key, ""));
	}

}
