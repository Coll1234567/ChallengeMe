package me.jishuna.challengeme.api.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import me.jishuna.challengeme.ChallengeMe;
import me.jishuna.challengeme.PluginKeys;
import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.player.ChallengePlayer;
import me.jishuna.commonlib.CustomInventory;
import me.jishuna.commonlib.ItemBuilder;

public class CustomInventoryManager implements Listener {

	private final HashMap<InventoryView, CustomInventory> inventoryMap = new HashMap<>();
	private final ChallengeMe plugin;

	public CustomInventoryManager(ChallengeMe plugin) {
		super();
		this.plugin = plugin;
	}

	public void openGui(HumanEntity player, CustomInventory inventory) {
		this.inventoryMap.put(inventory.open(player), inventory);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getClickedInventory() == null)
			return;

		CustomInventory inventory = this.inventoryMap.get(event.getView());

		if (inventory != null) {
			inventory.consumeClickEvent(event);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getInventory() == null)
			return;

		CustomInventory inventory = inventoryMap.get(event.getView());

		if (inventory != null) {
			inventory.consumeCloseEvent(event);

			this.inventoryMap.remove(event.getView());
		}
	}

	public CustomInventory getChallengeGUI(ChallengePlayer player, int start) {
		CustomInventory inventory = new CustomInventory(null, 54, "Challenges");
		inventory.addClickConsumer(this::handleClick);
		List<Challenge> challengeCache = this.plugin.getChallengeManager().getChallengeCache();

		for (int i = 0; i < Math.min(45, challengeCache.size() - start); i++) {
			Challenge challenge = challengeCache.get(i);

			ItemBuilder itemBuilder = ItemBuilder.modifyItem(challenge.getIcon()).withName(challenge.getName())
					.withLore(challenge.getDescription())
					.withFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS)
					.withPersistantData(PluginKeys.CHALLENGE_KEY.getKey(), PersistentDataType.STRING,
							challenge.getKey());

			if (player.hasChallenge(challenge)) {
				itemBuilder.withEnchantment(Enchantment.DURABILITY, 1);
				itemBuilder.addLore("", this.plugin.getMessage("enabled"));
			} else {
				itemBuilder.addLore("", this.plugin.getMessage("disabled"));
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

		if (start + 45 < challengeCache.size()) {
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

		Player player = (Player) event.getWhoClicked();

		Optional<ChallengePlayer> playerOptional = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());

		Optional<Challenge> challengeOptional = this.plugin.getChallengeManager()
				.getChallenge(container.get(PluginKeys.CHALLENGE_KEY.getKey(), PersistentDataType.STRING));

		if (playerOptional.isPresent() && challengeOptional.isPresent()) {
			ChallengePlayer challengePlayer = playerOptional.get();
			Challenge challenge = challengeOptional.get();

			if (challengePlayer.isOnCooldown(challenge)) {
				player.sendMessage(this.plugin.getMessage("on-cooldown").replace("%challenge%", challenge.getName()));
				return;
			}

			if (challengePlayer.hasChallenge(challenge)) {
				disableChallenge(player, challengePlayer, challenge, item);
			} else {
				enableChallenge(player, challengePlayer, challenge, item);
			}
			challengePlayer.setCooldown(challenge, this.plugin.getConfig().getInt("cooldown", 300));
		}
	}

	private void enableChallenge(Player player, ChallengePlayer challengePlayer, Challenge challenge, ItemStack item) {
		String enabled = this.plugin.getMessage("enabled");
		String disabled = this.plugin.getMessage("disabled");

		challengePlayer.addChallenge(challenge);
		player.sendMessage(this.plugin.getMessage("challenge-enabled").replace("%challenge%", challenge.getName()));

		item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
		int pos = lore.size() - 1;

		if (lore.size() > 0 && lore.get(pos).equals(disabled)) {
			lore.set(pos, enabled);
		}

		meta.setLore(lore);
		item.setItemMeta(meta);
	}

	private void disableChallenge(Player player, ChallengePlayer challengePlayer, Challenge challenge, ItemStack item) {
		String enabled = this.plugin.getMessage("enabled");
		String disabled = this.plugin.getMessage("disabled");

		challengePlayer.removeChallenge(challenge);
		player.sendMessage(this.plugin.getMessage("challenge-disabled").replace("%challenge%", challenge.getName()));

		item.removeEnchantment(Enchantment.DURABILITY);

		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
		int pos = lore.size() - 1;

		if (lore.size() > 0 && lore.get(pos).equals(enabled)) {
			lore.set(pos, disabled);
		}

		meta.setLore(lore);
		item.setItemMeta(meta);
	}
}
