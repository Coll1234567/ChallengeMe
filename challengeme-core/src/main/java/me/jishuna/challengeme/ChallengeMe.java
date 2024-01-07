package me.jishuna.challengeme;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import me.jishuna.challengeme.challenge.Challenge;
import me.jishuna.challengeme.command.ChallengeMeCommandHandler;
import me.jishuna.challengeme.listener.ConnectionListener;
import me.jishuna.challengeme.player.PlayerManager;
import me.jishuna.jishlib.JishLib;
import me.jishuna.jishlib.config.ConfigApi;
import me.jishuna.jishlib.inventory.InventoryAPI;
import me.jishuna.jishlib.message.MessageAPI;

public class ChallengeMe extends JavaPlugin {

    @Override
    public void onEnable() {
        JishLib.initialize(this);
        ConfigApi.initialize();
        MessageAPI.initialize("messages.lang");
        InventoryAPI.initialize();

        Registries.CHALLENGE.discoverChallenges();

        Bukkit.getPluginManager().registerEvents(new ConnectionListener(), this);

        getCommand("challengeme").setExecutor(new ChallengeMeCommandHandler(this));

        Bukkit.getScheduler().runTaskTimer(this, PlayerManager.INSTANCE::tick, 1, 1);
    }

    public void reload() {
        MessageAPI.reload();
        Registries.CHALLENGE.getValues().forEach(Challenge::reload);
    }
}
