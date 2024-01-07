package me.jishuna.challengeme.challenge.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
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
import me.jishuna.jishlib.util.Utils;

@RegisterChallenge
public class ChunkEffectsChallenge extends Challenge implements TickingChallenge {
    private final Map<UUID, Data> cache = new HashMap<>();

    @ConfigEntry("disabled-effects")
    @Comment("A list of effects that cannot be randomly chosen")
    private Set<NamespacedKey> disabledEffects = Set
            .of(PotionEffectType.CONFUSION.getKey(), PotionEffectType.HARM.getKey(), PotionEffectType.HEAL.getKey());

    @ConfigEntry("max-level")
    @Comment("The maximum level that can be randomly chosen for an effect")
    private int maxLevel = 2;

    private List<PotionEffectType> types = List.of();

    public ChunkEffectsChallenge() {
        super("chunk_effects");

        this.description = List.of(ChatColor.GRAY + "Each chunk will give you a random potion effect.");

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
        long chunk = Utils.getChunkKey(bukkitPlayer.getLocation().getChunk());
        Data data = this.cache.get(bukkitPlayer.getUniqueId());

        if (data == null || data.chunk != chunk) {
            if (data != null) {
                bukkitPlayer.removePotionEffect(data.type);
            }

            Random random = new Random(chunk);
            PotionEffectType type = this.types.get(random.nextInt(this.types.size()));

            bukkitPlayer.addPotionEffect(new PotionEffect(type, PotionEffect.INFINITE_DURATION, random.nextInt(this.maxLevel), true));
            this.cache.put(bukkitPlayer.getUniqueId(), new Data(chunk, type));
        }
    }

    private void onPlayerQuit(PlayerQuitEvent event, ChallengePlayer player) {
        onDeactivate(player);
    }

    public static record Data(long chunk, PotionEffectType type) {
    }
}
