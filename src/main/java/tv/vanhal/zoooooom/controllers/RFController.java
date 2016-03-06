package tv.vanhal.zoooooom.controllers;

import java.util.ArrayList;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import tv.vanhal.zoooooom.Zoooooom;
import tv.vanhal.zoooooom.util.Point3I;

public class RFController extends BaseController {

	@Override
	public boolean canExtract(World world, Point3I source) {
		for (EnumFacing face: getExtractFaces(world, source)) {
			if (extractEnergy(world, source.offset(face), face.getOpposite(), getType(world, source).xferRate, true)>0) 
				return true;
		}
		return false;
	}

	@Override
	public void process(World world, Point3I source, ArrayList<Point3I> network) {
		if (canExtract(world, source)) {
			boolean hasTarget = false;
			for (Point3I point : network) {
				if (canInsert(world, point)) {
					hasTarget = true;
					break;
				}
			}
			if (hasTarget) {
				for (EnumFacing face: getExtractFaces(world, source)) {
					int power = extractEnergy(world, source.offset(face), face.getOpposite(), getType(world, source).xferRate, true);
					if (power>0) {
						for (Point3I target : network) {
							for (EnumFacing facing: getBlockFaces(world, target)) {
								if (insertEnergy(world, target.offset(facing), facing.getOpposite(), power, true)>0) {
									power -= insertEnergy(world, target.offset(facing), facing.getOpposite(), 
											extractEnergy(world, source.offset(face), face.getOpposite(), power, false), false);
								}
							}
							if (power<=0) break;
						}
					}
				}
			}
		}
	}

	@Override
	public boolean canInsert(World world, Point3I target) {
		for (EnumFacing face: getBlockFaces(world, target)) {
			if (insertEnergy(world, target.offset(face), face.getOpposite(), 1, true)>0) return true;
		}
		return false;
	}
	
	//returns the amount of energy that was extracted
	protected int extractEnergy(World world, Point3I point, EnumFacing face, int amount, boolean sim) {
		if (point.getTileEntity(world) instanceof IEnergyProvider) {
			IEnergyProvider energyTile = (IEnergyProvider) point.getTileEntity(world);
			if (energyTile.canConnectEnergy(face)) {
				return energyTile.extractEnergy(face, amount, sim);
			}
		}
		return 0;
	}
	
	//returns the amount of energy that was consumed
	protected int insertEnergy(World world, Point3I point, EnumFacing face, int amount, boolean sim) {
		if (point.getTileEntity(world) instanceof IEnergyReceiver) {
			IEnergyReceiver energyTile = (IEnergyReceiver) point.getTileEntity(world);
			if (energyTile.canConnectEnergy(face)) {
				return energyTile.receiveEnergy(face, amount, sim);
			}
		}
		return 0;
	}

	
}
