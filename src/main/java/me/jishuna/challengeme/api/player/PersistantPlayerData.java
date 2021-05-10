package me.jishuna.challengeme.api.player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.jishuna.challengeme.api.challenge.Challenge;

public class PersistantPlayerData {

	private Set<Challenge> activeChallenges;
	private Map<String, Object> challengeData;
	private Map<String, Long> cooldowns;

	public PersistantPlayerData() {
		this.activeChallenges = new HashSet<>();
		this.challengeData = new HashMap<>();
		this.cooldowns = new HashMap<>();
	}

	public PersistantPlayerData(Set<Challenge> activeChallenges, Map<String, Object> challengeData,
			Map<String, Long> cooldowns) {
		this.activeChallenges = activeChallenges;
		this.challengeData = challengeData;
		this.cooldowns = cooldowns;
	}

	public long getCooldown(Challenge challenge) {
		Long time = cooldowns.get(challenge.getKey());
		return time == null ? 0 : time - System.currentTimeMillis();
	}

	public void setCooldown(Challenge challenge, int time) {
		cooldowns.put(challenge.getKey(), System.currentTimeMillis() + time * 1000);
	}

	public <T> T getChallengeData(Challenge challenge, Class<T> type) {
		Object object = this.challengeData.get(challenge.getKey());

		if (object == null)
			return null;

		return type.isInstance(object) ? type.cast(object) : null;
	}

	public void setChallengeData(Challenge challenge, Object data) {
		this.challengeData.put(challenge.getKey(), data);
	}

	public Set<Challenge> getActiveChallenges() {
		return activeChallenges;
	}

	public Map<String, Object> getChallengeData() {
		return challengeData;
	}

	public Map<String, Long> getCooldowns() {
		return cooldowns;
	}

}
