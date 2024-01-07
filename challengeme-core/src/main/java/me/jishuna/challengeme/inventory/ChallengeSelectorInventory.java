package me.jishuna.challengeme.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import me.jishuna.challengeme.Registries;
import me.jishuna.challengeme.challenge.Challenge;
import me.jishuna.challengeme.player.ChallengePlayer;
import me.jishuna.jishlib.inventory.InventorySession;
import me.jishuna.jishlib.inventory.PagedCustomInventory;
import me.jishuna.jishlib.item.ItemBuilder;
import me.jishuna.jishlib.message.MessageAPI;

public class ChallengeSelectorInventory extends PagedCustomInventory<Challenge, Inventory> {
    private final ChallengePlayer player;

    public ChallengeSelectorInventory(ChallengePlayer player) {
        super(Bukkit.createInventory(null, 54, "Challenges"), Registries.CHALLENGE.getValues(), 45);
        this.player = player;

        addClickConsumer(event -> event.setCancelled(true));

        refreshOptions();
    }

    @Override
    protected ItemStack asItemStack(Challenge challenge) {
        return getChallengeItem(challenge);
    }

    @Override
    protected void onItemClicked(InventoryClickEvent event, InventorySession session, Challenge challenge) {
        if (!challenge.isEnabled() || challenge.isForced()) {
            return;
        }

        if (this.player.isChallengeActive(challenge)) {
            this.player.deactivateChallenge(challenge);
        } else {
            this.player.activateChallenge(challenge);
        }

        setItem(event.getSlot(), getChallengeItem(challenge));
    }

    private ItemStack getChallengeItem(Challenge challenge) {
        ItemBuilder builder = ItemBuilder.create(Material.PAPER).lore(challenge.getDescription()).hideAll();

        String activeString;
        if (!challenge.isEnabled()) {
            activeString = MessageAPI.get("challenge.disabled");
        } else if (challenge.isForced()) {
            activeString = MessageAPI.get("challenge.forced");
        } else if (this.player.isChallengeActive(challenge)) {
            builder.enchantment(Enchantment.DURABILITY, 1);
            activeString = MessageAPI.get("challenge.active");
        } else {
            activeString = MessageAPI.get("challenge.inactive");
        }

        builder.name(MessageAPI.get("challenge.active-name", challenge.getReadableName(), activeString));
        return builder.build();
    }
}
