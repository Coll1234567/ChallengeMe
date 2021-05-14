package me.jishuna.challengeme.challenges;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
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
	private final boolean showMessage;

	public NoDarknessChallenge(Plugin owner, YamlConfiguration challengeConfig) {
		this(owner, challengeConfig.getConfigurationSection("no-darkness"));
	}

	private NoDarknessChallenge(Plugin owner, ConfigurationSection challengeSection) {
		super(owner, "no-darkness", challengeSection);

		this.showMessage = challengeSection.getBoolean("show-message");
	}

	@Override
	public void onTick(ChallengePlayer challengePlayer, Player player) {
		byte light = player.getLocation().getBlock().getLightLevel();
		if (this.showMessage) {
			String lightLevel = light < 3 ? ChatColor.RED.toString() + light : ChatColor.GREEN.toString() + light;
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
