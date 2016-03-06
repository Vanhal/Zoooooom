package tv.vanhal.zoooooom.items;

import java.util.List;

import tv.vanhal.zoooooom.blocks.BasePipe;
import tv.vanhal.zoooooom.enums.EnumPipe;
import tv.vanhal.zoooooom.enums.EnumType;
import tv.vanhal.zoooooom.util.StringHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPipe extends ItemBlock {
	protected EnumPipe type = EnumPipe.None;

	public ItemPipe(Block block) {
		super(block);
		if (block instanceof BasePipe) {
			type = ((BasePipe)block).type;
		}
	}
	
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
    	String ttip = StringHelper.GRAY+StringHelper.localize("info.usage.max")+" "+type.xferRate+" ";
    	ttip += StringHelper.localize("info.usage."+type.type.toString().toLowerCase());
    	
    	tooltip.add(ttip);
    	tooltip.add(StringHelper.GRAY+" Right click on pipe with empty hand too change mode");
    }

}
