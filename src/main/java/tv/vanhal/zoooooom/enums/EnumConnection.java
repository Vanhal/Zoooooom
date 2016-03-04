package tv.vanhal.zoooooom.enums;

import net.minecraft.util.IStringSerializable;

public enum EnumConnection implements IStringSerializable {
	none,
	connected,
	extract;

	@Override
	public String getName() {
		return this.toString();
	}
	
	public static EnumConnection get(int i) {
		return values()[i];
	}
}
