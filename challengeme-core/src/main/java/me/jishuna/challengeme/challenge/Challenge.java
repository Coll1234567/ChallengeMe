package me.jishuna.challengeme.challenge;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.util.ChatPaginator;
import me.jishuna.challengeme.player.ChallengePlayer;
import me.jishuna.jishlib.JishLib;
import me.jishuna.jishlib.config.ConfigApi;
import me.jishuna.jishlib.config.annotation.Comment;
import me.jishuna.jishlib.config.annotation.ConfigEntry;
import me.jishuna.jishlib.config.annotation.PostLoad;
import me.jishuna.jishlib.util.StringUtils;

public abstract class Challenge {
    protected static final File CHALLENGE_FOLDER = new File(JishLib.getPlugin().getDataFolder(), "Challenges");
    private final Map<Class<? extends Event>, List<BiConsumer<? extends Event, ChallengePlayer>>> eventConsumers = new HashMap<>();

    protected final String name;
    protected final NamespacedKey key;

    @ConfigEntry("enabled")
    @Comment("Allows fully disabling this challenge, disabled challenges cannot be selected")
    protected boolean enabled = true;

    @ConfigEntry("forced")
    @Comment("Forced challenges will always be enabled for all players")
    protected boolean forced = false;

    @ConfigEntry("description")
    @Comment("Allows modifying the description of this challenge")
    protected List<String> description = List.of();

    protected Challenge(String name) {
        this.name = name;
        this.key = NamespacedKey.fromString("challengeme:" + name);
    }

    public void reload() {
        ConfigApi
                .createReloadable(new File(CHALLENGE_FOLDER, this.name + ".yml"), this)
                .saveDefaults()
                .load();
    }

    @PostLoad
    public void postLoad() {
        this.description = this.description
                .stream()
                .map(line -> ChatPaginator.wordWrap(line, 30))
                .flatMap(Arrays::stream)
                .toList();
    }

    public <T extends Event> void registerEventConsumer(Class<T> clazz, BiConsumer<T, ChallengePlayer> consumer) {
        this.eventConsumers.computeIfAbsent(clazz, k -> new ArrayList<>()).add(consumer);
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void handleEvent(T event, ChallengePlayer player) {
        List<BiConsumer<? extends Event, ChallengePlayer>> eventListeners = this.eventConsumers.get(event.getClass());
        if (eventListeners != null) {
            eventListeners.forEach(listener -> ((BiConsumer<T, ChallengePlayer>) listener).accept(event, player));
        }
    }

    public Set<Class<? extends Event>> getEventClasses() {
        return Collections.unmodifiableSet(this.eventConsumers.keySet());
    }

    public void onActivate(ChallengePlayer challengePlayer) {
        // Do nothing by default
    }

    public void onDeactivate(ChallengePlayer challengePlayer) {
        // Do nothing by default
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isForced() {
        return this.forced;
    }

    public String getName() {
        return this.name;
    }

    public String getReadableName() {
        return StringUtils.capitalizeAll(this.name.replace('_', ' '));
    }

    public NamespacedKey getKey() {
        return this.key;
    }

    public List<String> getDescription() {
        return this.description;
    }
}
