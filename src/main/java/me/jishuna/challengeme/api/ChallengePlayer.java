package me.jishuna.challengeme.api;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;

public class ChallengePlayer {

	private final UUID id;
	private final Set<Challenge> activeChallenges = new HashSet<>();

	public ChallengePlayer(Player player) {
		this(player.getUniqueId());
	}

	public ChallengePlayer(UUID uuid) {
		this.id = uuid;
	}

	public UUID getId() {
		return id;
	}

	public Set<Challenge> getActiveChallenges() {
		return activeChallenges;
	}

	public void addChallenge(Challenge challenge) {
		this.activeChallenges.add(challenge);
	}

	public boolean removeChallenge(Challenge challenge) {
		return this.activeChallenges.remove(challenge);
	}

	public boolean hasChallenge(Challenge challenge) {
		return this.activeChallenges.contains(challenge);
	}

	public void savePlayer(File file) {

		Gson gson = new Gson();
		try (FileWriter writer = new FileWriter(file)) {
			List<String> challenge_keys = this.activeChallenges.stream().map(Challenge::getKey)
					.collect(Collectors.toList());
			gson.toJson(challenge_keys, writer);
		} catch (JsonIOException | IOException e) {
			// this.plugin.getLogger().severe("Encountered " + e.getClass().getSimpleName()
			// + " while saving player data for UUID " + this.id);
		}
	}
}
