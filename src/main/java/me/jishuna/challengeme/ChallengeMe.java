package me.jishuna.challengeme;

import java.io.File;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.jishuna.challengeme.api.ChallengeManager;
import me.jishuna.challengeme.api.PlayerManager;
import me.jishuna.challengeme.challenges.NoDamageChallenge;
import me.jishuna.challengeme.commands.ChallengeCommand;
import me.jishuna.commonlib.FileUtils;
import net.md_5.bungee.api.ChatColor;

public class ChallengeMe extends JavaPlugin {

	private ChallengeManager challengeManager;
	private PlayerManager playerManager;
	private YamlConfiguration challengeConfig;
	private CustomInventoryManager inventoryManager;

	@Override
	public void onEnable() {
		loadConfiguration();
		PluginKeys.initialize(this);

		this.inventoryManager = new CustomInventoryManager();
		Bukkit.getPluginManager().registerEvents(this.inventoryManager, this);

		this.challengeManager = new ChallengeManager(this);

		this.playerManager = new PlayerManager(this, this.challengeManager);
		this.playerManager.registerListeners();

		Bukkit.getPluginManager().registerEvents(new ChallengeListener(this.playerManager), this);

		getCommand("challenges").setExecutor(new ChallengeCommand(this));

		registerDefaultChallenges();
	}

	@Override
	public void onDisable() {

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

	public void setChallengeManager(ChallengeManager challengeManager) {
		this.challengeManager = challengeManager;
	}

	private void registerDefaultChallenges() {
		ChallengeManager manager = this.challengeManager;
		YamlConfiguration challengeConfig = this.challengeConfig;

		manager.registerChallenge(new NoDamageChallenge(this, challengeConfig), challengeConfig);
	}

	private void loadConfiguration() {
		if (!this.getDataFolder().exists())
			this.getDataFolder().mkdirs();

		Optional<File> configOptional = FileUtils.copyResource(this, "challenges.yml");
		configOptional.ifPresent(file -> this.challengeConfig = YamlConfiguration.loadConfiguration(file));
	}

	public String getMessage(String key) {
		return ChatColor.translateAlternateColorCodes('&', this.challengeConfig.getString(key));
	}

}
