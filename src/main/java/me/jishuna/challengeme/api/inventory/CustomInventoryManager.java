package me.jishuna.challengeme.api.inventory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import me.jishuna.challengeme.api.challenge.Category;
import me.jishuna.challengeme.api.challenge.Challenge;
import me.jishuna.challengeme.api.player.ChallengePlayer;
import me.jishuna.commonlib.inventory.CustomInventory;
import me.jishuna.commonlib.items.ItemBuilder;
import net.md_5.bungee.api.ChatColor;

public class CustomInventoryManager implements Listener {
	private static final String CHALLENGE_PLACEHOLDER = "%challenge%";

	private final HashMap<InventoryView, CustomInventory> inventoryMap = new HashMap<>();
	private final ChallengeMe plugin;

	private final DateFormat dateFormat = new SimpleDateFormat("mm:ss");
	private final DateFormat dateFormatHours = new SimpleDateFormat("HH:mm:ss");

	private CustomInventory categoryGUI;

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
		CustomInventory inventory = inventoryMap.get(event.getView());

		if (inventory != null) {
			inventory.consumeCloseEvent(event);

			this.inventoryMap.remove(event.getView());
		}
	}

	public CustomInventory getCategoryGUI() {
		return categoryGUI;
	}

	public void cacheCategoryGUI() {
		CustomInventory inventory = new CustomInventory(null, 54, this.plugin.getMessage("challenge-categories"));
		inventory.addClickConsumer(this::handleCategoryClick);

		for (Category category : this.plugin.getChallengeManager().getCategories()) {

			ItemBuilder itemBuilder = ItemBuilder.modifyItem(category.getIcon().clone()).withName(category.getName())
					.withLore(category.getDescription())
					.withFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS)
					.withPersistantData(PluginKeys.CATEGORY_KEY.getKey(), PersistentDataType.STRING, category.getKey());

			inventory.addItem(itemBuilder.build());
		}

		ItemStack filler = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).withName(" ").build();

		for (int i = 46; i < 54; i++) {
			inventory.setItem(i, filler);
		}

		ItemStack close = new ItemBuilder(Material.BARRIER)
				.withName(ChatColor.RED.toString() + ChatColor.BOLD + "Close").build();
		inventory.addButton(45, close, event -> event.getWhoClicked().closeInventory());

		this.categoryGUI = inventory;
	}

	public CustomInventory getChallengeGUI(ChallengePlayer player, Category category) {
		CustomInventory inventory = new CustomInventory(null, 54,
				this.plugin.getMessage("challenges").replace("%category%", category.getName()));
		inventory.addClickConsumer(this::handleChallengeClick);

		for (Challenge challenge : this.plugin.getChallengeManager().getChallenges(category)) {
			if (!challenge.isEnabled())
				continue;

			ItemBuilder itemBuilder = ItemBuilder.modifyItem(challenge.getIcon().clone()).withName(challenge.getName())
					.withLore(challenge.getDescription()).addLore("", challenge.getDifficulty())
					.withFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);

			if (challenge.isForced()) {
				itemBuilder.addLore("", this.plugin.getMessage("forced"));
				itemBuilder.withEnchantment(Enchantment.DURABILITY, 1);
			} else {
				itemBuilder.withPersistantData(PluginKeys.CHALLENGE_KEY.getKey(), PersistentDataType.STRING,
						challenge.getKey());

				if (player.hasChallenge(challenge)) {
					itemBuilder.withEnchantment(Enchantment.DURABILITY, 1);
					itemBuilder.addLore("", this.plugin.getMessage("enabled"));
				} else {
					itemBuilder.addLore("", this.plugin.getMessage("disabled"));
				}
			}

			inventory.addItem(itemBuilder.build());
		}

		ItemStack filler = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).withName(" ").build();

		for (int i = 46; i < 54; i++) {
			inventory.setItem(i, filler);
		}

		ItemStack back = new ItemBuilder(Material.ARROW).withName(ChatColor.RED.toString() + ChatColor.BOLD + "Back")
				.build();
		inventory.addButton(45, back, event -> openGui(event.getWhoClicked(), getCategoryGUI()));

		return inventory;
	}

	private void handleCategoryClick(InventoryClickEvent event) {
		event.setCancelled(true);

		if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta())
			return;

		PersistentDataContainer container = event.getCurrentItem().getItemMeta().getPersistentDataContainer();

		if (!container.has(PluginKeys.CATEGORY_KEY.getKey(), PersistentDataType.STRING))
			return;

		Player player = (Player) event.getWhoClicked();

		Optional<Category> categoryOptional = this.plugin.getChallengeManager()
				.getCategory(container.get(PluginKeys.CATEGORY_KEY.getKey(), PersistentDataType.STRING));

		Optional<ChallengePlayer> playerOptional = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());

		if (categoryOptional.isPresent() && playerOptional.isPresent()) {
			CustomInventory challengeInventory = getChallengeGUI(playerOptional.get(), categoryOptional.get());
			openGui(player, challengeInventory);
		}
	}

	private void handleChallengeClick(InventoryClickEvent event) {
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

			long cooldown = challengePlayer.getCooldown(challenge);

			if (cooldown > 0 && !player.hasPermission("challengeme.nocooldown")) {
				player.sendMessage(this.plugin.getMessage("on-cooldown")
						.replace(CHALLENGE_PLACEHOLDER, challenge.getName()).replace("%time%", getTimeLeft(cooldown)));
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
		player.sendMessage(
				this.plugin.getMessage("challenge-enabled").replace(CHALLENGE_PLACEHOLDER, challenge.getName()));

		ItemMeta meta = item.getItemMeta();
		meta.addEnchant(Enchantment.DURABILITY, 1, true);

		List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
		int pos = lore.size() - 1;

		if (!lore.isEmpty() && lore.get(pos).equals(disabled)) {
			lore.set(pos, enabled);
		}

		meta.setLore(lore);
		item.setItemMeta(meta);
	}

	private void disableChallenge(Player player, ChallengePlayer challengePlayer, Challenge challenge, ItemStack item) {
		String enabled = this.plugin.getMessage("enabled");
		String disabled = this.plugin.getMessage("disabled");

		challengePlayer.removeChallenge(challenge);
		player.sendMessage(
				this.plugin.getMessage("challenge-disabled").replace(CHALLENGE_PLACEHOLDER, challenge.getName()));

		ItemMeta meta = item.getItemMeta();
		meta.removeEnchant(Enchantment.DURABILITY);

		List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
		int pos = lore.size() - 1;

		if (!lore.isEmpty() && lore.get(pos).equals(enabled)) {
			lore.set(pos, disabled);
		}

		meta.setLore(lore);
		item.setItemMeta(meta);
	}

	private String getTimeLeft(long time) {

		if (time < 60 * 60 + 1000) {
			return dateFormat.format(new Date(time));
		} else {
			return dateFormatHours.format(new Date(time));
		}
	}
}
