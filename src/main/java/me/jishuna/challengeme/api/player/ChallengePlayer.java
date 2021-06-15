package me.jishuna.challengeme.api.player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;

import me.jishuna.challengeme.ChallengeMe;
import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.challenge.ToggleChallenge;
import me.jishuna.challengeme.gson.PlayerDataSerializer;

public class ChallengePlayer {

	private final UUID id;
	private PersistantPlayerData playerData;
	private final ChallengeMe plugin;
	private boolean isLoaded = false;

	private final PlayerDataSerializer playerDataSeralizer = new PlayerDataSerializer();

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

	public boolean isLoaded() {
		return isLoaded;
	}

	public void setLoaded(boolean isLoaded) {
		this.isLoaded = isLoaded;
	}

	public Set<Challenge> getActiveChallenges() {
		return this.playerData.getActiveChallenges();
	}

	public long getCooldown(Challenge challenge) {
		return this.playerData.getCooldown(challenge);
	}

	public void setCooldown(Challenge challenge, int time) {
		this.playerData.setCooldown(challenge, time);
	}

	public <T> T getChallengeData(Challenge challenge, Class<T> type) {
		return this.playerData.getChallengeData(challenge, type);
	}

	public void setChallengeData(Challenge challenge, Object data) {
		this.playerData.setChallengeData(challenge, data);
	}

	public void setPlayerData(PersistantPlayerData data) {
		this.playerData = data;
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(this.id);
	}

	public void addChallenge(Challenge challenge) {
		boolean add = this.playerData.getActiveChallenges().add(challenge);

		if (add && challenge instanceof ToggleChallenge) {
			Bukkit.getScheduler().runTask(this.plugin, () -> {
				Player player = Bukkit.getPlayer(this.id);
				if (player != null)
					((ToggleChallenge) challenge).onEnable(this, player);
			});

		}
	}

	public boolean removeChallenge(Challenge challenge) {
		boolean remove = this.playerData.getActiveChallenges().remove(challenge);

		if (remove && challenge instanceof ToggleChallenge) {
			Bukkit.getScheduler().runTask(this.plugin, () -> {
				Player player = Bukkit.getPlayer(this.id);
				if (player != null)
					((ToggleChallenge) challenge).onDisable(this, player);
			});
		}
		return remove;
	}

	public boolean hasChallenge(Challenge challenge) {
		return this.playerData.getActiveChallenges().contains(challenge);
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
			} else if (!challenge.isEnabled() && hasChallenge(challenge)) {
				removeChallenge(challenge);
			}

		}
	}

	public void disableActiveChallenges() {
		Player player = Bukkit.getPlayer(this.id);

		if (player != null) {
			this.playerData.getActiveChallenges().forEach(challenge -> {
				if (challenge instanceof ToggleChallenge) {
					((ToggleChallenge) challenge).onDisable(this, player);
				}
			});
		}
	}

	public void savePlayer(File file) {
		Gson gson = new GsonBuilder().registerTypeAdapter(PersistantPlayerData.class, playerDataSeralizer).create();
		try (FileWriter writer = new FileWriter(file)) {
			gson.toJson(this.playerData, writer);
		} catch (JsonIOException | IOException e) {
			// this.plugin.getLogger().severe("Encountered " + e.getClass().getSimpleName()
			// + " while saving player data for UUID " + this.id);
		}
	}
}
