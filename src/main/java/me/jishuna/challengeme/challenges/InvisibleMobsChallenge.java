package me.jishuna.challengeme.challenges;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.challenge.ToggleChallenge;
import me.jishuna.challengeme.api.packets.PacketWrapper;
import me.jishuna.challengeme.api.player.ChallengePlayer;

public class InvisibleMobsChallenge extends Challenge implements ToggleChallenge {
	private final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
	private final int EFFECT_COLOR = 8356754;
	private static final String KEY = "invisible_mobs";

	public InvisibleMobsChallenge(Plugin owner) {
		super(owner, KEY, loadConfig(owner, KEY));

		PacketWrapper wrapper = new PacketWrapper(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
		wrapper.setSendHandler(this::onEntitySpawn);

		addPacketHandler(wrapper);
	}

	private void onEntitySpawn(PacketEvent event) {
		int id = event.getPacket().getIntegers().read(0);

		if (manager.getEntityFromID(event.getPlayer().getWorld(), id).getType() == EntityType.PLAYER)
			return;

		PacketContainer entityMeta = constructPacket(id, false);

		Bukkit.getScheduler().runTaskLaterAsynchronously(getOwningPlugin(), () -> {
			try {
				manager.sendServerPacket(event.getPlayer(), entityMeta);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}, 1);
	}

	@Override
	public void onEnable(ChallengePlayer challengePlayer, Player player) {
		player.getWorld().getNearbyEntities(player.getLocation(), 100, 100, 100).forEach(entity -> {
			if (entity instanceof LivingEntity && entity.getType() != EntityType.PLAYER) {
				PacketContainer entityMeta = constructPacket(entity.getEntityId(), false);

				try {
					manager.sendServerPacket(player, entityMeta);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}

			}
		});
	}

	@Override
	public void onDisable(ChallengePlayer challengePlayer, Player player) {
		player.getWorld().getNearbyEntities(player.getLocation(), 100, 100, 100).forEach(entity -> {
			if (entity instanceof LivingEntity && entity.getType() != EntityType.PLAYER) {
				PacketContainer entityMeta = constructPacket(entity.getEntityId(), true);

				try {
					manager.sendServerPacket(player, entityMeta);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private PacketContainer constructPacket(int id, boolean disable) {
		PacketContainer entityMeta = manager.createPacket(PacketType.Play.Server.ENTITY_METADATA);

		entityMeta.getIntegers().write(0, id);

		WrappedDataWatcher dataWatcher = new WrappedDataWatcher(entityMeta.getWatchableCollectionModifier().read(0));

		WrappedDataWatcher.WrappedDataWatcherObject isInvisibleIndex = new WrappedDataWatcher.WrappedDataWatcherObject(
				0, WrappedDataWatcher.Registry.get(Byte.class));
		WrappedDataWatcher.WrappedDataWatcherObject effectIdIndex = new WrappedDataWatcher.WrappedDataWatcherObject(9,
				WrappedDataWatcher.Registry.get(Integer.class));

		dataWatcher.setObject(isInvisibleIndex, disable ? (byte) 0 : (byte) 0x20);
		dataWatcher.setObject(effectIdIndex, disable ? 0 : EFFECT_COLOR);

		entityMeta.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());
		return entityMeta;
	}
}
