package me.jishuna.challengeme.gson;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import me.jishuna.challengeme.ChallengeMe;
import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.player.PersistantPlayerData;
import me.jishuna.challengeme.api.player.PersistantPlayerData.PersistantChallengeData;

public class PlayerDataDeserializer implements JsonDeserializer<PersistantPlayerData> {
	private final Type listType = new TypeToken<List<String>>() {
	}.getType();
	private final Type dataType = new TypeToken<Map<String, PersistantChallengeData>>() {
	}.getType();

	private final ChallengeMe plugin;

	public PlayerDataDeserializer(ChallengeMe plugin) {
		this.plugin = plugin;
	}

	public PersistantPlayerData deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {

		JsonObject json = jsonElement.getAsJsonObject();

		List<String> challenges = context.deserialize(json.get("challenges"), listType);
		Set<Challenge> activeChallenges = new HashSet<>();

		for (String key : challenges) {
			this.plugin.getChallengeManager().getChallenge(key).ifPresent(challenge -> {
				if (challenge.isEnabled())
					activeChallenges.add(challenge);
			});
		}
		
		Map<String, PersistantChallengeData> challengeData = context.deserialize(json.get("data"), dataType);

		return new PersistantPlayerData(activeChallenges, challengeData);
	}
}