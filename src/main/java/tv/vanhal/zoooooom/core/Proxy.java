package tv.vanhal.zoooooom.core;

import net.minecraftforge.fml.common.registry.GameRegistry;
import tv.vanhal.zoooooom.tiles.TilePipeExtract;
import tv.vanhal.zoooooom.tiles.TilePipeState;
import tv.vanhal.zoooooom.util.Ref;

public class Proxy {
	
	public void registerItems() {
		
	}
	
	public void registerEntities() {
		GameRegistry.registerTileEntity(TilePipeState.class, Ref.MODID+":TilePipeState");
		GameRegistry.registerTileEntity(TilePipeExtract.class, Ref.MODID+":TilePipeExtract");
	}
	
	public int registerGui(String guiName, String containerName) {
		return -1;
	}
	
	public boolean isClient() {
		return false;
	}
	
	public boolean isServer() {
		return true;
	}
	
	public void preInit() {
		
	}
	
	public void init() {
		
	}
	
	public void postInit() {
		
	}
}
