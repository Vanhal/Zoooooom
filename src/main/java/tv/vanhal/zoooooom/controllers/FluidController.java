package tv.vanhal.zoooooom.controllers;

import java.util.ArrayList;

import cofh.api.energy.IEnergyProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import tv.vanhal.zoooooom.util.InventoryHelper;
import tv.vanhal.zoooooom.util.Point3I;

public class FluidController extends BaseController {

	@Override
	public boolean canExtract(World world, Point3I source) {
		for (EnumFacing face: getExtractFaces(world, source)) {
			if (extractFluid(world, source.offset(face), face.getOpposite(), getType(world, source).xferRate, false)!=null) 
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
			for (EnumFacing face: getExtractFaces(world, source)) {
				FluidStack fluid = extractFluid(world, source.offset(face), face.getOpposite(), getType(world, source).xferRate, false);
				if (fluid!=null) {
					for (Point3I target : network) {
						for (EnumFacing facing: getBlockFaces(world, target)) {
							if (target.offset(facing).getTileEntity(world) instanceof IFluidHandler) {
								IFluidHandler tank = (IFluidHandler) target.offset(facing).getTileEntity(world);
								int inserted = tank.fill(facing.getOpposite(), fluid.copy(), true);
								if (inserted == fluid.amount) {
									//all of it
									fluid = null;
								} else {
									//some if it
									fluid.amount -= inserted;
								}
								if (inserted>0) 
									extractFluid(world, source.offset(face), face.getOpposite(), inserted, true);
								if (fluid==null) break;
							}
						}
						if (fluid==null) break;
					}
				}
			}
		}
	}
	
	//returns fluidstack that was extracted
	protected FluidStack extractFluid(World world, Point3I point, EnumFacing face, int amount, boolean dofill) {
		if (point.getTileEntity(world) instanceof IFluidHandler) {
			IFluidHandler tankTile = (IFluidHandler) point.getTileEntity(world);
			return tankTile.drain(face, amount, dofill);
		}
		return null;
	}
	
	public boolean canInsert(World world, Point3I target, FluidStack fluidStack) {
		for (EnumFacing face: getBlockFaces(world, target)) {
			if (target.offset(face).getTileEntity(world) instanceof IFluidHandler) {
				IFluidHandler tank = (IFluidHandler) target.offset(face).getTileEntity(world);
				if (tank.canFill(face.getOpposite(), fluidStack.getFluid())) {
					if (tank.fill(face.getOpposite(), fluidStack, false)>0) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
