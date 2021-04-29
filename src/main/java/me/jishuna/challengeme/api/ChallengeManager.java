package me.jishuna.challengeme.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import me.jishuna.challengeme.ChallengeMe;
import me.jishuna.challengeme.PluginKeys;
import me.jishuna.commonlib.CustomInventory;
import me.jishuna.commonlib.ItemBuilder;

public class ChallengeManager {

	private final Map<String, Challenge> challenges = new LinkedHashMap<>();
	private List<Challenge> challengeCache;
	private final ChallengeMe plugin;

	public ChallengeManager(ChallengeMe plugin) {
		this.plugin = plugin;
	}

	public void registerChallenge(Challenge challenge, YamlConfiguration challengeConfig) {
		if (challengeConfig.getBoolean("challenges." + challenge.getKey() + ".enabled", true)) {
			this.challenges.putIfAbsent(challenge.getKey(), challenge);
			this.challengeCache = new ArrayList<Challenge>(this.challenges.values());
		}
	}

	public Optional<Challenge> getChallenge(String key) {
		return Optional.ofNullable(this.challenges.get(key));
	}

	public Collection<Challenge> getAllChallenges() {
		return this.challenges.values();
	}

	public CustomInventory getChallengeGUI(ChallengePlayer player, int start) {
		CustomInventory inventory = new CustomInventory(null, 54, "Challenges");
		inventory.addClickConsumer(this::handleClick);

		for (int i = 0; i < Math.min(45, this.challengeCache.size() - start); i++) {
			Challenge challenge = this.challengeCache.get(i);

			ItemBuilder itemBuilder = new ItemBuilder(challenge.getIcon()).withName(challenge.getName())
					.withLore(challenge.getDescription()).withFlags(ItemFlag.HIDE_ENCHANTS).withPersistantData(
							PluginKeys.CHALLENGE_KEY.getKey(), PersistentDataType.STRING, challenge.getKey());

			if (player.hasChallenge(challenge)) {
				itemBuilder.withEnchantment(Enchantment.DURABILITY, 1);
			}

			inventory.setItem(i, itemBuilder.build());
		}

		ItemStack filler = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).withName(" ").build();

		for (int i = 46; i < 53; i++) {
			inventory.setItem(i, filler);
		}

		if (start > 0) {
			inventory.addButton(45,
					new ItemBuilder(Material.ARROW).withName("Test").withPersistantData(
							PluginKeys.TARGET_INDEX.getKey(), PersistentDataType.INTEGER, start - 45).build(),
					this::gotoPage);
		} else {
			inventory.setItem(45, filler);
		}

		if (start + 45 < this.challengeCache.size()) {
			inventory.addButton(45,
					new ItemBuilder(Material.ARROW).withName("Test").withPersistantData(
							PluginKeys.TARGET_INDEX.getKey(), PersistentDataType.INTEGER, start - 45).build(),
					this::gotoPage);
		} else {
			inventory.setItem(53, filler);
		}
		return inventory;
	}

	private void gotoPage(InventoryClickEvent event) {
		PersistentDataContainer container = event.getCurrentItem().getItemMeta().getPersistentDataContainer();
		Optional<ChallengePlayer> playerOptional = this.plugin.getPlayerManager()
				.getPlayer(event.getWhoClicked().getUniqueId());

		if (playerOptional.isPresent()) {
			this.plugin.getInventoryManager().openGui(event.getWhoClicked(), getChallengeGUI(playerOptional.get(),
					container.get(PluginKeys.TARGET_INDEX.getKey(), PersistentDataType.INTEGER)));
		}
	}

	private void handleClick(InventoryClickEvent event) {
		event.setCancelled(true);

		if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta())
			return;

		ItemStack item = event.getCurrentItem();
		PersistentDataContainer container = event.getCurrentItem().getItemMeta().getPersistentDataContainer();

		if (!container.has(PluginKeys.CHALLENGE_KEY.getKey(), PersistentDataType.STRING))
			return;

		Optional<ChallengePlayer> playerOptional = this.plugin.getPlayerManager()
				.getPlayer(event.getWhoClicked().getUniqueId());

		Optional<Challenge> challengeOptional = this.plugin.getChallengeManager()
				.getChallenge(container.get(PluginKeys.CHALLENGE_KEY.getKey(), PersistentDataType.STRING));

		if (playerOptional.isPresent() && challengeOptional.isPresent()) {
			ChallengePlayer player = playerOptional.get();
			Challenge challenge = challengeOptional.get();

			if (player.hasChallenge(challenge)) {
				player.removeChallenge(challenge);
				item.removeEnchantment(Enchantment.DURABILITY);
			} else {
				player.addChallenge(challenge);
				item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			}
		}
	}

}
