package me.jishuna.challengeme;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;

import me.jishuna.challengeme.api.challenge.ChallengeManager;
import me.jishuna.challengeme.api.inventory.CustomInventoryManager;
import me.jishuna.challengeme.api.listener.EventManager;
import me.jishuna.challengeme.api.player.PlayerManager;
import me.jishuna.challengeme.commands.ChallengeCommand;
import me.jishuna.challengeme.nms.NMSAdapter;
import me.jishuna.challengeme.packets.PacketAdapterLivingSpawn;
import me.jishuna.challengeme.runnables.TickingChallengeRunnable;
import me.jishuna.commonlib.utils.FileUtils;
import me.jishuna.commonlib.utils.VersionUtils;
import net.md_5.bungee.api.ChatColor;

public class ChallengeMe extends JavaPlugin {

	private static NMSAdapter adapter;

	private final int DELAY = 5;

	private ChallengeManager challengeManager;
	private PlayerManager playerManager;
	private CustomInventoryManager inventoryManager;
	private EventManager eventManager;

	private YamlConfiguration cateogryConfig;
	private YamlConfiguration challengeConfig;
	private YamlConfiguration config;
	private YamlConfiguration messageConfig;

	private TickingChallengeRunnable challengeRunnable;

	@Override
	public void onEnable() {
		initializeNMSAdapter();
		loadConfiguration();
		PluginKeys.initialize(this);

		this.challengeManager = new ChallengeManager(this);
		this.challengeManager.reloadCategories();
		this.challengeManager.reloadChallenges();

		this.inventoryManager = new CustomInventoryManager(this);
		this.inventoryManager.cacheCategoryGUI();

		this.playerManager = new PlayerManager(this);
		this.playerManager.registerListeners();

		this.eventManager = new EventManager(this);

		Bukkit.getPluginManager().registerEvents(this.inventoryManager, this);

		this.challengeRunnable = new TickingChallengeRunnable(this);

		this.challengeRunnable.runTaskTimer(this, DELAY, DELAY);

		getCommand("challenges").setExecutor(new ChallengeCommand(this));

		registerPacketListeners();
	}

	private void initializeNMSAdapter() {
		String version = VersionUtils.getServerVersion();

		try {
			adapter = (NMSAdapter) Class.forName("me.jishuna.challengeme.nms.NMSAdapter_" + version).newInstance();
			getLogger().info("Version detected: " + version);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			getLogger().severe("Server version \"" + version + "\" is unsupported! Check the plugin page for updates.");
			getLogger().severe("Plugin will now be disabled.");

			Bukkit.getPluginManager().disablePlugin(this);
		}
	}

	@Override
	public void onDisable() {
		this.playerManager.saveAllPlayers(true);

		this.challengeRunnable.cancel();
	}

	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	public EventManager getEventManager() {
		return eventManager;
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

	public static NMSAdapter getNMSAdapter() {
		return adapter;
	}

}
