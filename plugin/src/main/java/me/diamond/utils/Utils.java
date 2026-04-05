package me.diamond.utils;

import me.diamond.SMP;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.apache.commons.lang3.ObjectUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;
import java.util.function.Function;

public class Utils {

    public static Component gradientText(String text, int startColor, int endColor) {
        Component result = Component.empty();
        int length = text.length();

        int startR = (startColor >> 16) & 0xFF;
        int startG = (startColor >> 8) & 0xFF;
        int startB = startColor & 0xFF;

        int endR = (endColor >> 16) & 0xFF;
        int endG = (endColor >> 8) & 0xFF;
        int endB = endColor & 0xFF;

        for (int i = 0; i < length; i++) {
            double ratio = i / (double) (length - 1);
            int r = (int) (startR + ratio * (endR - startR));
            int g = (int) (startG + ratio * (endG - startG));
            int b = (int) (startB + ratio * (endB - startB));

            result = result.append(
                    Component.text(String.valueOf(text.charAt(i)))
                            .color(TextColor.color(r, g, b))
            );
        }

        return result;
    }

    public static BossBar decreasingBossBar(Player player, Component text, BossBar.Color color, double durationSeconds, Runnable onFinish) {
        BossBar bar = BossBar.bossBar(text, 1f, color, BossBar.Overlay.PROGRESS);

        player.showBossBar(bar);

        new BukkitRunnable() {
            float progress = 1.0f;
            final double step = 1.0 / (durationSeconds * 2); // 2 updates/sec

            @Override
            public void run() {
                progress -= step;

                if (progress <= 0) {
                    onFinish.run();
                    player.hideBossBar(bar);
                    cancel();
                    return;
                }

                bar.progress(progress);
            }
        }.runTaskTimer(SMP.getPlugin(), 0, 10);
        return bar;
    }
}
