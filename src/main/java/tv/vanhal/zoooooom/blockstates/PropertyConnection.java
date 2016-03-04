package tv.vanhal.zoooooom.blockstates;

import java.util.Collection;

import com.google.common.collect.Lists;

import tv.vanhal.zoooooom.enums.EnumConnection;
import net.minecraft.block.properties.PropertyEnum;

public class PropertyConnection extends PropertyEnum<EnumConnection> {

	protected PropertyConnection(String name, Collection<EnumConnection> allowedValues) {
		super(name, EnumConnection.class, allowedValues);
	}

	public static PropertyConnection create(String name) {
		return new PropertyConnection(name, Lists.newArrayList(EnumConnection.values()));
	}
}
