package me.jishuna.challengeme.api.player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;

import me.jishuna.challengeme.ChallengeMe;
import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.challenge.ToggleChallenge;

public class ChallengePlayer {

	private final UUID id;
	private final Set<Challenge> activeChallenges = new HashSet<>();
	private final ChallengeMe plugin;

	public ChallengePlayer(Player player, ChallengeMe plugin) {
		this(player.getUniqueId(), plugin);
	}

	public ChallengePlayer(UUID uuid, ChallengeMe plugin) {
		this.id = uuid;
		this.plugin = plugin;
	}

	public UUID getId() {
		return id;
	}

	public Set<Challenge> getActiveChallenges() {
		return activeChallenges;
	}

	public void addChallenge(Challenge challenge) {
		boolean add = this.activeChallenges.add(challenge);

		if (add && challenge instanceof ToggleChallenge) {
			Bukkit.getScheduler().runTask(this.plugin, () -> {
				Player player = Bukkit.getPlayer(this.id);
				if (player != null)
					((ToggleChallenge) challenge).onEnable(player);
			});

		}
	}

	public boolean removeChallenge(Challenge challenge) {
		boolean remove = this.activeChallenges.remove(challenge);

		if (remove && challenge instanceof ToggleChallenge) {
			Bukkit.getScheduler().runTask(this.plugin, () -> {
				Player player = Bukkit.getPlayer(this.id);
				if (player != null)
					((ToggleChallenge) challenge).onDisable(player);
			});
		}
		return remove;
	}

	public boolean hasChallenge(Challenge challenge) {
		return this.activeChallenges.contains(challenge);
	}

	public void updateEnabledChallenges() {
		Player player = Bukkit.getPlayer(this.id);

		for (Challenge challenge : this.plugin.getChallengeManager().getAllChallenges()) {

			if (challenge.isForced() && !hasChallenge(challenge)) {
				addChallenge(challenge);

				if (player != null) {
					player.sendMessage(this.plugin.getMessage("challenge-force-enabled").replace("%challenge%",
							challenge.getName()));
				}
			}

		}
	}

	public void disableActiveChallenges() {
		Player player = Bukkit.getPlayer(this.id);

		if (player != null) {
			this.activeChallenges.forEach(challenge -> {
				if (challenge instanceof ToggleChallenge) {
					((ToggleChallenge) challenge).onDisable(player);
				}
			});
		}
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
