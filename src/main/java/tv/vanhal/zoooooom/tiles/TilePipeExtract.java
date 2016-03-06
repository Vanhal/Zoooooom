package tv.vanhal.zoooooom.tiles;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import tv.vanhal.zoooooom.Zoooooom;
import tv.vanhal.zoooooom.controllers.BaseController;
import tv.vanhal.zoooooom.controllers.FluidController;
import tv.vanhal.zoooooom.controllers.ItemController;
import tv.vanhal.zoooooom.controllers.RFController;
import tv.vanhal.zoooooom.enums.EnumConnection;
import tv.vanhal.zoooooom.enums.EnumPipe;
import tv.vanhal.zoooooom.enums.EnumType;
import tv.vanhal.zoooooom.util.PipeState;
import tv.vanhal.zoooooom.util.Point3I;

public class TilePipeExtract extends TilePipeState implements ITickable {
	protected ArrayList<Point3I> network = new ArrayList<Point3I>();
	protected BaseController controller;
	protected boolean updateNetwork = true;

	public TilePipeExtract() {
		this(EnumPipe.None);
	}
	
	public TilePipeExtract(EnumPipe type) {
		super(type);
		filthy = true;
	}
	
	protected void createCrontroller() {
		if (pipeType.getType() == EnumType.Power) controller = new RFController();
		if (pipeType.getType() == EnumType.Item) controller = new ItemController();
		if (pipeType.getType() == EnumType.Fluid) controller = new FluidController();
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
	}

	@Override
	public void update() {
		rebuildNetwork();
		if ( (state.canExtract()) && (network.size()>0) ) {
			if (controller != null) {
				Point3I point = new Point3I(pos);
				if (controller.canExtract(worldObj, point)) {
					controller.process(worldObj, point, network);
				}
			} else {
				createCrontroller();
			}
		}
	}
	
	protected void rebuildNetwork() {
		if ((state.canExtract()) && (updateNetwork)) {
			cleanFilth();
			updateNetwork = false;
			//map the network
			network.clear();
			addPoint(pos);
			while (addPoints()) {}
		}
	}
	
	protected boolean addPoints() {
		ArrayList<Point3I> toAdd = new ArrayList<Point3I>();
		for (Point3I point: network) {
			if (point.getTileEntity(worldObj) instanceof TilePipeState) {
				PipeState state = ((TilePipeState)point.getTileEntity(worldObj)).getState();
				for (EnumFacing face : state.getConnections()) {
					if ( (!network.contains(point.offset(face))) && (!toAdd.contains(point.offset(face))) ) {
						toAdd.add(point.offset(face));
					}
				}
			}
			
		}
		if (toAdd.size()>0) {
			for (Point3I point: toAdd) {
				network.add(point);
			}
			return true;
		}
		return false;
	}
	
	protected void addPoint(BlockPos pos) {
		network.add(new Point3I(pos));
	}
	
	public void markFilthy() {
		super.markFilthy();
		updateNetwork = true;
	}
}
