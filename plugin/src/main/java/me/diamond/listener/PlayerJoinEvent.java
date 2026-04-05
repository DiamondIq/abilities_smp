package me.diamond.listener;

import me.diamond.abilities.AbilityManager;
import me.diamond.abilities.AbilityType;
import me.diamond.utils.ActionBarManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerJoinEvent implements Listener {
    @EventHandler
    public void onJoinEvent(org.bukkit.event.player.PlayerJoinEvent event) {
        AbilityManager.grantAbility(event.getPlayer(), AbilityType.INFERNO);
        AbilityManager.grantAbility(event.getPlayer(), AbilityType.SORCERER);
        AbilityManager.grantAbility(event.getPlayer(), AbilityType.HACKER);

        ActionBarManager.startActionBar(event.getPlayer(), Component.text("\uE002").font(Key.key("smp", "custom"))
                .append(Component.text("      ").font(Key.key("minecraft", "default")))
                .append(Component.text("\uE003").font(Key.key("smp", "custom")))
                .append(Component.text("      ").font(Key.key("minecraft", "default")))
                .append(Component.text("\uE001").font(Key.key("smp", "custom"))));
    }
}
