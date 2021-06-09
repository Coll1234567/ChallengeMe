package me.jishuna.challengeme.nms;

import me.jishuna.commonlib.utils.VersionUtils;

//TODO static
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
		String version = VersionUtils.getServerVersion();

		try {
			NMSAdapter versionAdapter = (NMSAdapter) Class.forName("me.jishuna.challengeme.nms.NMSAdapter_" + version)
					.newInstance();
			instance = new NMSManager(versionAdapter);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			// TODO log error
		}
		return instance;
	}

}
