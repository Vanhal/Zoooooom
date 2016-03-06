package tv.vanhal.zoooooom.controllers;

import java.util.ArrayList;

import cofh.api.energy.IEnergyProvider;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import tv.vanhal.zoooooom.Zoooooom;
import tv.vanhal.zoooooom.util.InventoryHelper;
import tv.vanhal.zoooooom.util.Point3I;

public class ItemController extends BaseController {

	@Override
	public boolean canExtract(World world, Point3I source) {
		for (EnumFacing face: getExtractFaces(world, source)) {
			if (InventoryHelper.doesInventoryHaveItems(source.offset(face).getTileEntity(world), face.getOpposite()))
				return true;
		}
		return false;
	}

	@Override
	public boolean canInsert(World world, Point3I target) {
		return false;
	}

	@Override
	public void process(World world, Point3I source, ArrayList<Point3I> network) {
		if (canExtract(world, source)) {
			boolean hasTarget = false;
			ItemStack itemToTest = extract(world, source, getType(world, source).xferRate, true);
			if (itemToTest != null) {
				for (Point3I point : network) {
					if (canInsert(world, point, itemToTest)) {
						hasTarget = true;
						break;
					}
				}
				if (hasTarget) {
					for (EnumFacing face: getExtractFaces(world, source)) {
						ItemStack itemToMove = extract(world, source, getType(world, source).xferRate, true);
						if (itemToMove!=null) {
							for (Point3I target : network) {
								for (EnumFacing facing: getBlockFaces(world, target)) {
									ItemStack remainder = InventoryHelper.insert(target.offset(facing).getTileEntity(world), 
											itemToMove.copy(), facing.getOpposite(), false);

									if (remainder==null) {
										InventoryHelper.extract((IInventory)source.offset(face).getTileEntity(world),
												itemToMove, face.getOpposite(), false, false);
										itemToMove = null;
									} else {
										itemToMove.stackSize -= remainder.stackSize;
										InventoryHelper.extract((IInventory)source.offset(face).getTileEntity(world), 
												itemToMove, face.getOpposite(), false, false);
										itemToMove.stackSize = remainder.stackSize;
									}
									if (itemToMove==null) break;
								}
								if (itemToMove==null) break;
							}
						}
					}
				}
			}
		}
	}
	
	public boolean canInsert(World world, Point3I target, ItemStack itemStack) {
		for (EnumFacing face: getBlockFaces(world, target)) {
			if (InventoryHelper.isInventory(target.offset(face).getTileEntity(world), face)) {
				int startAmt = itemStack.stackSize;
				ItemStack remainder = InventoryHelper.insert(
						target.offset(face).getTileEntity(world), 
						itemStack, face.getOpposite(), true);
				if (remainder == null) return true;
				if (startAmt != itemStack.stackSize) return true;
			}
		}
		return false;
	}

	protected ItemStack extract(World world, Point3I source, int amt, boolean sim) {
		for (EnumFacing face: getExtractFaces(world, source)) {
			if (InventoryHelper.doesInventoryHaveItems(source.offset(face).getTileEntity(world), face.getOpposite())) {
				return InventoryHelper.extractAmount(source.offset(face).getTileEntity(world), face.getOpposite(), amt, sim);
			}
		}
		return null;
	}
}
