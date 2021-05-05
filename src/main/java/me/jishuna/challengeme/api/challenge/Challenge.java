package me.jishuna.challengeme.api.challenge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import me.jishuna.challengeme.api.event.EventWrapper;
import me.jishuna.challengeme.api.packets.PacketWrapper;
import me.jishuna.commonlib.ItemParser;
import me.jishuna.commonlib.StringUtils;
import net.md_5.bungee.api.ChatColor;

public abstract class Challenge {

	private final Plugin owningPlugin;

	private final String key;
	private final String name;
	private final String message;
	private String category;
	private List<String> description;
	private final ItemStack icon;
	private final boolean enabled;
	private final boolean forced;

	private final Multimap<Class<? extends Event>, EventWrapper<? extends Event>> handlerMap = ArrayListMultimap
			.create();
	private final Multimap<PacketType, PacketWrapper> packetMap = ArrayListMultimap.create();

	public Challenge(Plugin owner, String key, YamlConfiguration challengeConfig) {
		this(owner, key, challengeConfig.getConfigurationSection(key));
	}

	public Challenge(Plugin owner, String key, ConfigurationSection challengeSection) {
		this.owningPlugin = owner;
		this.key = key;

		this.category = challengeSection.getString("category", "");

		this.enabled = challengeSection.getBoolean("enabled", true);
		this.forced = challengeSection.getBoolean("forced", false);

		this.name = ChatColor.translateAlternateColorCodes('&', challengeSection.getString("name", ""));
		this.message = ChatColor.translateAlternateColorCodes('&', challengeSection.getString("message", ""));

		this.icon = ItemParser.parseItem(challengeSection.getString("material", ""), Material.DIAMOND);

		String description = ChatColor.translateAlternateColorCodes('&', challengeSection.getString("description", ""));

		for (String configKey : challengeSection.getKeys(false)) {
			description = description.replace("%" + configKey + "%", challengeSection.getString(configKey));
		}

		List<String> desc = new ArrayList<>();

		for (String line : description.split("\\\\n")) {
			desc.addAll(StringUtils.splitString(line, 30));
		}
		this.description = desc;
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

	public <T extends Event> void addEventHandler(Class<T> type, BiConsumer<T, Player> consumer) {
		this.handlerMap.put(type, new EventWrapper<>(type, consumer));
	}

	public <T extends Event> Collection<EventWrapper<? extends Event>> getEventHandlers(Class<T> type) {
		return this.handlerMap.get(type);
	}

	public void addPacketHandler(PacketWrapper wrapper) {
		this.packetMap.put(wrapper.getType(), wrapper);
	}

	public Collection<PacketWrapper> getPacketHandlers(PacketType type) {
		return this.packetMap.get(type);
	}

}
