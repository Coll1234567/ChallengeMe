package me.jishuna.challengeme.api.event;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.jishuna.challengeme.api.challenge.Challenge;

public class ChallengeSetupEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();

	private final List<Challenge> challengesToAdd = new ArrayList<>();

	public List<Challenge> getChallengesToAdd() {
		return challengesToAdd;
	}

	@Override
	public HandlerList getHandlers() {
		return getHandlerList();
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
