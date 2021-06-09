package me.jishuna.challengeme.nms;

import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class NMSAdapter_v1_16_R3 implements NMSAdapter {

	@Override
	public void damageEntity(LivingEntity entity, DamageCause type, float amount) {
		((CraftLivingEntity) entity).getHandle().damageEntity(NMSUtils.getFromCause(type), amount);
	}
}