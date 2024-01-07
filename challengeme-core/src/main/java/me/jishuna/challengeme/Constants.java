package me.jishuna.challengeme;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.NamespacedKey;
import me.jishuna.challengeme.challenge.Challenge;
import me.jishuna.jishlib.pdc.CollectionType;
import me.jishuna.jishlib.pdc.PDCTypes;
import me.jishuna.jishlib.pdc.RegistryType;

public final class Constants {
    public static final NamespacedKey CHALLENGES_KEY = NamespacedKey.fromString("challengeme:challenges");

    public static final RegistryType<String, Challenge, NamespacedKey> CHALLENGE_TYPE = new RegistryType<>(
            Challenge.class, PDCTypes.NAMESPACE, Challenge::getKey, Registries.CHALLENGE);

    public static final CollectionType<Set<Challenge>, Challenge> CHALLENGES_TYPE = new CollectionType<>(HashSet::new, CHALLENGE_TYPE);

    private Constants() {
    }
}
