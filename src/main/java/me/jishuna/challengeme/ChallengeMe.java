package me.jishuna.challengeme;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;

import me.jishuna.challengeme.api.challenge.ChallengeManager;
import me.jishuna.challengeme.api.inventory.CustomInventoryManager;
import me.jishuna.challengeme.api.player.PlayerManager;
import me.jishuna.challengeme.commands.ChallengeCommand;
import me.jishuna.challengeme.listeners.BlockListeners;
import me.jishuna.challengeme.listeners.CombatListeners;
import me.jishuna.challengeme.listeners.PlayerListeners;
import me.jishuna.challengeme.packets.PacketAdapterLivingSpawn;
import me.jishuna.challengeme.runnables.TickingChallengeRunnable;
import me.jishuna.commonlib.FileUtils;
import net.md_5.bungee.api.ChatColor;

public class ChallengeMe extends JavaPlugin {

	private final int DELAY = 5;

	private ChallengeManager challengeManager;
	private PlayerManager playerManager;
	private CustomInventoryManager inventoryManager;

	private YamlConfiguration cateogryConfig;
	private YamlConfiguration challengeConfig;
	private YamlConfiguration config;
	private YamlConfiguration messageConfig;

	private TickingChallengeRunnable challengeRunnable;

	@Override
	public void onEnable() {
		loadConfiguration();
		PluginKeys.initialize(this);

		this.challengeManager = new ChallengeManager(this);
		this.challengeManager.reloadCategories();
		this.challengeManager.reloadChallenges();

		this.inventoryManager = new CustomInventoryManager(this);
		this.inventoryManager.cacheCategoryGUI();

		this.playerManager = new PlayerManager(this);
		this.playerManager.registerListeners();

		Bukkit.getPluginManager().registerEvents(this.inventoryManager, this);

		Bukkit.getPluginManager().registerEvents(new PlayerListeners(this.playerManager), this);
		Bukkit.getPluginManager().registerEvents(new BlockListeners(this.playerManager), this);
		Bukkit.getPluginManager().registerEvents(new CombatListeners(this.playerManager), this);

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

		manager.addPacketListener(new PacketAdapterLivingSpawn(this, ListenerPriority.NORMAL));
	}

	private void loadConfiguration() {
		if (!this.getDataFolder().exists())
			this.getDataFolder().mkdirs();

		FileUtils.loadResource(this, "config.yml").ifPresent(config -> this.config = config);
		FileUtils.loadResource(this, "messages.yml").ifPresent(config -> this.messageConfig = config);
		FileUtils.loadResource(this, "categories.yml").ifPresent(config -> this.cateogryConfig = config);
		FileUtils.loadResource(this, "challenges.yml").ifPresent(config -> this.challengeConfig = config);
	}

	public String getMessage(String key) {
		return ChatColor.translateAlternateColorCodes('&', this.messageConfig.getString(key, ""));
	}

}
