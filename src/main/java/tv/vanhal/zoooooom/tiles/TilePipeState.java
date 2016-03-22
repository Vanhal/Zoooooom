package tv.vanhal.zoooooom.tiles;

import org.apache.logging.log4j.message.Message;

import tv.vanhal.zoooooom.Zoooooom;
import tv.vanhal.zoooooom.enums.EnumConnection;
import tv.vanhal.zoooooom.enums.EnumPipe;
import tv.vanhal.zoooooom.util.PipeState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class TilePipeState extends TileEntity {
	protected EnumPipe pipeType = EnumPipe.None;
	protected PipeState state;
	protected boolean filthy = true;

	public TilePipeState() {
		this(EnumPipe.None);
	}
	
	public TilePipeState(EnumPipe type) {
		pipeType = type;
		state = new PipeState(pipeType);
		markDirty();
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("type", pipeType.ordinal());
		nbt.setTag("state", state.getNBT());
		if (!worldObj.isRemote) worldObj.markBlockForUpdate(pos);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if (nbt.hasKey("type")) pipeType = EnumPipe.get(nbt.getInteger("type"));
		if (nbt.hasKey("state")) state.setNBT(nbt.getCompoundTag("state"));
	}
	
	@Override
	public Packet getDescriptionPacket() {
		return new S35PacketUpdateTileEntity(pos, 0, state.getNBT());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		state.setNBT(pkt.getNbtCompound());
		markDirty();
		worldObj.markBlockRangeForRenderUpdate(pos, pos);
	}
	
	public void sendUpdateStateMessage() {
		if (!worldObj.isRemote) {
			worldObj.markBlockForUpdate(pos);
			markDirty();
			markFilthy();
		}
	}
	
	public void cleanFilth() {
		if (!worldObj.isRemote) {
			if (!filthy) return;
			filthy = false;
			markConnectedFilthy(false);
		}
	}
	
	public void markFilthy() {
		if (!worldObj.isRemote) {
			if (filthy) return;
			filthy = true;
			markConnectedFilthy(true);
		}
	}
	
	protected void markConnectedFilthy(boolean filth) {
		if (!worldObj.isRemote) {
			for (EnumFacing face: state.getConnections()) {
				if (worldObj.getTileEntity(pos.offset(face)) instanceof TilePipeState) {
					if (filth)
						((TilePipeState)worldObj.getTileEntity(pos.offset(face))).markFilthy();
					else
						((TilePipeState)worldObj.getTileEntity(pos.offset(face))).cleanFilth();
				}
			}
		}
	}
	
	protected void updateTile() {
		if (this instanceof TilePipeExtract) {
			/*if (!state.canExtract()) {
				TilePipeState newState = new TilePipeState(pipeType);
				newState.setState(state);
				worldObj.setTileEntity(pos, newState);
				worldObj.markBlockForUpdate(pos);
			}*/
		} else if (state.canExtract()) {
			TilePipeExtract newState = new TilePipeExtract(pipeType);
			newState.setState(state);
			worldObj.setTileEntity(pos, newState);
		}
	}
	
	public PipeState getState() {
		return state;
	}
	
	public void setState(PipeState _state) {
		state.setNBT(_state.getNBT());
	}
	
	public void updateState() {
		if (state!=null) {
			if (state.update(worldObj, pos)) {
				updateTile();
				sendUpdateStateMessage();
			}
		}
	}
	
	public void updateConnection(EnumFacing face, EnumConnection connect) {
		if (state!=null) {
			if (state.getState(face) != connect) {
				state.setForcedState(face, connect);
				//send the update
				sendUpdateStateMessage();
				//if it's an extract then change this to a extract tile
			}
		}
	}
	
	public EnumPipe getType() {
		return pipeType;
	}
	
	public boolean changeConnection(EnumFacing face, float hitX, float hitY, float hitZ) {
		int realHitX = Math.round((hitX * ((face.getFrontOffsetX()==0)?1:0))*100);
		int realHitY = Math.round((hitY * ((face.getFrontOffsetY()==0)?1:0))*100);
		int realHitZ = Math.round((hitZ * ((face.getFrontOffsetZ()==0)?1:0))*100);

		if (realHitY>=80) {
			if (changeConnection(EnumFacing.UP)) return true;
		} else if ( (realHitY<=20) && (realHitY>0) ) {
			if (changeConnection(EnumFacing.DOWN)) return true;
		} else if (realHitZ>=80) {
			if (changeConnection(EnumFacing.SOUTH)) return true;
		} else if ( (realHitZ<=20) && (realHitZ>0) ) {
			if (changeConnection(EnumFacing.NORTH)) return true;
		} else if (realHitX>=80) {
			if (changeConnection(EnumFacing.EAST)) return true;
		} else if ( (realHitX<=20) && (realHitX>0) ) {
			if (changeConnection(EnumFacing.WEST)) return true;
		}

		if (!state.isConnected(face)) {
			if (state.canConnectTo(worldObj, pos.offset(face))) {
				updateConnection(face, EnumConnection.connected);
				return true;
			}
		}
		return false;
	}
	
	protected boolean changeConnection(EnumFacing face) {
		if (state.isConnected(face)) {
			if ((state.canConnectBlock(worldObj, pos.offset(face)))) {
				if (!worldObj.isRemote) {
					toggleConenction(face);
					updateTile();
				}
				return true;
			}
		}
		return false;
	}
	
	public void toggleConenction(EnumFacing face) {
		if (state.getState(face) == EnumConnection.connected) {
			updateConnection(face, EnumConnection.extract);
		} else {
			updateConnection(face, EnumConnection.none);
		}
	}
}
