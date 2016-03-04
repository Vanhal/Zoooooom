package tv.vanhal.zoooooom.blocks;

import tv.vanhal.zoooooom.BasePipe;
import tv.vanhal.zoooooom.enums.EnumType;

public class ZBlocks {
	public static BasePipe rfPipe = new BasePipe("rfPipe", EnumType.Power);
	public static BasePipe itemPipe = new BasePipe("itemPipe", EnumType.Item);
	public static BasePipe fluidPipe = new BasePipe("fluidPipe", EnumType.Fluid);
	
	public static void preInit() {
		rfPipe.preInit();
		fluidPipe.preInit();
		itemPipe.preInit();
	}
	
	public static void init() {
		rfPipe.init();
		fluidPipe.init();
		itemPipe.init();
	}

	public static void postInit() {
		rfPipe.postInit();
		fluidPipe.postInit();
		itemPipe.postInit();
	}
}
