package me.diamond.listener;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import me.diamond.abilities.Ability;
import me.diamond.abilities.AbilityManager;
import me.diamond.abilities.AbilityType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class PlayerDeathEvent implements Listener {
    @EventHandler
    public void onDeath(org.bukkit.event.entity.PlayerDeathEvent event) {
        Player player = event.getPlayer();

        //Only drop if player dies to a different player
        if (event.getDamageSource().getCausingEntity() instanceof Player) {
            ItemStack head = ItemStack.of(Material.PLAYER_HEAD);
            //Make edible
            head.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                    .consumeSeconds(1.6f)
                    .addEffect(ConsumeEffect.applyStatusEffects(List.of(new PotionEffect(PotionEffectType.REGENERATION, 3 * 20, 1)), 1f)) // effect: 5s regeneration I
                    .build());

            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(player);
                meta.setLore(List.of(ChatColor.GRAY + "Eat to gain regeneration"));
                head.setItemMeta(meta);
            }

            // Add the head to dropped loot
            event.getDrops().add(head);
        }

        //Drop abilities
        for (AbilityType ability : AbilityManager.getAbilities(player)) {
            event.getDrops().add(Ability.getEquipItem(ability));
            AbilityManager.removeAbility(player, ability);
        }
        player.sendMessage(Component.text("You died and dropped all of your abilities").color(NamedTextColor.RED));
    }
}
