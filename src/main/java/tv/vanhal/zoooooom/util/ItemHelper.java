package tv.vanhal.zoooooom.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class ItemHelper {
	public static void dropAsItem(World worldObj, BlockPos pos, Block block, int meta) {
		if (block!=null) {
			ItemStack itemStack = new ItemStack(block, 1, meta);
			dropAsItem(worldObj, pos, itemStack);
		}
	}
	
	
	public static void dropAsItem(World worldObj, BlockPos pos, ItemStack itemStack) {
        if (!worldObj.isRemote) {
            float f = 0.7F;
            double d0 = (double)(worldObj.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double d1 = (double)(worldObj.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double d2 = (double)(worldObj.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(worldObj, (double)pos.getX() + d0, (double)pos.getY() + d1,
            		(double)pos.getZ() + d2, itemStack);
            worldObj.spawnEntityInWorld(entityitem);
        }
    }
	
	public static void dropBlockIntoWorld(World worldObj, BlockPos pos, Block block, IBlockState state) {
		List<ItemStack> drops = block.getDrops(worldObj, pos, state, 0);
		for (ItemStack itemStack: drops) {
			dropAsItem(worldObj, pos, itemStack);
		}
		worldObj.setBlockToAir(pos);
	}
	
	public static ArrayList<ItemStack> getBlockContents(World world, BlockPos pos, TileEntity tileEntity) {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		if (tileEntity!=null) {
			if (tileEntity instanceof IInventory) {
				IInventory inventory = (IInventory)tileEntity;
				for (int i = 0; i < inventory.getSizeInventory(); ++i) {
	                ItemStack itemstack = inventory.getStackInSlot(i);
	                if (itemstack != null) {
	                	items.add(itemstack);
	                }
	            }
			}
		}
		
		return items;
	}
	
	public static boolean areStacksEqual(ItemStack stack1, ItemStack stack2) {
		if ( (stack1 == null) && (stack2 == null) ) return true;
		if (stack1 == null) return false;
		if (stack2 == null) return false;
		return ( 
			(stack1.isItemEqual(stack2)) && 
			(stack1.stackSize == stack2.stackSize) && 
			(stack1.getItemDamage() == stack2.getItemDamage())
		);
	}
	
    public static boolean areStacksSame(ItemStack itemStack1, ItemStack itemStack2, boolean checkMeta, boolean checkNBT) {
        return itemStack1 == null && itemStack2 == null || (!(itemStack1 == null || itemStack2 == null) && (itemStack1.getItem() == itemStack2.getItem() && ((!checkMeta || itemStack1.getItemDamage() == itemStack2.getItemDamage()) && (!checkNBT || !(itemStack1.getTagCompound() == null && itemStack2.getTagCompound() != null) && (itemStack1.getTagCompound() == null || itemStack1.getTagCompound().equals(itemStack2.getTagCompound()))))));
    }
	
	public static boolean isFuel(ItemStack itemStack) {
		return (getBurnTime(itemStack)>0);
	}
	
	public static int getBurnTime(ItemStack itemStack) {
		return TileEntityFurnace.getItemBurnTime(itemStack);
	}
	

    public static boolean areOreDictEntriesSame(ItemStack itemStack1, ItemStack itemStack2) {
        int[] a = OreDictionary.getOreIDs(itemStack1);
        if (a.length != 0)
        {
            int[] b = OreDictionary.getOreIDs(itemStack2);
            for (int bb : b)
            {
                for (int aa : a)
                {
                    if (aa == bb)
                        return true;
                }
            }
        }
        return false;
    }
	
	public static boolean clickAddToTile(World world, BlockPos pos, EntityPlayer player, int slot) {
		if (player.isSneaking()) {
			if (!world.isRemote) {
				if (player.getCurrentEquippedItem()==null) {
					TileEntity tile = world.getTileEntity(pos);
					if ( (tile != null) && (tile instanceof ISidedInventory) ) {
						ISidedInventory inventory = (ISidedInventory)tile;
						if (inventory.getStackInSlot(slot)!=null) {
							dropAsItem(world, pos.up(), inventory.getStackInSlot(slot));
							inventory.setInventorySlotContents(slot, null);
						}
					}
				}
			}
			return true;
		} else if (player.getCurrentEquippedItem() != null) {
			TileEntity tile = world.getTileEntity(pos);
			if ( (tile != null) && (tile instanceof ISidedInventory) ) {
				ISidedInventory inventory = (ISidedInventory)tile;
				if (inventory.isItemValidForSlot(slot, player.getCurrentEquippedItem())) {
					if (!world.isRemote) {
						if (inventory.canInsertItem(slot, player.getCurrentEquippedItem(), null)) {
							if (inventory.getStackInSlot(slot)==null) {
								inventory.setInventorySlotContents(slot, new ItemStack(player.getCurrentEquippedItem().getItem(), 
										1, player.getCurrentEquippedItem().getItemDamage()));
								player.inventory.decrStackSize(player.inventory.currentItem, 1);
							}
						}
					}
					return true;
				}
			}
		}
		return false;
	}
}
