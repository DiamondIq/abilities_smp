package me.diamond.listener;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataType;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.data.EntityMetadataProvider;
import com.github.retrooper.packetevents.protocol.entity.pose.EntityPose;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientAnimation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import me.diamond.abilities.AbilityManager;
import me.diamond.abilities.AbilityType;
import me.diamond.abilities.Inferno;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.List;

public class PlayerConsumeEvent implements Listener {
    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Key model = event.getItem().getData(DataComponentTypes.ITEM_MODEL);
        if (model.equals(Key.key("smp", "inferno_ability"))) {
            ((Inferno) AbilityManager.getAbility(player, AbilityType.INFERNO)).activate();
            player.setCooldown(event.getItem(), 20 * 20);
            event.setCancelled(true); // Prevent item from disappearing
        } else if (event.getItem().getType() == Material.PLAYER_HEAD) {
            player.setSaturation(player.getSaturation() + 5);
            player.setFoodLevel(player.getFoodLevel() + 3);
        }
    }
}
