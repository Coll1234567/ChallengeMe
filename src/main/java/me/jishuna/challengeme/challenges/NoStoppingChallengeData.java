package me.jishuna.challengeme.challenges;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class NoStoppingChallengeData {
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
