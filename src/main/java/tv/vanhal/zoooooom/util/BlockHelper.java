package tv.vanhal.zoooooom.util;


import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockHelper {
	
	private static int[] rotateOrder = {0, 1, 2, 5, 3, 4};
	private static int[] orderLookup = {0, 1, 2, 4, 5, 3};

    private static final EnumFacing[] FOURWAY_AXES = new EnumFacing[] { 
    	EnumFacing.WEST, 
    	EnumFacing.EAST, 
    	EnumFacing.NORTH, 
    	EnumFacing.SOUTH
    };
    
    private static final EnumFacing[] SIXWAY_AXES = new EnumFacing[] { 
    	EnumFacing.WEST, 
    	EnumFacing.EAST, 
    	EnumFacing.NORTH, 
    	EnumFacing.SOUTH, 
    	EnumFacing.UP, 
    	EnumFacing.DOWN
    };
	
	public static enum Axis {
		None, SixWay, FourWay;
	}
	
	public static EnumFacing[] getValidFacing(Axis input) {
		if (input == Axis.SixWay) return SIXWAY_AXES;
		if (input == Axis.FourWay) return FOURWAY_AXES;
		return new EnumFacing[] {};
	}

	public static int determineOrientation(World world, Axis axis, BlockPos pos, EntityLivingBase entity) {
		if (axis == Axis.None) return 0;
		if (axis == Axis.SixWay) return BlockPistonBase.getFacingFromEntity(world, pos, entity).ordinal();
		else if (axis == Axis.FourWay) {
	        int l = MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
	        return rotateOrder[2+l];
		}
		return 0;
	}
	
	public static int rotateBlock(int current, Axis axis) {
		if (axis == Axis.None) return 0;
		int order = orderLookup[current];
		order++;
		if (order>=6) {
			order = (axis==Axis.FourWay)?2:0;
		}
		return rotateOrder[order];
	}
}
