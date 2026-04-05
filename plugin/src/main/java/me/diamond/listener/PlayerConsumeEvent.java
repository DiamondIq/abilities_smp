package me.diamond.listener;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.diamond.abilities.AbilityManager;
import me.diamond.abilities.AbilityType;
import me.diamond.abilities.Inferno;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PlayerConsumeEvent implements Listener {
    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        Key model = event.getItem().getData(DataComponentTypes.ITEM_MODEL);
        if (model.equals(Key.key("smp", "inferno_ability"))) {
            ((Inferno) AbilityManager.getAbility(event.getPlayer(), AbilityType.INFERNO)).activate();
            event.getPlayer().setCooldown(event.getItem(), 20 * 20);
            event.setCancelled(true); // Prevent item from disappearing
        } else if (event.getItem().getType() == Material.PLAYER_HEAD) {
            event.getPlayer().setSaturation(event.getPlayer().getSaturation() + 5);
            event.getPlayer().setFoodLevel(event.getPlayer().getFoodLevel() + 3);
        }
    }
}
