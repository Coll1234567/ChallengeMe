package me.jishuna.challengeme.api;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import me.jishuna.challengeme.ChallengeMe;

public class PlayerManager {

	private final ChallengeManager challengeManager;
	private final ChallengeMe plugin;
	private final Map<UUID, ChallengePlayer> players = new HashMap<>();

	private final Type listType = new TypeToken<List<String>>() {
	}.getType();

	public PlayerManager(ChallengeMe plugin, ChallengeManager challengeManager) {
		this.plugin = plugin;
		this.challengeManager = challengeManager;

		File playerDataDirectory = new File(this.plugin.getDataFolder() + File.separator + "playerdata");
		
		if (!playerDataDirectory.exists()) {
			playerDataDirectory.mkdirs();
		}
	}

	public void registerListeners() {
		EventConsumer<PlayerJoinEvent> loginWrapper = new EventConsumer<>(PlayerJoinEvent.class,
				event -> loadPlayerData(event.getPlayer()));

		loginWrapper.register(plugin);

		EventConsumer<PlayerQuitEvent> loggoutWrapper = new EventConsumer<>(PlayerQuitEvent.class,
				event -> savePlayerData(event.getPlayer()));

		loggoutWrapper.register(plugin);
	}

	public Optional<ChallengePlayer> getPlayer(Player player) {
		return getPlayer(player.getUniqueId());
	}

	public Optional<ChallengePlayer> getPlayer(UUID id) {
		return Optional.ofNullable(this.players.get(id));
	}

	private void loadPlayerData(Player player) {
		loadPlayerData(player.getUniqueId());
	}

	private void loadPlayerData(UUID uuid) {
		Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
			ChallengePlayer challengePlayer = new ChallengePlayer(uuid);

			File jsonFile = new File(this.plugin.getDataFolder() + File.separator + "playerdata", uuid + ".json");

			if (jsonFile.exists()) {
				Gson gson = new Gson();
				try (FileReader reader = new FileReader(jsonFile)) {
					List<String> challenge_keys = gson.fromJson(reader, this.listType);

					for (String key : challenge_keys) {
						this.challengeManager.getChallenge(key)
								.ifPresent(challenge -> challengePlayer.addChallenge(challenge));
					}

				} catch (JsonSyntaxException | JsonIOException | IOException e) {
					this.plugin.getLogger()
							.severe("Encountered " + e.getClass().getSimpleName() + " while loading player data.");
					e.printStackTrace();
				}
			}

			this.players.put(uuid, challengePlayer);
		});
	}

	private void savePlayerData(Player player) {
		savePlayerData(player.getUniqueId());
	}

	private void savePlayerData(UUID uuid) {
		ChallengePlayer challengePlayer = this.players.remove(uuid);

		if (challengePlayer != null) {
			Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
				savePlayer(uuid, challengePlayer);
			});
		}
	}

	public void savePlayer(UUID id, ChallengePlayer player) {
		File jsonFile = new File(this.plugin.getDataFolder() + File.separator + "playerdata", id + ".json");
		if (!jsonFile.exists()) {
			try {
				jsonFile.createNewFile();
			} catch (IOException e) {
				this.plugin.getLogger()
						.severe("Encountered " + e.getClass().getSimpleName() + " while creating player data file.");
				e.printStackTrace();
			}
		}

		if (jsonFile.exists()) {
			player.savePlayer(jsonFile);
		}
	}

	public void saveAllPlayers() {
		this.players.entrySet().forEach(entry -> savePlayer(entry.getKey(), entry.getValue()));
	}
}
