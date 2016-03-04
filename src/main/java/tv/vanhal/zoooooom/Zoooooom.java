package tv.vanhal.zoooooom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tv.vanhal.zoooooom.blocks.ZBlocks;
import tv.vanhal.zoooooom.core.Proxy;
import tv.vanhal.zoooooom.items.ZItems;
import tv.vanhal.zoooooom.util.Ref;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = Ref.MODID, version = Ref.Version)
public class Zoooooom {
	@Instance(Ref.MODID)
	public static Zoooooom instance;
	
	//logger
	public static final Logger logger = LogManager.getLogger(Ref.MODID);
	
	@SidedProxy(clientSide = "tv.vanhal."+Ref.MODID+".core.ClientProxy", serverSide = "tv.vanhal."+Ref.MODID+".core.Proxy")
	public static Proxy proxy;
	
	//Creative Tab
	public static CreativeTabs ZTab = new CreativeTabs("ZoooooomTab") {
		@Override
		public Item getTabIconItem() {
			return Items.redstone;
		}
	};

	public Zoooooom() {
		logger.info("Zooooooming Stuff");
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
		
		ZBlocks.preInit();
		ZItems.preInit();
	}

    @EventHandler
    public void init(FMLInitializationEvent event) {
		ZBlocks.init();
		ZItems.init();
		
    	proxy.init();
    }
    
    @EventHandler
	public void postInit(FMLPostInitializationEvent event) {
    	ZBlocks.postInit();
		ZItems.postInit();
		
		proxy.registerEntities();
		proxy.registerItems();
		
    	proxy.postInit();
    }
    
    
}
