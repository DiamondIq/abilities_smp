package me.diamond.utils;

import me.diamond.SMP;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class ActionBarManager {
    private static final Map<Player, BukkitRunnable> actionBars = new HashMap<>();

    public static void startActionBar(Player player, Component text) {
        stopActionBar(player); // prevent duplicates

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                player.sendActionBar(text);
            }
        };

        task.runTaskTimer(SMP.getPlugin(), 0, 20);
        actionBars.put(player, task);
    }

    public static void stopActionBar(Player player) {
        if (actionBars.containsKey(player)) {
            actionBars.get(player).cancel();
            actionBars.remove(player);
        }
    }
}
