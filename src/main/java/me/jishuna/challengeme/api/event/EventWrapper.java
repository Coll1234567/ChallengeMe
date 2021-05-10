package me.jishuna.challengeme.api.event;

import java.util.function.BiConsumer;

import org.bukkit.event.Event;

import me.jishuna.challengeme.api.player.ChallengePlayer;

public class EventWrapper<T extends Event> {

	private BiConsumer<T, ChallengePlayer> handler;
	private Class<T> eventClass;

	public EventWrapper(Class<T> eventClass, BiConsumer<T, ChallengePlayer> handler) {
		this.handler = handler;
		this.eventClass = eventClass;
	}

	public void consume(Event event, ChallengePlayer player) {
		if (this.eventClass.isAssignableFrom(event.getClass())) {
			handler.accept(this.eventClass.cast(event), player);
		}
	}
}
