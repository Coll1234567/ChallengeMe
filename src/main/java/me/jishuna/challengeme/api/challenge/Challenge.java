package me.jishuna.challengeme.api.challenge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import me.jishuna.challengeme.api.ChallengeMeAPI;
import me.jishuna.challengeme.api.event.EventWrapper;
import me.jishuna.challengeme.api.player.ChallengePlayer;
import me.jishuna.commonlib.items.ItemParser;
import me.jishuna.commonlib.utils.FileUtils;
import me.jishuna.commonlib.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;

public abstract class Challenge {

	private final Plugin owningPlugin;

	private final String key;
	private String name;
	private String message;
	private String difficulty;
	private String category;
	private List<String> description;
	private ItemStack icon;
	private boolean enabled = true;
	private boolean forced = false;

	private ChallengePacketData challengePacketData;

	private final Multimap<Class<? extends Event>, EventWrapper<? extends Event>> handlerMap = ArrayListMultimap
			.create();

	protected Challenge(Plugin owner, String key) {
		this.key = key;
		this.owningPlugin = owner;

		if (ChallengeMeAPI.hasProtcolLib()) {
			this.challengePacketData = new ChallengePacketData();
		}

		this.reload();
	}

	public void reload() {
		YamlConfiguration challengeConfig = loadConfig(this.owningPlugin, this.key);

		if (challengeConfig != null) {
			this.loadData(challengeConfig);
		}
	}

	protected void loadData(YamlConfiguration challengeConfig) {
		this.category = challengeConfig.getString("category", "");

		this.enabled = challengeConfig.getBoolean("enabled", true);
		this.forced = challengeConfig.getBoolean("forced", false);

		this.name = ChatColor.translateAlternateColorCodes('&', challengeConfig.getString("name", ""));
		this.message = ChatColor.translateAlternateColorCodes('&', challengeConfig.getString("message", ""));
		this.difficulty = ChatColor.translateAlternateColorCodes('&', challengeConfig.getString("difficulty", ""));

		this.icon = ItemParser.parseItem(challengeConfig.getString("material", ""), Material.DIAMOND);

		String desc = ChatColor.translateAlternateColorCodes('&', challengeConfig.getString("description", ""));

		for (String configKey : challengeConfig.getKeys(false)) {
			desc = desc.replace("%" + configKey + "%", challengeConfig.getString(configKey));
		}

		List<String> descriptionList = new ArrayList<>();

		for (String line : desc.split("\\\\n")) {
			descriptionList.addAll(StringUtils.splitString(line, 30));
		}
		this.description = descriptionList;
	}

	public Plugin getOwningPlugin() {
		return owningPlugin;
	}

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public List<String> getDescription() {
		return description;
	}

	public String getMessage() {
		return message;
	}

	public String getDifficulty() {
		return difficulty;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public ItemStack getIcon() {
		return icon;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isForced() {
		return forced;
	}

	public ChallengePacketData getChallengePacketData() {
		return this.challengePacketData;
	}

	public <T extends Event> void addEventHandler(Class<T> type, BiConsumer<T, ChallengePlayer> consumer) {
		this.handlerMap.put(type, new EventWrapper<>(type, consumer));
	}

	public <T extends Event> Collection<EventWrapper<? extends Event>> getEventHandlers(Class<T> type) {
		return this.handlerMap.get(type);
	}

	private YamlConfiguration loadConfig(Plugin owner, String key) {
		Optional<YamlConfiguration> optional = FileUtils.loadResource(owner, "challenges/" + key + ".yml");

		return optional.isPresent() ? optional.get() : null;
	}
}
