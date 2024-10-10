package buildcraft.transport;

import net.minecraft.src.Block;
import net.minecraft.src.BuildCraftCore;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.buildcraft.transport.BlockGenericPipe;
import net.minecraft.src.buildcraft.transport.PipeLogic;
import net.minecraft.src.mod_BuildCraftTransport;
import net.minecraft.src.buildcraft.api.APIProxy;
import net.minecraft.src.buildcraft.api.ISpecialInventory;
import net.minecraft.src.buildcraft.api.Orientations;
import net.minecraft.src.buildcraft.api.SafeTimeTracker;
import net.minecraft.src.buildcraft.core.CoreProxy;
import net.minecraft.src.buildcraft.core.DefaultProps;
import net.minecraft.src.buildcraft.core.GuiIds;
import net.minecraft.src.buildcraft.core.network.PacketIds;
import net.minecraft.src.buildcraft.core.network.PacketNBT;
import buildcraft.core.utils.SimpleInventory;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;

public class PipeLogicDiamond extends PipeLogic implements ISpecialInventory {

    private SimpleInventory filters = new SimpleInventory(54, "items", 1);
    private final SafeTimeTracker tracker = new SafeTimeTracker();

    /* PIPE LOGIC */
    @Override
    public boolean doDrop() {
        return false;
    }

    @Override
    public boolean blockActivated(EntityPlayer entityplayer) {
        if (entityplayer.getCurrentEquippedItem() != null
                && entityplayer.getCurrentEquippedItem().itemID < Block.blocksList.length)
            if (Block.blocksList[entityplayer.getCurrentEquippedItem().itemID] instanceof BlockGenericPipe)
                return false;

        if (!APIProxy.isClient(container.worldObj))
            entityplayer.openGui(mod_BuildCraftTransport.instance, GuiIds.PIPE_DIAMOND, container.worldObj, container.xCoord,
                    container.yCoord, container.zCoord);

        return true;
    }

    /* UPDATING */
    @Override
    public void updateEntity() {
        if (tracker.markTimeIfDelay(worldObj, 20 * BuildCraftCore.updateFactor))
            if (APIProxy.isServerSide())
                sendFilterSet();
    }

    /* SAVING & LOADING */
    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        filters.readFromNBT(nbttagcompound);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        filters.writeToNBT(nbttagcompound);
    }

    /* ISPECIALINVENTORY */
    @Override
    public boolean addItem(ItemStack stack, boolean doAdd, Orientations from) {
        return false;
    }

    @Override
    public ItemStack extractItem(boolean doRemove, Orientations from) {
        return null;
    }

    /* IINVENTORY IMPLEMENTATION */
    @Override public int getSizeInventory() { return filters.getSizeInventory(); }
    @Override public ItemStack getStackInSlot(int i) { return filters.getStackInSlot(i); }
    @Override public String getInvName() { return "Filters"; }
    @Override public int getInventoryStackLimit() { return filters.getInventoryStackLimit(); }
    @Override public ItemStack getStackInSlotOnClosing(int i) { return filters.getStackInSlotOnClosing(i); }
    @Override public void onInventoryChanged() { filters.onInventoryChanged(); }
    @Override public boolean isUseableByPlayer(EntityPlayer var1) { return true; }
    @Override public void openChest() {}
    @Override public void closeChest() {}

    @Override
    public ItemStack decrStackSize(int i, int j) {
        ItemStack stack = filters.decrStackSize(i, j);

        if (APIProxy.isServerSide())
            sendFilterSet();

        return stack;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {

        filters.setInventorySlotContents(i, itemstack);
        if (APIProxy.isServerSide())
            sendFilterSet();

    }

    /* SERVER SIDE */
    public void sendFilterSet() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.writeToNBT(nbttagcompound);
        PacketNBT packet = new PacketNBT(PacketIds.DIAMOND_PIPE_CONTENTS, nbttagcompound, xCoord, yCoord, zCoord);
        CoreProxy.sendToPlayers(packet.getPacket(), worldObj, xCoord, yCoord, zCoord, DefaultProps.NETWORK_UPDATE_RANGE, mod_BuildCraftTransport.instance);
    }

    /* CLIENT SIDE */
    public void handleFilterSet(PacketNBT packet) {
        this.readFromNBT(packet.getTagCompound());
    }

    // CraftBukkit start
    public ItemStack[] getContents() {
        return filters.getContents();
    }

    public void onOpen(CraftHumanEntity craftHumanEntity) {
        filters.onOpen(craftHumanEntity);
    }

    public void onClose(CraftHumanEntity craftHumanEntity) {
        filters.onClose(craftHumanEntity);
    }


    public List<HumanEntity> getViewers() {
        return filters.getViewers();
    }

    public InventoryHolder getOwner() {
        return new InventoryHolder() {
            @Override
            public Inventory getInventory() {
                return new CraftInventory(PipeLogicDiamond.this);
            }
        };
    }

    public void setMaxStackSize(int i) {
        filters.setMaxStackSize(i);
    }

    // CraftBukkit end
}
