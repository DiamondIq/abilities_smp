package me.diamond.utils;

import me.diamond.SMP;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

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

    public static void playItemPopup(Player player, ItemStack item) {
        Location eyeLoc = player.getEyeLocation();
        Location spawnLoc = eyeLoc.clone().add(eyeLoc.getDirection().multiply(1.2));

        ItemDisplay itemDisplay = (ItemDisplay) player.getWorld().spawnEntity(spawnLoc, EntityType.ITEM_DISPLAY);
        itemDisplay.setItemStack(item);
        itemDisplay.setBillboard(Display.Billboard.CENTER);

        // Smooth out the teleports
        itemDisplay.setTeleportDuration(1);

        int duration = 30; // Total life of the popup animation

        // 1. Set the INITIAL spawn state (Tiny scale, zero offset)
        itemDisplay.setTransformation(new Transformation(
                new Vector3f(0, 0, 0),
                new org.joml.AxisAngle4f(),
                new Vector3f(0.1F, 0.1F, 0.1F),
                new org.joml.AxisAngle4f()
        ));

        // 2. Run the camera tracking loop
        new org.bukkit.scheduler.BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!player.isOnline() || !itemDisplay.isValid() || ticks > duration) {
                    itemDisplay.remove();
                    this.cancel();
                    return;
                }

                // STAGE 1: Fast Burst (Ticks 1 to 14)
                // Snaps out quickly, completing the bulk of the rotation and scale
                if (ticks == 1) {
                    itemDisplay.setInterpolationDuration(13); // Run for 13 ticks
                    itemDisplay.setInterpolationDelay(0);

                    itemDisplay.setTransformation(new Transformation(
                            new Vector3f(0, 0.35F, 0),
                            new org.joml.AxisAngle4f(-((float) Math.toRadians(160)), 0, 1, 0),
                            new Vector3f(1.1F, 1.1F, 1.1F), // 85% of full scale
                            new org.joml.AxisAngle4f()
                    ));
                }

                // STAGE 2: The Ease-Out Cushion (Ticks 14 to 30)
                // Gently drifts into place, finishes the turn, and reaches full size slowly
                if (ticks == 14) {
                    itemDisplay.setInterpolationDuration(16); // Take 16 ticks for the remaining tiny gap
                    itemDisplay.setInterpolationDelay(0);

                    itemDisplay.setTransformation(new Transformation(
                            new Vector3f(0, 0.4F, 0), // Final float height
                            new org.joml.AxisAngle4f(-((float) Math.toRadians(180) + 0.1F), 0, 1, 0), // Final rotation
                            new Vector3f(1.3F, 1.3F, 1.3F), // FIXED: Restored target scale size!
                            new org.joml.AxisAngle4f()
                    ));
                }

                // Keep the center of the animation locked onto their crosshair vector
                Location updatedLoc = player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(1.2));
                itemDisplay.teleport(updatedLoc);

                ticks++;
            }
        }.runTaskTimer(SMP.getPlugin(), 0L, 1L);

        //Todo - Make it lower on the screen
    }
}
