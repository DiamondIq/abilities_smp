package me.diamond.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;

public class InventoryClickEvent implements Listener {

    @EventHandler
    public void onInventoryClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        // Ability recipe menu
        if (event.getView().getType() == InventoryType.WORKBENCH) {
            // 2. Extract and check the title text
            Component titleComponent = event.getView().title();
            String plainTitle = PlainTextComponentSerializer.plainText().serialize(titleComponent);

            if (plainTitle.contains("ABILITY RECIPE")) {
                // Cancel the event so they can't take items out of the preview
                event.setCancelled(true);
            }
        }
    }
}
