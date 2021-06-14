package me.jishuna.challengeme.api.challenge;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import me.jishuna.commonlib.items.ItemParser;
import me.jishuna.commonlib.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;

public class Category {
	private final String key;
	private final String name;
	private List<String> description;
	private final ItemStack icon;

	public Category(String key, ConfigurationSection categorySection) {
		this.key = key;
		
		this.name = ChatColor.translateAlternateColorCodes('&', categorySection.getString("name", ""));
		this.icon = ItemParser.parseItem(categorySection.getString("material", ""), Material.DIAMOND);
		
		String desc = ChatColor.translateAlternateColorCodes('&', categorySection.getString("description", ""));

		List<String> descriptionList = new ArrayList<>();

		for (String line : desc.split("\\\\n")) {
			descriptionList.addAll(StringUtils.splitString(line, 30));
		}
		this.description = descriptionList;
	}

	public String getName() {
		return name;
	}

	public List<String> getDescription() {
		return description;
	}

	public ItemStack getIcon() {
		return icon;
	}

	public String getKey() {
		return key;
	}

}
