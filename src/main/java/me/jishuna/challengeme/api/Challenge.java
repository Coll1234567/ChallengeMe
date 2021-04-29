package me.jishuna.challengeme.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import me.jishuna.commonlib.StringUtils;
import net.md_5.bungee.api.ChatColor;

public abstract class Challenge {

	private final Plugin owningPlugin;
	private final String key;
	private final String name;
	private final List<String> description;
	private final Material icon;

	private final Multimap<Class<? extends Event>, EventWrapper<? extends Event>> handlerMap = ArrayListMultimap
			.create();

	public Challenge(Plugin owner, String key, YamlConfiguration messageConfig) {
		String name = ChatColor.translateAlternateColorCodes('&',
				messageConfig.getString("challenges." + key + ".name", ""));
		String description = ChatColor.translateAlternateColorCodes('&',
				messageConfig.getString("challenges." + key + ".description", ""));

		Material material = Material.matchMaterial(messageConfig.getString("challenges." + key + ".material"));
		this.icon = material != null ? material : Material.DIAMOND;

		this.owningPlugin = owner;
		this.key = key;
		this.name = name;

		List<String> desc = new ArrayList<>();

		System.out.println(description);
		for (String line : description.split("\\\\n")) {
			System.out.println(line);
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

	public Material getIcon() {
		return icon;
	}

	public <T extends Event> void addEventHandler(Class<T> type, Consumer<T> consumer) {
		this.handlerMap.put(type, new EventWrapper<>(type, consumer));
	}

	public <T extends Event> Collection<EventWrapper<? extends Event>> getEventHandlers(Class<T> type) {
		return this.handlerMap.get(type);
	}

}
