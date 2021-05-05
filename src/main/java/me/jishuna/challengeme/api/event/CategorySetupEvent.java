package me.jishuna.challengeme.api.event;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.jishuna.challengeme.api.challenge.Category;

public class CategorySetupEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();

	private final List<Category> categoriesToAdd = new ArrayList<>();

	public List<Category> getCategoriesToAdd() {
		return categoriesToAdd;
	}

	@Override
	public HandlerList getHandlers() {
		return getHandlerList();
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
