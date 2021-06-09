package me.jishuna.challengeme.nms;

import org.bukkit.Bukkit;

public class NMSManager {
	private static NMSManager instance;

	private final NMSAdapter adapter;

	public NMSManager(NMSAdapter adapter) {
		this.adapter = adapter;
	}

	public NMSAdapter getAdapter() {
		return this.adapter;
	}

	public static NMSManager getInstance() {
		if (instance == null) {
			Bukkit.broadcastMessage(Bukkit.getServer().getClass().getPackage().getName());
		}
		return instance;
	}

}
