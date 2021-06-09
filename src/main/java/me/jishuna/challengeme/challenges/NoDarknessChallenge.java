package me.jishuna.challengeme.challenges;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.challenge.TickingChallenge;
import me.jishuna.challengeme.api.player.ChallengePlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class NoDarknessChallenge extends Challenge implements TickingChallenge {

	private final Map<UUID, Integer> challengeData = new HashMap<>();
	private boolean showMessage;
	private static final String KEY = "no_darkness";

	public NoDarknessChallenge(Plugin owner) {
		super(owner, KEY, loadConfig(owner, KEY));
	}

	@Override
	protected void loadData(YamlConfiguration upgradeConfig) {
		super.loadData(upgradeConfig);

		this.showMessage = upgradeConfig.getBoolean("show-actionbar");
	}

	@Override
	public void onTick(ChallengePlayer challengePlayer, Player player) {
		byte light = player.getLocation().getBlock().getLightLevel();
		if (this.showMessage) {
			ChatColor color = ChatColor.GREEN;
			if (light <= 0) {
				color = ChatColor.DARK_RED;
			} else if (light <= 3) {
				color = ChatColor.RED;
			}

			String lightLevel = color.toString() + light;
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
					TextComponent.fromLegacyText(this.getMessage().replace("%light%", lightLevel)));
		}

		if (light == 0) {
			int checks = this.challengeData.getOrDefault(player.getUniqueId(), 1);
			checks = (checks + 1) % 4;

			if (checks == 0) {
				player.damage(2.0);
			}
			this.challengeData.put(player.getUniqueId(), checks);
		}
	}
}
