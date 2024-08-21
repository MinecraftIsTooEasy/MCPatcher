package moddedmite.mcpatcher.mixin.mcpatcherforge.cc.client.renderer;

import net.minecraft.Block;
import net.minecraft.ItemRenderer;
import net.minecraft.EntityLivingBase;
import net.minecraft.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.prupe.mcpatcher.cc.ColorizeBlock;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

    @Inject(
        method = "renderItem",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/ItemRenderer;renderItemIn2D(Lnet/minecraft/Tessellator;FFFFIIF)V",
            ordinal = 0))
    private void modifyRenderItem2(EntityLivingBase par1EntityLivingBase, ItemStack par2ItemStack, int par3, CallbackInfo ci) {
        ColorizeBlock.colorizeWaterBlockGL(par2ItemStack.getItemAsBlock().getBlock());
    }
}
