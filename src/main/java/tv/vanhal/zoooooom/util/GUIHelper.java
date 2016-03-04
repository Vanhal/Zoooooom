package tv.vanhal.zoooooom.util;

import net.minecraft.client.gui.FontRenderer;

public class GUIHelper {
	public static void DrawStringCentered(FontRenderer fontRenderer, String str, int x, int y, int colour) {
		int strWidth = Math.round(fontRenderer.getStringWidth(str) / 2.0f);
		fontRenderer.drawString(str, x - strWidth, y, colour);
	}
	
	public static void DrawShadowStringCentered(FontRenderer fontRenderer, String str, int x, int y, int colour) {
		int strWidth = Math.round(fontRenderer.getStringWidth(str) / 2.0f);
		fontRenderer.drawStringWithShadow(str, x - strWidth, y, colour);
	}
}
