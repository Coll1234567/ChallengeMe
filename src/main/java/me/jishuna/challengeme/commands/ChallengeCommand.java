package me.jishuna.challengeme.commands;

import java.util.Optional;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jishuna.challengeme.ChallengeMe;
import me.jishuna.challengeme.api.player.ChallengePlayer;

public class ChallengeCommand implements CommandExecutor {
	private final ChallengeMe plugin;

	public ChallengeCommand(ChallengeMe plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}

		Player player = (Player) sender;

		Optional<ChallengePlayer> playerOptional = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());

		if (playerOptional.isPresent()) {
			this.plugin.getInventoryManager().openGui(player, this.plugin.getInventoryManager().getCategoryGUI());
		}
		return true;
	}

}
