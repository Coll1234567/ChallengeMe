package me.jishuna.challengeme.challenge.impl;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import me.jishuna.challengeme.challenge.Challenge;
import me.jishuna.challengeme.challenge.RegisterChallenge;
import me.jishuna.challengeme.challenge.TickingChallenge;
import me.jishuna.challengeme.player.ChallengePlayer;
import me.jishuna.jishlib.config.annotation.Comment;
import me.jishuna.jishlib.config.annotation.ConfigEntry;

@RegisterChallenge
public class RandomEffectsChallenge extends Challenge implements TickingChallenge {
    private final Map<UUID, Data> cache = new HashMap<>();

    @ConfigEntry("disabled-effects")
    @Comment("A list of effects that cannot be randomly chosen")
    private Set<NamespacedKey> disabledEffects = Set.of(PotionEffectType.CONFUSION.getKey());

    @ConfigEntry("max-level")
    @Comment("The maximum level that can be randomly chosen for an effect")
    private int maxLevel = 2;

    private List<PotionEffectType> types = List.of();

    public RandomEffectsChallenge() {
        super("random_effects");

        this.description = List.of(ChatColor.GRAY + "You will recieve a random potion effect every 60 seconds.");

        registerEventConsumer(PlayerQuitEvent.class, this::onPlayerQuit);
    }

    @Override
    public void reload() {
        super.reload();

        this.types = Registry.EFFECT.stream().filter(effect -> !this.disabledEffects.contains(effect.getKey())).toList();
    }

    @Override
    public void onDeactivate(ChallengePlayer player) {
        Player bukkitPlayer = player.getPlayer();
        Data data = this.cache.remove(bukkitPlayer.getUniqueId());
        if (data != null) {
            bukkitPlayer.removePotionEffect(data.type);
        }
    }

    @Override
    public void tick(ChallengePlayer player) {
        Player bukkitPlayer = player.getPlayer();
        Instant now = Instant.now();
        Data data = this.cache.get(bukkitPlayer.getUniqueId());

        if (data == null || data.instant.isBefore(now)) {
            if (data != null) {
                bukkitPlayer.removePotionEffect(data.type);
            }

            Random random = ThreadLocalRandom.current();
            PotionEffectType type = this.types.get(random.nextInt(this.types.size()));
            int duration = type.isInstant() ? 1 : 60 * 20;

            bukkitPlayer.addPotionEffect(new PotionEffect(type, duration, random.nextInt(this.maxLevel), true));
            this.cache.put(bukkitPlayer.getUniqueId(), new Data(now.plusSeconds(60), type));
        }
    }

    private void onPlayerQuit(PlayerQuitEvent event, ChallengePlayer player) {
        onDeactivate(player);
    }

    public static record Data(Instant instant, PotionEffectType type) {
    }
}
