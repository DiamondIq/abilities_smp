package me.diamond.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PlayerConsumeEvent implements Listener {
    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (event.getItem().getType() == Material.PLAYER_HEAD) {
            player.setSaturation(player.getSaturation() + 9);
            player.setFoodLevel(player.getFoodLevel() + 6);
        }
    }
}
