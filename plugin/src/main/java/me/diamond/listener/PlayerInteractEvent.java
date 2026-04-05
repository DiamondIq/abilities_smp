package me.diamond.listener;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.diamond.abilities.AbilityManager;
import me.diamond.abilities.AbilityType;
import me.diamond.abilities.Hacker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerInteractEvent implements Listener {
    @EventHandler
    public void onInteract(org.bukkit.event.player.PlayerInteractEvent event) {
        event.setCancelled(checkIfBlinking(event.getPlayer()));
    }

    @EventHandler
    public void onInteract(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            event.setCancelled(checkIfBlinking(player));
        }
    }
    private boolean checkIfBlinking(Player player) {
        Hacker hacker = (Hacker) AbilityManager.getAbility(player, AbilityType.HACKER);
        boolean isBlinking = (hacker != null && hacker.isBlinking());
        if (isBlinking) {
            player.sendMessage(Component.text("You can't do this while blinking").color(NamedTextColor.RED));
        }
        return isBlinking;
    }
}
