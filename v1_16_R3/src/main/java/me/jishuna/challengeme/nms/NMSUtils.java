package me.jishuna.challengeme.nms;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import net.minecraft.server.v1_16_R3.DamageSource;

public class NMSUtils {

	public static DamageSource getFromCause(DamageCause cause) {
		switch (cause) {

		case BLOCK_EXPLOSION:
		case CUSTOM:
		case ENTITY_ATTACK:
		case ENTITY_EXPLOSION:
		case ENTITY_SWEEP_ATTACK:
		case PROJECTILE:
		case SUICIDE:
		case THORNS:
		default:
			return DamageSource.GENERIC;
		case CONTACT:
			return DamageSource.CACTUS;
		case CRAMMING:
			return DamageSource.CRAMMING;
		case DRAGON_BREATH:
			return DamageSource.DRAGON_BREATH;
		case DROWNING:
			return DamageSource.DROWN;
		case DRYOUT:
			return DamageSource.DRYOUT;
		case FALL:
			return DamageSource.FALL;
		case FALLING_BLOCK:
			return DamageSource.FALLING_BLOCK;
		case FIRE:
			return DamageSource.FIRE;
		case FIRE_TICK:
		case MELTING:
			return DamageSource.BURN;
		case FLY_INTO_WALL:
			return DamageSource.FLY_INTO_WALL;
		case HOT_FLOOR:
			return DamageSource.HOT_FLOOR;
		case LAVA:
			return DamageSource.LAVA;
		case LIGHTNING:
			return DamageSource.LIGHTNING;
		case MAGIC:
		case POISON:
			return DamageSource.MAGIC;
		case STARVATION:
			return DamageSource.STARVE;
		case SUFFOCATION:
			return DamageSource.STUCK;
		case VOID:
			return DamageSource.OUT_OF_WORLD;
		case WITHER:
			return DamageSource.WITHER;
		}
	}
}
