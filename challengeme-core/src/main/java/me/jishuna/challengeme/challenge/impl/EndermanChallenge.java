package me.jishuna.challengeme.challenge.impl;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import me.jishuna.challengeme.challenge.Challenge;
import me.jishuna.challengeme.challenge.RegisterChallenge;
import me.jishuna.challengeme.challenge.TickingChallenge;
import me.jishuna.challengeme.player.ChallengePlayer;
import me.jishuna.jishlib.config.annotation.Comment;
import me.jishuna.jishlib.config.annotation.ConfigEntry;

@RegisterChallenge
public class EndermanChallenge extends Challenge implements TickingChallenge {

    @ConfigEntry("teleport-chance")
    @Comment("The chance to teleport when taking damage")
    @Comment("Ranges from 0.0 (0%) to 1.0 (100%)")
    private double teleportChance = 0.25;

    public EndermanChallenge() {
        super("enderman");

        this.description = List
                .of(ChatColor.GRAY + "Touching water hurts you and causes you to randomly teleport somewhere nearby.", "",
                        ChatColor.GRAY + "Taking damage may also cause you to randomly teleport somewhere nearby.");

        registerEventConsumer(EntityDamageEvent.class, this::onEntityDamage);
    }

    @Override
    public void tick(ChallengePlayer player) {
        Player bukkitPlayer = player.getPlayer();
        if (bukkitPlayer.isInWater()) {
            bukkitPlayer.damage(2);
            teleport(bukkitPlayer);
        }
    }

    private void onEntityDamage(EntityDamageEvent event, ChallengePlayer player) {
        if (ThreadLocalRandom.current().nextDouble() < this.teleportChance) {
            teleport(player.getPlayer());
        }
    }

    private void teleport(Player player) {
        World world = player.getWorld();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Location location = player.getLocation();

        location.setX(location.getBlockX() + random.nextInt(-32, 32));
        location.setZ(location.getBlockZ() + random.nextInt(-32, 32));
        location.setY(Math.min(world.getHighestBlockYAt(location, HeightMap.MOTION_BLOCKING), location.getBlockY() + 32));

        while (location.getBlockY() > 0 && !location.getBlock().getType().isSolid()) {
            location.subtract(0, 1, 0);
        }

        if (location.getBlock().getRelative(0, 1, 0).isPassable() && location.getBlock().getRelative(0, 2, 0).isPassable()) {
            player.teleport(location.add(0.5, 1, 0.5));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.HOSTILE, 1f, 1f);
        }
    }
}
