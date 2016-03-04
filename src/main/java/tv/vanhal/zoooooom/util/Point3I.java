package tv.vanhal.zoooooom.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/* A Point case to allow for an x, y, z point */
public class Point3I {
	private int x, y, z;
	
	public Point3I() {
		setLocation(0, 0, 0);
	}
	
	public Point3I(NBTTagCompound nbt) {
		setNBT(nbt);
	}
	
	public Point3I(Point3I point) {
		setLocation(point.getX(), point.getY(), point.getZ());
	}
	
	public Point3I(BlockPos pos) {
		setLocation(pos.getX(), pos.getY(), pos.getZ());
	}
	
	public Point3I(int x, int y, int z) {
		setLocation(x, y, z);
	}
	
	public void setLocation(int x1, int y1, int z1) {
		this.x = x1;
		this.y = y1;
		this.z = z1;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setZ(int z) {
		this.z = z;
	}
	
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public BlockPos getPos() {
		return new BlockPos(getX(), getY(), getZ());
	}
	
	public String toString() {
		return ""+x+", "+y+", "+z;
	}
	
	public Point3I offset(int _x, int _y, int _z) {
		Point3I result = new Point3I();
		result.setX(x + _x);
		result.setY(y + _y);
		result.setZ(z + _z);
		return result;
	}
	
	
	public Point3I offset(EnumFacing face) {
		return this.offset(face, 1);
	}
	
	public Point3I offset(EnumFacing face, int times) {
		return this.offset(face.getFrontOffsetX()*times, face.getFrontOffsetY()*times, face.getFrontOffsetZ()*times);
	}
	
	@Override
	public boolean equals(Object test) {
		if (test instanceof Point3I) {
			Point3I testPoint = (Point3I) test;
			return ( (x == testPoint.getX()) && (y == testPoint.getY()) && (z == testPoint.getZ()) );
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		String hash = x+"|"+y+"|"+z;
		return hash.hashCode();
	}
	
	public Point3I getAdjacentPoint(EnumFacing face) {
		return offset(face);
	}
	
	public Point3I getAdjacentPoint(int side) {
		EnumFacing dir = EnumFacing.getFront(side);
		return getAdjacentPoint(dir);
	}
	
	public boolean blockExists(World world) {
		return world.isBlockLoaded(getPos());
	}
	
	public Block getBlock(IBlockAccess world) {
		return world.getBlockState(getPos()).getBlock();
	}
	
	public TileEntity getTileEntity(IBlockAccess world) {
		return world.getTileEntity(getPos());
	}
	
	public int getMetaData(IBlockAccess world) {
		return getBlock(world).getMetaFromState(getState(world));
	}
	
	public IBlockState getState(IBlockAccess world) {
		return world.getBlockState(getPos());
	}
	
	public NBTTagCompound getNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("x", getX());
		tag.setInteger("y", getY());
		tag.setInteger("z", getZ());
		return tag;
	}
	
	public void setNBT(NBTTagCompound nbt) {
		setX(nbt.getInteger("x"));
		setY(nbt.getInteger("y"));
		setZ(nbt.getInteger("z"));
	}
}
