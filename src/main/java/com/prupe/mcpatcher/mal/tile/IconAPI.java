package com.prupe.mcpatcher.mal.tile;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Icon;
import net.minecraft.TextureAtlasSprite;

@Environment(EnvType.CLIENT)
public class IconAPI {
    public static boolean needRegisterTileAnimations() {
        return false;
    }

    public static int getIconOriginX(TextureAtlasSprite icon) {
        return icon.getOriginX();
    }

    public static int getIconOriginY(TextureAtlasSprite icon) {
        return icon.getOriginY();
    }

    public static int getIconWidth(Icon icon) {
    	try {
            return icon.getIconWidth(); //Math.round(icon.getSheetWidth() * (icon.getMaxU() - icon.getMinU()));
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public static int getIconHeight(Icon icon) {
    	try {
            return icon.getIconHeight(); //Math.round(icon.getSheetHeight() * (icon.getMaxV() - icon.getMinV()));
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public static String getIconName(Icon icon) {
        return icon.getIconName();
    }
}
