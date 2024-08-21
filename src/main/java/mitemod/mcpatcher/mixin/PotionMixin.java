package mitemod.mcpatcher.mixin;

import mitemod.mcpatcher.api.IPotion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Potion.class)
public class PotionMixin implements IPotion {
    @Unique
    public int liquidColor;

    @Unique
    @Environment(EnvType.CLIENT)
    public int origColor;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void addLiquidColor(int par1, boolean par2, int par3, CallbackInfo ci) {
        this.liquidColor = par3;
    }

    @Override
    public int getLiquidColor() {
        return this.liquidColor;
    }
}
