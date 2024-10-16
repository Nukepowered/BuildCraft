package buildcraft.core.utils;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.buildcraft.api.Orientations;
import net.minecraft.src.forge.ISidedInventory;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;

/**
 * This class is responsible for abstracting an ISidedInventory as a normal
 * IInventory
 * 
 * @author Krapht
 * 
 */
public class SidedInventoryAdapter implements IInventory {

	private final ISidedInventory _sidedInventory;
	private final Orientations _side;
	private final int _slotOffset;

	public SidedInventoryAdapter(ISidedInventory sidedInventory, Orientations side) {
		_sidedInventory = sidedInventory;
		_side = side;
		_slotOffset = _sidedInventory.getStartInventorySide(side.ordinal());
	}

	@Override
	public int getSizeInventory() {
		return _sidedInventory.getSizeInventorySide(_side.ordinal());
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return _sidedInventory.getStackInSlot(i + _slotOffset);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return _sidedInventory.decrStackSize(i + _slotOffset, j);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		_sidedInventory.setInventorySlotContents(i + _slotOffset, itemstack);
	}

	@Override
	public String getInvName() {
		return _sidedInventory.getInvName();
	}

	@Override
	public int getInventoryStackLimit() {
		return _sidedInventory.getInventoryStackLimit();
	}

	@Override
	public void onInventoryChanged() {
		_sidedInventory.onInventoryChanged();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return _sidedInventory.isUseableByPlayer(entityplayer);
	}

	@Override
	public void openChest() {
		_sidedInventory.openChest();
	}

	@Override
	public void closeChest() {
		_sidedInventory.closeChest();
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return _sidedInventory.getStackInSlotOnClosing(slot + _slotOffset);
	}

	// CraftBukkit start
	public ItemStack[] getContents() {
		return _sidedInventory.getContents();
	}

	public void onOpen(CraftHumanEntity var1) {
		_sidedInventory.onOpen(var1);
	}

	public void onClose(CraftHumanEntity var1) {
		_sidedInventory.onClose(var1);
	}

	public List<HumanEntity> getViewers() {
		return _sidedInventory.getViewers();
	}

	public InventoryHolder getOwner() {
		return _sidedInventory.getOwner();
	}

	public void setMaxStackSize(int var1) {
		_sidedInventory.setMaxStackSize(var1);
	}
	// CraftBukkit end
}
