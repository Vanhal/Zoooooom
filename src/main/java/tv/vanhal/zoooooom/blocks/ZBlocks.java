package tv.vanhal.zoooooom.blocks;

import java.util.ArrayList;

import tv.vanhal.zoooooom.enums.EnumPipe;
import tv.vanhal.zoooooom.enums.EnumType;

public class ZBlocks {
	public static ArrayList<BasePipe> pipes = new ArrayList<BasePipe>();
	
	public static void preInit() {
		for (EnumPipe type : EnumPipe.values()) {
			if (type.getType() != EnumType.None)
				pipes.add(new BasePipe(type.name+"Pipe", type));
		}
		
		for (BasePipe pipe : pipes) {
			pipe.preInit();
		}
	}
	
	public static void init() {
		for (BasePipe pipe : pipes) {
			pipe.init();
		}
	}

	public static void postInit() {
		for (BasePipe pipe : pipes) {
			pipe.postInit();
		}
	}
}
