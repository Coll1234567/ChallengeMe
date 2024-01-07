package me.jishuna.challengeme.player;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.entity.Player;
import me.jishuna.challengeme.Constants;
import me.jishuna.challengeme.Registries;
import me.jishuna.challengeme.challenge.Challenge;
import me.jishuna.challengeme.challenge.TickingChallenge;

public class ChallengePlayer {
    private final Set<Challenge> activeChallenges;
    private final Player player;

    public ChallengePlayer(Player player) {
        this.player = player;
        this.activeChallenges = player.getPersistentDataContainer().getOrDefault(Constants.CHALLENGES_KEY, Constants.CHALLENGES_TYPE, new HashSet<>());

        updateActive();
    }

    public void updateActive() {
        Iterator<Challenge> iterator = this.activeChallenges.iterator();
        while (iterator.hasNext()) {
            Challenge challenge = iterator.next();

            // Remove disabled challenges
            if (!challenge.isEnabled() && isChallengeActive(challenge)) {
                iterator.remove();
                challenge.onDeactivate(this);
            }
        }

        for (Challenge challenge : Registries.CHALLENGE.getValues()) {

            // Add forced challenges
            if (challenge.isForced() && !isChallengeActive(challenge)) {
                this.activeChallenges.add(challenge);
                challenge.onActivate(this);
            }
        }

        save();
    }

    public void tick() {
        for (Challenge challenge : this.activeChallenges) {
            if (challenge instanceof TickingChallenge ticking) {
                ticking.tick(this);
            }
        }
    }

    public boolean activateChallenge(Challenge challenge) {
        if (this.activeChallenges.add(challenge)) {
            challenge.onActivate(this);
            save();
            return true;
        }

        return false;
    }

    public boolean deactivateChallenge(Challenge challenge) {
        if (this.activeChallenges.remove(challenge)) {
            challenge.onDeactivate(this);
            save();
            return true;
        }

        return false;
    }

    public boolean isChallengeActive(Challenge challenge) {
        return this.activeChallenges.contains(challenge);
    }

    public void save() {
        getPlayer().getPersistentDataContainer().set(Constants.CHALLENGES_KEY, Constants.CHALLENGES_TYPE, this.activeChallenges);
    }

    public Player getPlayer() {
        return this.player;
    }
}
