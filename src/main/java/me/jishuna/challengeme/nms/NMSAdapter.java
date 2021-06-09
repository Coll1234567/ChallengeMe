package me.jishuna.challengeme.nms;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public interface NMSAdapter {
	public void damageEntity(LivingEntity entity, DamageCause type, float amount);
}
