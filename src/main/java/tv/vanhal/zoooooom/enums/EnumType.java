package tv.vanhal.zoooooom.enums;

public enum EnumType {
	None,
	Power,
	Item,
	Fluid;
	
	public static EnumType get(int i) {
		return values()[i];
	}
}
