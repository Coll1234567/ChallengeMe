package me.jishuna.challengeme.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.jishuna.challengeme.ChallengeMe;
import me.jishuna.commonlib.commands.SimpleCommandHandler;
import net.md_5.bungee.api.ChatColor;

public class ReloadCommand extends SimpleCommandHandler {

	private final ChallengeMe plugin;

	public ReloadCommand(ChallengeMe plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!sender.hasPermission("challengeme.reload")) {
			sender.sendMessage(this.plugin.getMessage("no-permission"));
			return true;
		}

		sender.sendMessage(ChatColor.GREEN + "Reloading Configuration Files.");
		this.plugin.loadConfiguration();

		sender.sendMessage(ChatColor.GREEN + "Reloading Categories.");
		sender.sendMessage(ChatColor.GREEN + "Reloading Challenges.");
		this.plugin.getChallengeManager().reload();

		sender.sendMessage(ChatColor.GREEN + "Reloading Inventories.");
		this.plugin.getInventoryManager().cacheCategoryGUI();

		sender.sendMessage(ChatColor.GREEN + "Reloading Current Players.");
		this.plugin.getPlayerManager().reloadPlayers();

		sender.sendMessage(ChatColor.GREEN + "Reload Complete!");
		return true;
	}

}
