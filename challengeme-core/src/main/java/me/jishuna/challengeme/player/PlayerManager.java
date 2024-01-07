package me.jishuna.challengeme.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

public class PlayerManager {
    public static final PlayerManager INSTANCE = new PlayerManager();

    private final Map<UUID, ChallengePlayer> players = new HashMap<>();

    private PlayerManager() {
    }

    public void tick() {
        this.players.values().forEach(ChallengePlayer::tick);
    }

    public void addPlayer(Player player) {
        this.players.computeIfAbsent(player.getUniqueId(), k -> new ChallengePlayer(player));
    }

    public ChallengePlayer getPlayer(Player player) {
        return this.players.computeIfAbsent(player.getUniqueId(), k -> new ChallengePlayer(player));
    }

    public void removePlayer(Player player) {
        this.players.remove(player.getUniqueId());
    }
}
