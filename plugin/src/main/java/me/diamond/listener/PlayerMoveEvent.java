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

        int entityId = -1;

        // Get entityId depending on packet type
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE) {
            entityId = new WrapperPlayServerEntityRelativeMove(event).getEntityId();
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION) {
            entityId = new WrapperPlayServerEntityRelativeMoveAndRotation(event).getEntityId();
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_ROTATION) {
            entityId = new WrapperPlayServerEntityRotation(event).getEntityId();
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_TELEPORT) {
            entityId = new WrapperPlayServerEntityTeleport(event).getEntityId();
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_POSITION_SYNC) {
            entityId = new WrapperPlayServerEntityPositionSync(event).getId();
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_HEAD_LOOK) {
            entityId = new WrapperPlayServerEntityHeadLook(event).getEntityId();
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_VELOCITY) {
            entityId = new WrapperPlayServerEntityVelocity(event).getEntityId();
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_ANIMATION) {
            entityId = new WrapperPlayServerEntityAnimation(event).getEntityId();
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_EQUIPMENT) {
            entityId = new WrapperPlayServerEntityEquipment(event).getEntityId();
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA) {
            entityId = new WrapperPlayServerEntityMetadata(event).getEntityId();
        } else {
            return;
        }


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
