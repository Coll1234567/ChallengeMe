package me.jishuna.challengeme.api.player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.jishuna.challengeme.api.challenge.Challenge;

public class PersistantPlayerData {

	private Set<Challenge> activeChallenges;
	private Map<String, PersistantChallengeData> challengeData;

	public PersistantPlayerData() {
		this.activeChallenges = new HashSet<>();
		this.challengeData = new HashMap<>();
	}

	public PersistantPlayerData(Set<Challenge> activeChallenges, Map<String, PersistantChallengeData> challengeData) {
		this.activeChallenges = activeChallenges;
		this.challengeData = challengeData;
	}

	public long getCooldown(Challenge challenge) {
		PersistantChallengeData persistantData = challengeData.computeIfAbsent(challenge.getKey(),
				key -> new PersistantChallengeData());
		Long time = persistantData.getCooldown();
		return time == null ? 0 : time - System.currentTimeMillis();
	}

	public void setCooldown(Challenge challenge, int time) {
		PersistantChallengeData persistantData = challengeData.computeIfAbsent(challenge.getKey(),
				key -> new PersistantChallengeData());
		persistantData.setCooldown(System.currentTimeMillis() + time * 1000);
	}

	public <T> T getChallengeData(Challenge challenge, Class<T> type) {
		PersistantChallengeData persistantData = challengeData.computeIfAbsent(challenge.getKey(),
				key -> new PersistantChallengeData());
		Object object = persistantData.getData();

		if (object == null)
			return null;

		return type.isInstance(object) ? type.cast(object) : null;
	}

	public void setChallengeData(Challenge challenge, Object data) {
		PersistantChallengeData persistantData = challengeData.computeIfAbsent(challenge.getKey(),
				key -> new PersistantChallengeData());
		persistantData.setData(data);
	}

	public Set<Challenge> getActiveChallenges() {
		return activeChallenges;
	}

	public Map<String, PersistantChallengeData> getChallengeData() {
		return challengeData;
	}

	public static class PersistantChallengeData {
		private Long cooldown;
		private Object data;

		public Long getCooldown() {
			return cooldown == null ? 0 : cooldown;
		}

		public void removeCooldown() {
			this.cooldown = null;
		}

		public void setCooldown(long cooldown) {
			this.cooldown = cooldown;
		}

		public Object getData() {
			return data;
		}

		public void setData(Object data) {
			this.data = data;
		}
	}

}
