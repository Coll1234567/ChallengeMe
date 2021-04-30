package me.jishuna.challengeme.api.event;

import java.util.function.BiConsumer;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class EventWrapper<T extends Event> {

	private BiConsumer<T, Player> handler;
	private Class<T> eventClass;

	public EventWrapper(Class<T> eventClass, BiConsumer<T, Player> handler) {
		this.handler = handler;
		this.eventClass = eventClass;
	}

	public void consume(Event event, Player player) {
		if (this.eventClass.isAssignableFrom(event.getClass())) {
			handler.accept(this.eventClass.cast(event), player);
		}
	}
}
