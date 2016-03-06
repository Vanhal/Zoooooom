package tv.vanhal.zoooooom.util;

import tv.vanhal.zoooooom.Zoooooom;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

//Most of this is based of code by Dynious
//Source: https://github.com/Dynious/RefinedRelocation/blob/master/src/main/java/com/dynious/refinedrelocation/helper/IOHelper.java
public class InventoryHelper {
	
    public static ItemStack extract(IInventory inventory, ItemStack itemStack, EnumFacing face, boolean oreDict, boolean simulate) {
        if (itemStack == null)
            return null;

        ItemStack toExtract = itemStack.copy();

        if (inventory instanceof ISidedInventory) {
            ISidedInventory iSidedInventory = (ISidedInventory) inventory;
            int[] accessibleSlotsFromSide = iSidedInventory.getSlotsForFace(face);

            for (int anAccessibleSlotsFromSide : accessibleSlotsFromSide) {
                ItemStack stack = extract(inventory, toExtract, face, anAccessibleSlotsFromSide, oreDict, simulate);
                if (stack != null) {
                    if (stack.stackSize >= toExtract.stackSize) {
                        return itemStack;
                    } else {
                        toExtract.stackSize -= stack.stackSize;
                    }
                }
            }
        } else {
            int j = inventory.getSizeInventory();

            for (int slot = 0; slot < j; ++slot) {
                ItemStack stack = extract(inventory, toExtract, face, slot, oreDict, simulate);
                if (stack != null) {
                    if (stack.stackSize >= toExtract.stackSize) {
                        return itemStack;
                    } else {
                        toExtract.stackSize -= stack.stackSize;
                    }
                }
            }
        }
        if (itemStack.stackSize == toExtract.stackSize) {
            return null;
        } else {
            inventory.markDirty();
            itemStack.stackSize -= toExtract.stackSize;
            return itemStack;
        }
    }

    public static ItemStack extract(IInventory inventory, ItemStack stack, EnumFacing face, int slot, boolean oreDict, boolean simulate) {
        ItemStack itemstack = inventory.getStackInSlot(slot);

        if (itemstack != null && canExtractItemFromInventory(inventory, itemstack, slot, face)
        		&& ((oreDict && ItemHelper.areOreDictEntriesSame(itemstack, stack)) ||
        				areItemStacksEqual(itemstack, stack, true, true))) {
            if (itemstack.stackSize > stack.stackSize) {
                if (!simulate)
                    inventory.decrStackSize(slot, stack.stackSize);
                return stack;
            } else {
                if (!simulate)
                    inventory.setInventorySlotContents(slot, null);
                return itemstack;
            }
        }
        return null;
    }
    
    public static ItemStack extract(IInventory inventory, EnumFacing face) {
    	return extract(inventory, face, false);
    }

    public static ItemStack extract(IInventory inventory, EnumFacing face, boolean simulate) {
        if (inventory instanceof ISidedInventory) {
            ISidedInventory iSidedInventory = (ISidedInventory) inventory;
            int[] accessibleSlotsFromSide = iSidedInventory.getSlotsForFace(face);

            for (int anAccessibleSlotsFromSide : accessibleSlotsFromSide) {
                ItemStack stack = extract(inventory, face, anAccessibleSlotsFromSide, simulate);
                if (stack != null)
                    return stack;
            }
        } else {
            int j = inventory.getSizeInventory();

            for (int slot = 0; slot < j; ++slot) {
                ItemStack stack = extract(inventory, face, slot, simulate);
                if (stack != null)
                    return stack;
            }
        }
        return null;
    }
    
    public static ItemStack extract(IInventory inventory, EnumFacing face, int slot) {
    	return extract(inventory, face, slot, false);
    }

    public static ItemStack extract(IInventory inventory, EnumFacing face, int slot, boolean simulate) {
        ItemStack itemstack = inventory.getStackInSlot(slot);

        if (itemstack != null && canExtractItemFromInventory(inventory, itemstack, slot, face)) {
            if (!simulate) inventory.setInventorySlotContents(slot, null);
            return itemstack;
        }
        return null;
    }
    
    public static ItemStack extractAmount(TileEntity inventory, EnumFacing face, int amt, boolean simulate) {
    	if (isInventory(inventory, face)) {
	    	 ItemStack stack = extract((IInventory)inventory, face, true);
	    	 if (stack != null) {
	    		 ItemStack test = stack.copy();
	    		 test.stackSize = amt;
	    		 return extract((IInventory)inventory, test, face, false, simulate);
	    	 }
	    	 return stack;
    	}
    	return null;
    }
	
	
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
    
    public static boolean doesInventoryHaveItems(TileEntity tile, EnumFacing face) {
    	return doesInventoryHaveItem(tile, null, face);
    }
    
    public static boolean doesInventoryHaveItem(TileEntity tile, ItemStack itemStack, EnumFacing face) {
    	if (isInventory(tile, face)) {
    		IInventory inventory = (IInventory)tile;
    		if (inventory instanceof ISidedInventory) {
                ISidedInventory iSidedInventory = (ISidedInventory) inventory;
                int[] accessibleSlotsFromSide = iSidedInventory.getSlotsForFace(face);

                for (int anAccessibleSlotsFromSide : accessibleSlotsFromSide) {
                	if (itemStack==null) {
                		if (iSidedInventory.getStackInSlot(anAccessibleSlotsFromSide)!=null) return true;
                	} else if (areItemStacksEqual(itemStack, iSidedInventory.getStackInSlot(anAccessibleSlotsFromSide), true, true)) 
                		return true;
                }
            } else {
                int j = inventory.getSizeInventory();

                for (int slot = 0; slot < j; ++slot) {
	                if (itemStack==null) {
	                	if (inventory.getStackInSlot(slot)!=null) {
	                		return true;
	                	}
	                } else if (areItemStacksEqual(itemStack, inventory.getStackInSlot(slot), true, true)) {
	            		return true;
	                }
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
