package me.jishuna.challengeme.api.challenge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import me.jishuna.challengeme.api.event.EventWrapper;
import me.jishuna.commonlib.ItemParser;
import me.jishuna.commonlib.StringUtils;
import net.md_5.bungee.api.ChatColor;

public abstract class Challenge {

	private final Plugin owningPlugin;

	private final String key;
	private final String name;
	private final String message;
	private final List<String> description;
	private final ItemStack icon;

	private final Multimap<Class<? extends Event>, EventWrapper<? extends Event>> handlerMap = ArrayListMultimap
			.create();

	public Challenge(Plugin owner, String key, YamlConfiguration challengeConfig) {
		this.owningPlugin = owner;
		this.key = key;
		
		this.name = ChatColor.translateAlternateColorCodes('&',
				challengeConfig.getString("challenges." + key + ".name", ""));
		this.message = ChatColor.translateAlternateColorCodes('&',
				challengeConfig.getString("challenges." + key + ".message", ""));

		this.icon = ItemParser.parseItem(challengeConfig.getString("challenges." + key + ".material", ""),
				Material.DIAMOND);
		
		String description = ChatColor.translateAlternateColorCodes('&',
				challengeConfig.getString("challenges." + key + ".description", ""));
		
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

	public ItemStack getIcon() {
		return icon;
	}

	public <T extends Event> void addEventHandler(Class<T> type, BiConsumer<T, Player> consumer) {
		this.handlerMap.put(type, new EventWrapper<>(type, consumer));
	}

	public <T extends Event> Collection<EventWrapper<? extends Event>> getEventHandlers(Class<T> type) {
		return this.handlerMap.get(type);
	}

}
