package tv.vanhal.zoooooom.core;

public class ClientProxy extends Proxy {
	@Override
	public boolean isClient() {
		return true;
	}
	
	@Override
	public boolean isServer() {
		return false;
	}
}
