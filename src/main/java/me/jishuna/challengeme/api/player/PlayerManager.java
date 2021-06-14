package me.jishuna.challengeme.api.player;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import me.jishuna.challengeme.ChallengeMe;
import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.challenge.ToggleChallenge;
import me.jishuna.challengeme.gson.PlayerDataDeserializer;
import me.jishuna.commonlib.events.EventConsumer;
import net.md_5.bungee.api.ChatColor;

public class PlayerManager {

	private final ChallengeMe plugin;
	private final Map<UUID, ChallengePlayer> players = new HashMap<>();

	private final PlayerDataDeserializer playerDataDeserializer;

	public PlayerManager(ChallengeMe plugin) {
		this.plugin = plugin;

		File playerDataDirectory = new File(this.plugin.getDataFolder() + File.separator + "playerdata");

		if (!playerDataDirectory.exists()) {
			playerDataDirectory.mkdirs();
		}

		this.playerDataDeserializer = new PlayerDataDeserializer(plugin);
	}

	public void registerListeners() {
		EventConsumer<PlayerJoinEvent> loginWrapper = new EventConsumer<>(PlayerJoinEvent.class,
				event -> loadPlayerData(event.getPlayer()));
		loginWrapper.register(plugin);

		EventConsumer<PlayerQuitEvent> loggoutWrapper = new EventConsumer<>(PlayerQuitEvent.class,
				event -> savePlayerData(event.getPlayer().getUniqueId()));
		loggoutWrapper.register(plugin);
	}

	public Optional<ChallengePlayer> getPlayer(Player player) {
		return getPlayer(player.getUniqueId());
	}

	public Optional<ChallengePlayer> getPlayer(UUID id) {
		return Optional.ofNullable(this.players.get(id));
	}

	public void savePlayer(UUID id, ChallengePlayer player, boolean disable) {
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

		if (disable) {
			player.disableActiveChallenges();
		}
	}

	public void saveAllPlayers(boolean disable) {
		this.players.entrySet().forEach(entry -> savePlayer(entry.getKey(), entry.getValue(), disable));

		if (disable) {
			this.players.clear();
		}
	}

	private void showLoginMessage(Player player) {
		ChallengePlayer challengePlayer = this.players.get(player.getUniqueId());

		if (challengePlayer != null) {
			StringBuilder challenges = new StringBuilder();

			for (Challenge challenge : challengePlayer.getActiveChallenges()) {
				challenges.append(challenge.getName() + ChatColor.GRAY + ", ");
			}

			if (challenges.length() == 0)
				return;

			String message = ChatColor.translateAlternateColorCodes('&', this.plugin.getMessage("active-challenges"));
			String color = org.bukkit.ChatColor.getLastColors(message);
			player.sendMessage(
					message.replace("%challenges%", challenges.substring(0, challenges.length() - 2) + color + "."));

		}
	}

	public void loadPlayerData(Player player) {
		UUID uuid = player.getUniqueId();

		Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
			ChallengePlayer challengePlayer = new ChallengePlayer(player, this.plugin);

			File jsonFile = new File(this.plugin.getDataFolder() + File.separator + "playerdata", uuid + ".json");

			if (jsonFile.exists()) {
				Gson gson = new GsonBuilder().registerTypeAdapter(PersistantPlayerData.class, playerDataDeserializer)
						.create();
				try (FileReader reader = new FileReader(jsonFile)) {
					PersistantPlayerData playerData = gson.fromJson(reader, PersistantPlayerData.class);

					playerData.getActiveChallenges().forEach(challenge -> {
						Bukkit.getScheduler().runTask(this.plugin, () -> {
							if (challenge instanceof ToggleChallenge) {
								((ToggleChallenge) challenge).onEnable(challengePlayer, player);
							}
						});
					});

					challengePlayer.setPlayerData(playerData);

				} catch (JsonSyntaxException | JsonIOException | IOException e) {
					this.plugin.getLogger()
							.severe("Encountered " + e.getClass().getSimpleName() + " while loading player data.");
					e.printStackTrace();
				}
			} else {
				challengePlayer.setPlayerData(new PersistantPlayerData());
			}

			this.players.put(uuid, challengePlayer);
			challengePlayer.setLoaded(true);

			challengePlayer.updateEnabledChallenges();
			showLoginMessage(player);
		});

	}

	private void savePlayerData(UUID uuid) {
		ChallengePlayer challengePlayer = this.players.get(uuid);

		if (challengePlayer != null) {
			challengePlayer.disableActiveChallenges();

			Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> savePlayer(uuid, challengePlayer, true));
		}
	}

}
