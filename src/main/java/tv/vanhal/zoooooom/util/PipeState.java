package tv.vanhal.zoooooom.util;

import java.util.ArrayList;
import java.util.List;

import cofh.api.energy.IEnergyHandler;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.IFluidHandler;
import tv.vanhal.zoooooom.blocks.BasePipe;
import tv.vanhal.zoooooom.enums.EnumConnection;
import tv.vanhal.zoooooom.enums.EnumPipe;
import tv.vanhal.zoooooom.enums.EnumType;

public class PipeState {
	protected EnumPipe pipeType = EnumPipe.None;
	
	protected EnumConnection up = EnumConnection.none;
	protected EnumConnection down = EnumConnection.none;
	protected EnumConnection north = EnumConnection.none;
	protected EnumConnection south = EnumConnection.none;
	protected EnumConnection east = EnumConnection.none;
	protected EnumConnection west = EnumConnection.none;
	
	protected int[] forcedSides = new int[]{0,0,0,0,0,0};
	
	public PipeState() {
	}
	
	public PipeState(EnumPipe _type) {
		pipeType = _type;
	}
	
	public boolean update(IBlockAccess world, BlockPos pos) {
		boolean updated = false;
		for (EnumFacing face: EnumFacing.values()) {
			if (forcedSides[face.ordinal()] == 0) {
				if ( (getState(face) == EnumConnection.none) && (canConnectTo(world, pos.offset(face))) ) {
					setState(face, EnumConnection.connected);
					updated = true;
				} else if ( (getState(face) != EnumConnection.none) && (!canConnectTo(world, pos.offset(face))) ) {
					setState(face, EnumConnection.none);
					updated = true;
				}
			} else if ( (getState(face) != EnumConnection.none) && (!canConnectTo(world, pos.offset(face))) ) {
				setState(face, EnumConnection.none);
				updated = true;
				forcedSides[face.ordinal()] = 0;
			}
		}
		return updated;
	}
	
	public boolean canConnectTo(IBlockAccess world, BlockPos pos) {
		if (canConnectPipe(world, pos)) return true;
		return canConnectBlock(world, pos);
	}
	
	public boolean canConnectPipe(IBlockAccess world, BlockPos pos) {
		Block testBlock = world.getBlockState(pos).getBlock();
		return ( (testBlock instanceof BasePipe) && (((BasePipe)testBlock).type == this.pipeType) );
	}
	
	public boolean canConnectBlock(IBlockAccess world, BlockPos pos) {
		Block testBlock = world.getBlockState(pos).getBlock();
		TileEntity tile = world.getTileEntity(pos);
		if ( (pipeType.getType() == EnumType.Power) && (tile instanceof IEnergyHandler) ) return true;
		if ( (pipeType.getType() == EnumType.Item) && (tile instanceof IInventory) ) return true;
		if ( (pipeType.getType() == EnumType.Fluid) && (tile instanceof IFluidHandler) ) return true;
		return false;

	}
	
	public EnumFacing[] getConnections() {
		List<EnumFacing> faces = new ArrayList<EnumFacing>();
		for (EnumFacing face: EnumFacing.values()) {
			if (isConnected(face)) faces.add(face);
		}
		EnumFacing[] rtn = new EnumFacing[faces.size()];
		return faces.toArray(rtn);
	}
	
	public EnumFacing[] getBlockConnections(IBlockAccess world, Point3I pos) {
		List<EnumFacing> faces = new ArrayList<EnumFacing>();
		for (EnumFacing face: EnumFacing.values()) {
			if ( (canConnectBlock(world, pos.offset(face).getPos())) 
					&& (getState(face) == EnumConnection.connected) ) faces.add(face);
		}
		EnumFacing[] rtn = new EnumFacing[faces.size()];
		return faces.toArray(rtn);
	}
	
	public EnumFacing[] getExtractConnections() {
		List<EnumFacing> faces = new ArrayList<EnumFacing>();
		for (EnumFacing face: EnumFacing.values()) {
			if (getState(face) == EnumConnection.extract) faces.add(face);
		}
		EnumFacing[] rtn = new EnumFacing[faces.size()];
		return faces.toArray(rtn);
	}
	
	public boolean isConnected(EnumFacing face) {
		return (getState(face) != EnumConnection.none);
	}
	
	public void setForcedState(EnumFacing face, EnumConnection type) {
		setState(face, type);
		forcedSides[face.ordinal()] = 1;
	}
	
	public void setState(EnumFacing face, EnumConnection type) {
		if (face == EnumFacing.UP) up = type;
		else if (face == EnumFacing.DOWN) down = type;
		else if (face == EnumFacing.NORTH) north = type;
		else if (face == EnumFacing.SOUTH) south = type;
		else if (face == EnumFacing.EAST) east = type;
		else if (face == EnumFacing.WEST) west = type;
	}
	
	public EnumConnection getState(EnumFacing face) {
		if (face == EnumFacing.UP) return up;
		else if (face == EnumFacing.DOWN) return down;
		else if (face == EnumFacing.NORTH) return north;
		else if (face == EnumFacing.SOUTH) return south;
		else if (face == EnumFacing.EAST) return east;
		else if (face == EnumFacing.WEST) return west;
		return EnumConnection.none;
	}
	
	public boolean canExtract() {
		for (EnumFacing face: EnumFacing.values()) {
			if (getState(face)==EnumConnection.extract) return true;
		}
		return false;
	}
	
	public NBTTagCompound getNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("up", up.ordinal());
		tag.setInteger("down", down.ordinal());
		tag.setInteger("north", north.ordinal());
		tag.setInteger("south", south.ordinal());
		tag.setInteger("east", east.ordinal());
		tag.setInteger("west", west.ordinal());
		tag.setInteger("type", pipeType.ordinal());
		tag.setIntArray("forced", forcedSides);
		return tag;
	}
	
	public void setNBT(NBTTagCompound nbt) {
		up = EnumConnection.get(nbt.getInteger("up"));
		down = EnumConnection.get(nbt.getInteger("down"));
		north = EnumConnection.get(nbt.getInteger("north"));
		south = EnumConnection.get(nbt.getInteger("south"));
		east = EnumConnection.get(nbt.getInteger("east"));
		west = EnumConnection.get(nbt.getInteger("west"));
		pipeType = EnumPipe.get(nbt.getInteger("type"));
		if (nbt.hasKey("forced")) forcedSides = nbt.getIntArray("forced");
	}
}
