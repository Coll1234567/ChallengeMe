package me.jishuna.challengeme.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.jishuna.challengeme.ChallengeMe;
import me.jishuna.challengeme.api.player.PlayerManager;
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
		this.plugin.getChallengeManager().reloadCategories();

		sender.sendMessage(ChatColor.GREEN + "Reloading Challenges.");
		this.plugin.getChallengeManager().reloadChallenges();

		sender.sendMessage(ChatColor.GREEN + "Reloading Inventories.");
		this.plugin.getInventoryManager().cacheCategoryGUI();

		PlayerManager playerManager = this.plugin.getPlayerManager();
		sender.sendMessage(ChatColor.GREEN + "Saving Current Players.");
		playerManager.saveAllPlayers(false);

		sender.sendMessage(ChatColor.GREEN + "Reloading Current Players.");
		Bukkit.getOnlinePlayers().forEach(player -> playerManager.loadPlayerData(player));

		sender.sendMessage(ChatColor.GREEN + "Reload Complete!");
		return true;
	}

}
