package me.jishuna.challengeme.nms;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import net.minecraft.world.damagesource.DamageSource;

public class NMSUtils_v1_17_R1 {

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
			return DamageSource.n;
		case CONTACT:
			return DamageSource.j;
		case CRAMMING:
			return DamageSource.g;
		case DRAGON_BREATH:
			return DamageSource.s;
		case DROWNING:
			return DamageSource.h;
		case DRYOUT:
			return DamageSource.t;
		case FALL:
			return DamageSource.k;
		case FALLING_BLOCK:
			return DamageSource.r;
		case FIRE:
			return DamageSource.a;
		case FIRE_TICK:
		case MELTING:
			return DamageSource.c;
		case FLY_INTO_WALL:
			return DamageSource.l;
		case HOT_FLOOR:
			return DamageSource.e;
		case LAVA:
			return DamageSource.d;
		case LIGHTNING:
			return DamageSource.b;
		case MAGIC:
		case POISON:
			return DamageSource.o;
		case STARVATION:
			return DamageSource.i;
		case SUFFOCATION:
			return DamageSource.f;
		case VOID:
			return DamageSource.m;
		case WITHER:
			return DamageSource.p;
		case FREEZE:
			return DamageSource.v;
		}
	}
}
