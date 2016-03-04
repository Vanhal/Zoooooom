package tv.vanhal.zoooooom.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import tv.vanhal.zoooooom.Zoooooom;
import tv.vanhal.zoooooom.enums.EnumConnection;
import tv.vanhal.zoooooom.enums.EnumType;

public class TilePipeExtract extends TilePipeState implements ITickable {

	public TilePipeExtract() {
		this(EnumType.None);
	}
	
	public TilePipeExtract(EnumType type) {
		super(type);
		filthy = true;
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
		
	}
	
	protected void rebuildNetwork() {
		if (this.filthy) {
			cleanFilth();
			filthy = false;
			//map the network
			Zoooooom.logger.info("Remapping Network for extracter "+pos.toString());
		}
	}
	
	@Override
	public void cleanFilth() {
		if (!filthy) return;
		markConnectedFilthy(false);
	}
}
