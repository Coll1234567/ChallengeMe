package me.jishuna.challengeme;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.jishuna.challengeme.api.ChallengeMeAPI;
import me.jishuna.challengeme.api.challenge.ChallengeManager;
import me.jishuna.challengeme.api.inventory.CustomInventoryManager;
import me.jishuna.challengeme.api.listener.EventManager;
import me.jishuna.challengeme.api.packets.PacketManager;
import me.jishuna.challengeme.api.player.PlayerManager;
import me.jishuna.challengeme.commands.ChallengeCommand;
import me.jishuna.challengeme.commands.ChallengeMeCommandHandler;
import me.jishuna.challengeme.nms.NMSAdapter;
import me.jishuna.challengeme.runnables.TickingChallengeRunnable;
import me.jishuna.commonlib.language.MessageConfig;
import me.jishuna.commonlib.utils.FileUtils;
import me.jishuna.commonlib.utils.VersionUtils;

public class ChallengeMe extends JavaPlugin {

	private static NMSAdapter adapter;

	private final int DELAY = 5;

	private ChallengeManager challengeManager;
	private PlayerManager playerManager;
	private CustomInventoryManager inventoryManager;
	private EventManager eventManager;
	private PacketManager packetManager;

	private YamlConfiguration cateogryConfig;
	private YamlConfiguration config;
	private MessageConfig messageConfig;

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

		if (ChallengeMeAPI.hasProtcolLib()) {
			this.packetManager = new PacketManager(this);
		}

		this.eventManager = new EventManager(this);

		Bukkit.getPluginManager().registerEvents(this.inventoryManager, this);

		this.challengeRunnable = new TickingChallengeRunnable(this);

		this.challengeRunnable.runTaskTimer(this, DELAY, DELAY);

		int interval = this.config.getInt("auto-save-delay", 5) * 60 * 20;
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> this.playerManager.saveAllPlayers(false), interval,
				interval);

		getCommand("challenges").setExecutor(new ChallengeCommand(this));
		getCommand("challengeme").setExecutor(new ChallengeMeCommandHandler(this));

		initializeMetrics();
	}

	private void initializeNMSAdapter() {
		String version = VersionUtils.getServerVersion();

		try {
			adapter = (NMSAdapter) Class.forName("me.jishuna.challengeme.nms.NMSAdapter_" + version)
					.getDeclaredConstructor().newInstance();
			getLogger().info("Version detected: " + version);
		} catch (ReflectiveOperationException e) {
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

	public PacketManager getPacketManager() {
		return packetManager;
	}

	public CustomInventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public ChallengeManager getChallengeManager() {
		return challengeManager;
	}

	public YamlConfiguration getCateogryConfig() {
		return cateogryConfig;
	}

	public MessageConfig getMessageConfig() {
		return messageConfig;
	}

	public YamlConfiguration getConfiguration() {
		return config;
	}

	public void loadConfiguration() {
		if (!this.getDataFolder().exists())
			this.getDataFolder().mkdirs();

		FileUtils.loadResource(this, "config.yml").ifPresent(config -> this.config = config);
		FileUtils.loadResource(this, "categories.yml").ifPresent(config -> this.cateogryConfig = config);

		FileUtils.loadResourceFile(this, "messages.yml")
				.ifPresent(file -> this.messageConfig = new MessageConfig(file));
	}

	public String getMessage(String key) {
		return this.messageConfig.getString(key);
	}

	public static NMSAdapter getNMSAdapter() {
		return adapter;
	}

	private void initializeMetrics() {
		Metrics metrics = new Metrics(this, 11668);

		metrics.addCustomChart(new SimplePie("has_forced_challenges",
				() -> Boolean.toString(this.challengeManager.hasForcedChallenges())));

		metrics.addCustomChart(
				new SimplePie("has_protocollib", () -> Boolean.toString(ChallengeMeAPI.hasProtcolLib())));

	}

}
