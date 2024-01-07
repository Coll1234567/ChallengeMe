package me.jishuna.challengeme.listener;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import me.jishuna.challengeme.Registries;
import me.jishuna.challengeme.challenge.Challenge;
import me.jishuna.challengeme.player.ChallengePlayer;
import me.jishuna.challengeme.player.PlayerManager;

public class EventManager implements Listener {
    private final Set<Class<? extends Event>> registered = new HashSet<>();
    private final Plugin plugin;

    public EventManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void registerEvents(Challenge challenge) {
        PluginManager manager = Bukkit.getPluginManager();

        for (Class<? extends Event> clazz : challenge.getEventClasses()) {
            if (this.registered.contains(clazz)) {
                continue;
            }

            manager.registerEvent(clazz, this, EventPriority.NORMAL, (listener, event) -> {
                if (clazz.isInstance(event)) {
                    handleEvent(event);
                }
            }, this.plugin);
            this.registered.add(clazz);
        }
    }

    private void handleEvent(Event event) {
        ChallengePlayer challengePlayer = null;
        if (event instanceof PlayerEvent playerEvent) {
            challengePlayer = PlayerManager.INSTANCE.getPlayer(playerEvent.getPlayer());
        }

        if (event instanceof EntityEvent entityEvent) {
            if (!(entityEvent.getEntity() instanceof Player player)) {
                return;
            }
            challengePlayer = PlayerManager.INSTANCE.getPlayer(player);
        }

        Registries.CHALLENGE.handleEvent(event, challengePlayer);
    }
}
