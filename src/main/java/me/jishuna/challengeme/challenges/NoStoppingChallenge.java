package me.jishuna.challengeme.challenges;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.challenge.TickingChallenge;
import me.jishuna.challengeme.api.player.ChallengePlayer;

public class NoStoppingChallenge extends Challenge implements TickingChallenge {

	private final Map<UUID, NoStoppingChallengeData> challengeData = new HashMap<>();
	private int msNeeded;
	private static final String KEY = "no_stopping";

	public NoStoppingChallenge(Plugin owner) {
		super(owner, KEY, loadConfig(owner, KEY));
	}

	@Override
	protected void loadData(YamlConfiguration upgradeConfig) {
		super.loadData(upgradeConfig);

		this.msNeeded = upgradeConfig.getInt("seconds", 3) * 1000;
	}

	@Override
	public void onTick(ChallengePlayer challengePlayer, Player player) {
		UUID id = player.getUniqueId();
		NoStoppingChallengeData challengeData = this.challengeData.get(id);
		Location location = player.getLocation();

		if (challengeData == null) {
			challengeData = new NoStoppingChallengeData(player.getLocation(), System.currentTimeMillis() + msNeeded);
			this.challengeData.put(id, challengeData);
		}

		if (!challengeData.compareLocations(location)) {
			challengeData.setLastLocation(location);
			challengeData.setTimestamp(System.currentTimeMillis() + msNeeded);
		}

		if (challengeData.getTimestamp() <= System.currentTimeMillis())
			player.setFireTicks(20);
	}

	public static class NoStoppingChallengeData {
		private long timestamp;
		private Vector lastLocation;

		public NoStoppingChallengeData(Location location, long timestamp) {
			this.lastLocation = location.toVector();
			this.timestamp = timestamp;
		}

		public long getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}

		public void setLastLocation(Location lastLocation) {
			this.lastLocation = lastLocation.toVector();
		}

		public boolean compareLocations(Location location) {
			if (Double.doubleToLongBits(this.lastLocation.getX()) != Double.doubleToLongBits(location.getX())) {
				return false;
			}
			if (Double.doubleToLongBits(this.lastLocation.getY()) != Double.doubleToLongBits(location.getY())) {
				return false;
			}
			if (Double.doubleToLongBits(this.lastLocation.getZ()) != Double.doubleToLongBits(location.getZ())) {
				return false;
			}

			return true;
		}

	}
}
