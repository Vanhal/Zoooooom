package tv.vanhal.zoooooom.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

//Most of this is based of code by Dynious
//Source: https://github.com/Dynious/RefinedRelocation/blob/master/src/main/java/com/dynious/refinedrelocation/helper/IOHelper.java
public class InventoryHelper {
    public static ItemStack insert(TileEntity tile, ItemStack itemStack, EnumFacing side, boolean simulate) {
        if (tile instanceof IInventory) {
            return insert((IInventory) tile, itemStack, side, simulate);
        }
        return itemStack;
    }

    public static ItemStack insert(IInventory inventory, ItemStack itemStack, EnumFacing side, boolean simulate) {
        ItemStack stackInSlot;
        int emptySlot = -1;

        if (inventory instanceof ISidedInventory && side != null) {
            ISidedInventory isidedinventory = (ISidedInventory) inventory;
            int[] aint = isidedinventory.getSlotsForFace(side);

            for (int j = 0; j < aint.length && itemStack != null && itemStack.stackSize > 0; ++j) {
                if (canInsertItemToInventory(inventory, itemStack, aint[j], side)) {
                    stackInSlot = inventory.getStackInSlot(aint[j]);
                    if (stackInSlot == null) {
                        if (simulate)
                            return null;

                        if (emptySlot == -1)
                            emptySlot = aint[j];
                        continue;
                    }
                    itemStack = insert(inventory, itemStack, stackInSlot, aint[j], simulate);
                }
            }
        } else {
            int invSize = inventory.getSizeInventory();

            for (int slot = 0; slot < invSize && itemStack != null && itemStack.stackSize > 0; ++slot) {
                if (canInsertItemToInventory(inventory, itemStack, slot, side)) {
                    stackInSlot = inventory.getStackInSlot(slot);
                    if (stackInSlot == null) {
                        if (simulate)
                            return null;

                        if (emptySlot == -1)
                            emptySlot = slot;
                        continue;
                    }
                    itemStack = insert(inventory, itemStack, stackInSlot, slot, simulate);
                }
            }
        }

        if (itemStack != null && itemStack.stackSize != 0 && emptySlot != -1) {
            stackInSlot = inventory.getStackInSlot(emptySlot);
            itemStack = insert(inventory, itemStack, stackInSlot, emptySlot, simulate);
        }

        if (itemStack != null && itemStack.stackSize == 0) {
            itemStack = null;
        }

        return itemStack;
    }
	
    public static ItemStack insert(IInventory inventory, ItemStack itemStack, ItemStack stackInSlot, int slot, boolean simulate) {
        boolean flag = false;

        if (stackInSlot == null) {
            int max = Math.min(itemStack.getMaxStackSize(), inventory.getInventoryStackLimit());
            if (max >= itemStack.stackSize) {
                if (!simulate) {
                    inventory.setInventorySlotContents(slot, itemStack);
                    flag = true;
                }
                itemStack = null;
            } else {
                if (!simulate) {
                    inventory.setInventorySlotContents(slot, itemStack.splitStack(max));
                    flag = true;
                } else {
                    itemStack.splitStack(max);
                }
            }
        } else if (areItemStacksEqual(stackInSlot, itemStack, true, true)) {
            int max = Math.min(itemStack.getMaxStackSize(), inventory.getInventoryStackLimit());
            if (max > stackInSlot.stackSize) {
                int l = Math.min(itemStack.stackSize, max - stackInSlot.stackSize);
                itemStack.stackSize -= l;
                if (!simulate) {
                    stackInSlot.stackSize += l;
                    flag = l > 0;
                }
            }
        }
        if (flag) {
            inventory.markDirty();
        }

        return itemStack;
    }
    
    public static boolean doesInventoryHaveItem(TileEntity tile, ItemStack itemStack, EnumFacing direction) {
    	if (isInventory(tile, direction)) {
    		IInventory inventory = (IInventory)tile;
    		if (inventory instanceof ISidedInventory) {
                ISidedInventory iSidedInventory = (ISidedInventory) inventory;
                int[] accessibleSlotsFromSide = iSidedInventory.getSlotsForFace(direction);

                for (int anAccessibleSlotsFromSide : accessibleSlotsFromSide) {
                	if (areItemStacksEqual(itemStack, iSidedInventory.getStackInSlot(anAccessibleSlotsFromSide), true, true)) 
                		return true;
                }
            } else {
                int j = inventory.getSizeInventory();

                for (int slot = 0; slot < j; ++slot) {
                	if (areItemStacksEqual(itemStack, inventory.getStackInSlot(slot), true, true)) 
                		return true;
                }
            }
    	}
    	return false;
    }

	public static boolean canInsertItemToInventory(IInventory inventory, ItemStack itemStack, int slot, EnumFacing side) {
		if (inventory.isItemValidForSlot(slot, itemStack)) {
			if (!(inventory instanceof ISidedInventory)) return true;
			else if (((ISidedInventory) inventory).canInsertItem(slot, itemStack, side)) return true;
		}
		return false;
	}

	public static boolean canExtractItemFromInventory(IInventory inventory, ItemStack itemStack, int slot, EnumFacing side) {
		return !(inventory instanceof ISidedInventory) || ((ISidedInventory) inventory).canExtractItem(slot, itemStack, side);
	}
	
    public static boolean isInventory(TileEntity tile, EnumFacing side) {
        if (tile instanceof IInventory) {
            return !(tile instanceof ISidedInventory) || ((ISidedInventory) tile).getSlotsForFace(side).length > 0;
        }
        return false;
    }
    
    public static boolean areItemStacksEqual(ItemStack itemStack1, ItemStack itemStack2, boolean checkMeta, boolean checkNBT) {
        return itemStack1 == null && itemStack2 == null || (!(itemStack1 == null || itemStack2 == null) && (itemStack1.getItem() == itemStack2.getItem() && ((!checkMeta || itemStack1.getItemDamage() == itemStack2.getItemDamage()) && (!checkNBT || !(itemStack1.getTagCompound() == null && itemStack2.getTagCompound() != null) && (itemStack1.getTagCompound() == null || itemStack1.getTagCompound().equals(itemStack2.getTagCompound()))))));
    }
}
