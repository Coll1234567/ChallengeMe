package me.jishuna.challengeme.challenges;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.challenge.ToggleChallenge;
import me.jishuna.challengeme.api.player.ChallengePlayer;
import net.minecraft.server.v1_16_R3.DamageSource;

public class AquaticChallenge extends Challenge implements ToggleChallenge {
	private final ProtocolManager manager = ProtocolLibrary.getProtocolManager();

	private static final String KEY = "aquatic";

	public AquaticChallenge(Plugin owner) {
		super(owner, KEY, loadConfig(owner, KEY));

		addEventHandler(EntityAirChangeEvent.class, this::onAirChange);
	}

	private void onAirChange(EntityAirChangeEvent event, ChallengePlayer challengePlayer) {
		Player player = (Player) event.getEntity();

		int oldAir = player.getRemainingAir();
		int newAir = event.getAmount();

		int air = 0;
		if (oldAir > newAir) {
			air = Math.min(oldAir + 4, player.getMaximumAir() - 1);
		} else {
			air = Math.max(oldAir - 1, -20);
		}
		if (air <= -20) {
			air = 0;
			((CraftPlayer) player).getHandle().damageEntity(DamageSource.DROWN, 2);
			if (player.isDead()) {
				air = player.getMaximumAir() - 1;
			}
		}
		event.setAmount(air);

		if (player.isInWater() && player.getEyeLocation().getBlock().isLiquid()) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 11, 0, true, false));
		}

		PacketContainer entityMeta = manager.createPacket(PacketType.Play.Server.ENTITY_METADATA);

		entityMeta.getIntegers().write(0, player.getEntityId());

		WrappedDataWatcher dataWatcher = new WrappedDataWatcher(entityMeta.getWatchableCollectionModifier().read(0));

		WrappedDataWatcher.WrappedDataWatcherObject airIndex = new WrappedDataWatcher.WrappedDataWatcherObject(1,
				WrappedDataWatcher.Registry.get(Integer.class));

		dataWatcher.setObject(airIndex, air);

		entityMeta.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());

		try {
			manager.sendServerPacket(player, entityMeta);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onEnable(ChallengePlayer challengePlayer, Player player) {
		if (player.getRemainingAir() >= player.getMaximumAir()) {
			player.setRemainingAir(player.getMaximumAir() - 1);
		}
	}

	@Override
	public void onDisable(ChallengePlayer challengePlayer, Player player) {
	}

}
