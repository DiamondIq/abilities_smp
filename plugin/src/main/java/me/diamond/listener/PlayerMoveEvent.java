package me.diamond.listener;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.diamond.abilities.AbilityManager;
import me.diamond.abilities.AbilityType;
import me.diamond.abilities.Hacker;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerMoveEvent implements PacketListener {

    @Override
    public void onPacketSend(PacketSendEvent event) {
        Player viewer = event.getPlayer();

        int entityId = switch (event.getPacketType()) {
            case PacketType.Play.Server.ENTITY_RELATIVE_MOVE -> new WrapperPlayServerEntityRelativeMove(event).getEntityId();
            case PacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION -> new WrapperPlayServerEntityRelativeMoveAndRotation(event).getEntityId();
            case PacketType.Play.Server.ENTITY_ROTATION -> new WrapperPlayServerEntityRotation(event).getEntityId();
            case PacketType.Play.Server.ENTITY_TELEPORT -> new WrapperPlayServerEntityTeleport(event).getEntityId();
            case PacketType.Play.Server.ENTITY_POSITION_SYNC -> new WrapperPlayServerEntityPositionSync(event).getId();
            case PacketType.Play.Server.ENTITY_HEAD_LOOK -> new WrapperPlayServerEntityHeadLook(event).getEntityId();
            case PacketType.Play.Server.ENTITY_VELOCITY -> new WrapperPlayServerEntityVelocity(event).getEntityId();
            case PacketType.Play.Server.ENTITY_ANIMATION -> new WrapperPlayServerEntityAnimation(event).getEntityId();
            case PacketType.Play.Server.ENTITY_METADATA -> new WrapperPlayServerEntityMetadata(event).getEntityId();
            case PacketType.Play.Server.ENTITY_EQUIPMENT -> new WrapperPlayServerEntityEquipment(event).getEntityId();
            default -> -1;
        };
        if (entityId == -1) return;
        Entity entity = SpigotConversionUtil.getEntityById(viewer.getWorld(), entityId);
        if (!(entity instanceof Player target)) return;

        // Don't cancel packets sent to the blinking player themselves
        if (viewer.equals(target)) return;

        Hacker hacker = (Hacker) AbilityManager.getAbility(target, AbilityType.HACKER);
        if (hacker != null && hacker.isBlinking()) {
            event.setCancelled(true);
        }
    }
}
