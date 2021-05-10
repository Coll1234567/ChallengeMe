package me.jishuna.challengeme.gson;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.player.PersistantPlayerData;

public class PlayerDataSeralizer implements JsonSerializer<PersistantPlayerData> {

	@Override
	public JsonElement serialize(PersistantPlayerData data, Type typeOfT, JsonSerializationContext context) {

		JsonObject json = new JsonObject();

		Map<String, Long> cooldowns = data.getCooldowns();
		cooldowns.entrySet().removeIf((Entry<String, Long> entry) -> entry.getValue() == null
				|| entry.getValue() <= System.currentTimeMillis());

		json.add("challenges", context
				.serialize(data.getActiveChallenges().stream().map(Challenge::getKey).collect(Collectors.toList())));

		json.add("data", context.serialize(data.getChallengeData()));
		json.add("cooldowns", context.serialize(cooldowns));

		return json;
	}
}