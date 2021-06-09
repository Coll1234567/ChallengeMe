package me.jishuna.challengeme.gson;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.player.PersistantPlayerData;
import me.jishuna.challengeme.api.player.PersistantPlayerData.PersistantChallengeData;

public class PlayerDataSeralizer implements JsonSerializer<PersistantPlayerData> {

	@Override
	public JsonElement serialize(PersistantPlayerData data, Type typeOfT, JsonSerializationContext context) {

		JsonObject json = new JsonObject();

		Map<String, PersistantChallengeData> challengeData = data.getChallengeData();
		challengeData.values().forEach(persistantData -> {
			if (persistantData.getCooldown() <= System.currentTimeMillis()) {
				persistantData.removeCooldown();
			}
		});

		json.add("challenges", context
				.serialize(data.getActiveChallenges().stream().map(Challenge::getKey).collect(Collectors.toList())));

		json.add("data", context.serialize(challengeData));

		return json;
	}
}