package tv.vanhal.zoooooom.controllers;

import java.util.ArrayList;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import tv.vanhal.zoooooom.enums.EnumPipe;
import tv.vanhal.zoooooom.tiles.TilePipeState;
import tv.vanhal.zoooooom.util.PipeState;
import tv.vanhal.zoooooom.util.Point3I;

public abstract class BaseController {
	
	protected PipeState getState(World world, Point3I point) {
		if (point.getTileEntity(world) instanceof TilePipeState) {
			return ((TilePipeState)point.getTileEntity(world)).getState();
		}
		return new PipeState();
	}
	
	protected EnumPipe getType(World world, Point3I point) {
		if (point.getTileEntity(world) instanceof TilePipeState) {
			return ((TilePipeState)point.getTileEntity(world)).getType();
		}
		return EnumPipe.None;
	}
	
	protected EnumFacing[] getExtractFaces(World world, Point3I point) {
		return getState(world, point).getExtractConnections();
	}
	
	protected EnumFacing[] getBlockFaces(World world, Point3I point) {
		return getState(world, point).getBlockConnections(world, point);
	}

	public abstract boolean canExtract(World world, Point3I source);
	public abstract boolean canInsert(World world, Point3I target);
	public abstract void process(World world, Point3I source, ArrayList<Point3I> network);
}
