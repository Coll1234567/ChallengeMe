package me.jishuna.challengeme.challenge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import me.jishuna.challengeme.listener.EventManager;
import me.jishuna.challengeme.player.ChallengePlayer;
import me.jishuna.jishlib.ClassScanner;
import me.jishuna.jishlib.JishLib;
import me.jishuna.jishlib.datastructure.Registry;

public class ChallengeRegistry extends Registry<NamespacedKey, Challenge> {
    private final Map<Class<? extends Event>, List<Challenge>> challengesByEvent = new HashMap<>();
    private final EventManager eventManager = new EventManager(JishLib.getPlugin());

    @Override
    public void register(NamespacedKey key, Challenge challenge, boolean replace) {
        super.register(key, challenge, replace);

        challenge.reload();

        if (challenge.isEnabled()) {
            for (Class<? extends Event> eventClass : challenge.getEventClasses()) {
                this.challengesByEvent.computeIfAbsent(eventClass, k -> new ArrayList<>()).add(challenge);
            }

            this.eventManager.registerEvents(challenge);
        }
    }

    public void handleEvent(Event event, ChallengePlayer player) {
        List<Challenge> challenges = this.challengesByEvent.get(event.getClass());
        if (challenges == null) {
            return;
        }

        challenges.forEach(challenge -> challenge.handleEvent(event, player));
    }

    public void discoverChallenges() {
        ClassScanner<Challenge, RegisterChallenge> scanner = new ClassScanner<>(this.getClass().getClassLoader(), Challenge.class, RegisterChallenge.class);
        scanner.forEach(challenge -> register(Challenge::getKey, challenge));

        JishLib.getLogger().log(Level.INFO, "Registered {0} default challenges.", size());
    }
}
