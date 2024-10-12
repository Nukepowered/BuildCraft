package net.minecraft.src.buildcraft.builders.network;

import net.minecraft.src.*;
import net.minecraft.src.buildcraft.core.network.ISynchronizedTile;
import net.minecraft.src.buildcraft.core.network.PacketIds;
import net.minecraft.src.buildcraft.core.network.PacketTileUpdate;
import net.minecraft.src.forge.IPacketHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class PacketHandler implements IPacketHandler {

    @Override
    public void onPacketData(NetworkManager network, String channel, byte[] bytes) {
        DataInputStream data = new DataInputStream(new ByteArrayInputStream(bytes));
        try {
            NetServerHandler net = (NetServerHandler) network.getNetHandler();
            int packetID = data.read();
            switch (packetID) {
                case PacketIds.TILE_UPDATE:
                    PacketTileUpdate packetT = new PacketTileUpdate();
                    packetT.readData(data);
                    onTileUpdate(net.getPlayerEntity().worldObj, packetT);
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onTileUpdate(World world, PacketTileUpdate packet) {
        if (!packet.targetExists(world)) {
            return;
        }

        TileEntity entity = packet.getTarget(world);
        if (!(entity instanceof ISynchronizedTile)) {
            return;
        }

        ISynchronizedTile tile = (ISynchronizedTile) entity;
        tile.handleUpdatePacket(packet);
        tile.postPacketHandling(packet);
    }
}

