package me.jishuna.challengeme.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import me.jishuna.challengeme.ChallengeMe;
import me.jishuna.jishlib.command.SimpleCommandHandler;

public class ReloadCommand extends SimpleCommandHandler {
    private final ChallengeMe plugin;

    protected ReloadCommand(ChallengeMe plugin) {
        super("challengeme.reload");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.plugin.reload();
        return true;
    }
}
