package me.jishuna.challengeme.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.jishuna.challengeme.inventory.ChallengeSelectorInventory;
import me.jishuna.challengeme.player.ChallengePlayer;
import me.jishuna.challengeme.player.PlayerManager;
import me.jishuna.jishlib.command.SimpleCommandHandler;

public class ChallengeGUICommand extends SimpleCommandHandler {

    protected ChallengeGUICommand() {
        super("temp");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        ChallengePlayer challengePlayer = PlayerManager.INSTANCE.getPlayer(player);
        new ChallengeSelectorInventory(challengePlayer).open(player);
        return true;
    }
}
