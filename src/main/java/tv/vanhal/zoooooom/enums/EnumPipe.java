package tv.vanhal.zoooooom.enums;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public enum EnumPipe {
	None("None", EnumType.None, 0, null),
	WoodenRF("woodenRF", EnumType.Power, 5, Item.getItemFromBlock(Blocks.planks)),
	WoodenItem("woodenItem", EnumType.Item, 1, Item.getItemFromBlock(Blocks.planks)),
	WoodenFluid("woodenFluid", EnumType.Fluid, 5, Item.getItemFromBlock(Blocks.planks)),
	StoneRF("stoneRF", EnumType.Power, 100, Item.getItemFromBlock(Blocks.stone)),
	StoneItem("stoneItem", EnumType.Item, 8, Item.getItemFromBlock(Blocks.stone)),
	StoneFluid("stoneFluid", EnumType.Fluid, 25, Item.getItemFromBlock(Blocks.stone)),
	IronRF("ironRF", EnumType.Power, 500, Items.iron_ingot),
	IronItem("ironItem", EnumType.Item, 32, Items.iron_ingot),
	IronFluid("ironFluid", EnumType.Fluid, 100, Items.iron_ingot),
	GoldRF("goldRF", EnumType.Power, 2000, Items.gold_ingot),
	GoldItem("goldItem", EnumType.Item, 64, Items.gold_ingot),
	GoldFluid("goldFluid", EnumType.Fluid, 500, Items.gold_ingot);
	
	public static EnumPipe get(int i) {
		return values()[i];
	}
	
	public String name;
	public int xferRate;
	public EnumType type;
	public Item craftItem;
	
	private EnumPipe(String _name, EnumType _type, int _xferRate, Item item) {
		name = _name;
		type = _type;
		xferRate = _xferRate;
		craftItem = item;
	}
	
	public EnumType getType() {
		return type;
	}
}
