package me.jishuna.challengeme;

import java.io.File;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;

import me.jishuna.challengeme.api.challenge.ChallengeManager;
import me.jishuna.challengeme.api.cooldowns.CooldownManager;
import me.jishuna.challengeme.api.inventory.CustomInventoryManager;
import me.jishuna.challengeme.api.player.PlayerManager;
import me.jishuna.challengeme.commands.ChallengeCommand;
import me.jishuna.challengeme.listeners.ChallengeListener;
import me.jishuna.challengeme.packets.PacketAdapaterLivingSpawn;
import me.jishuna.challengeme.runnables.TickingChallengeRunnable;
import me.jishuna.commonlib.FileUtils;
import net.md_5.bungee.api.ChatColor;

public class ChallengeMe extends JavaPlugin {

	private final int DELAY = 5;

	private ChallengeManager challengeManager;
	private PlayerManager playerManager;
	private CustomInventoryManager inventoryManager;
	private CooldownManager cooldownManager;

	private YamlConfiguration cateogryConfig;
	private YamlConfiguration challengeConfig;
	private YamlConfiguration config;
	private YamlConfiguration messageConfig;

	private TickingChallengeRunnable challengeRunnable;

	@Override
	public void onEnable() {
		loadConfiguration();
		PluginKeys.initialize(this);

		this.cooldownManager = new CooldownManager();

		this.challengeManager = new ChallengeManager(this);
		this.challengeManager.reloadCategories();
		this.challengeManager.reloadChallenges();
		
		this.inventoryManager = new CustomInventoryManager(this);
		this.inventoryManager.cacheCategoryGUI();

		this.playerManager = new PlayerManager(this);
		this.playerManager.registerListeners();

		Bukkit.getPluginManager().registerEvents(this.inventoryManager, this);
		Bukkit.getPluginManager().registerEvents(new ChallengeListener(this.playerManager), this);

		this.challengeRunnable = new TickingChallengeRunnable(this);

		this.challengeRunnable.runTaskTimer(this, DELAY, DELAY);

		getCommand("challenges").setExecutor(new ChallengeCommand(this));

		registerPacketListeners();
	}

	@Override
	public void onDisable() {
		this.playerManager.saveAllPlayers(true);

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

	public CooldownManager getCooldownManager() {
		return cooldownManager;
	}

	public YamlConfiguration getChallengeConfig() {
		return challengeConfig;
	}

	public YamlConfiguration getCateogryConfig() {
		return cateogryConfig;
	}

	public YamlConfiguration getConfiguration() {
		return config;
	}

	private void registerPacketListeners() {
		ProtocolManager manager = ProtocolLibrary.getProtocolManager();

		manager.addPacketListener(new PacketAdapaterLivingSpawn(this, ListenerPriority.NORMAL));
	}

	private void loadConfiguration() {
		if (!this.getDataFolder().exists())
			this.getDataFolder().mkdirs();

		Optional<File> challengeOptional = FileUtils.copyResource(this, "challenges.yml");
		challengeOptional.ifPresent(file -> this.challengeConfig = YamlConfiguration.loadConfiguration(file));

		Optional<File> categoryOptional = FileUtils.copyResource(this, "categories.yml");
		categoryOptional.ifPresent(file -> this.cateogryConfig = YamlConfiguration.loadConfiguration(file));

		Optional<File> configOptional = FileUtils.copyResource(this, "config.yml");
		configOptional.ifPresent(file -> this.config = YamlConfiguration.loadConfiguration(file));

		Optional<File> messageOptional = FileUtils.copyResource(this, "messages.yml");
		messageOptional.ifPresent(file -> this.messageConfig = YamlConfiguration.loadConfiguration(file));
	}

	public String getMessage(String key) {
		return ChatColor.translateAlternateColorCodes('&', this.messageConfig.getString(key, ""));
	}

}
