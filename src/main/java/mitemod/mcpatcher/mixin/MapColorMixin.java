package mitemod.mcpatcher.mixin;

import mitemod.mcpatcher.api.IMapColor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.MapColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MapColor.class)
public class MapColorMixin implements IMapColor {
    @Unique
    @Environment(EnvType.CLIENT)
    public int origColorValue;
}
