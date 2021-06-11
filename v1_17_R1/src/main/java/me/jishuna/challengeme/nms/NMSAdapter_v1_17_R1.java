package me.jishuna.challengeme.nms;

import org.bukkit.craftbukkit.v1_17_R1.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class NMSAdapter_v1_17_R1 implements NMSAdapter {

	@Override
	public void damageEntity(LivingEntity entity, DamageCause type, float amount) {
		((CraftLivingEntity) entity).getHandle().damageEntity(NMSUtils_v1_17_R1.getFromCause(type), amount);
	}
}