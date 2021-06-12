package me.jishuna.challengeme.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class ChallengeMeAPI {

	public static boolean hasProtcolLib() {
		Plugin plugin = Bukkit.getPluginManager().getPlugin("ProtocolLib");

		return plugin != null && plugin.isEnabled();
	}

}
