package moddedmite.mcpatcher.mixin.mcpatcherforge.cit.client.renderer.entity;

import net.minecraft.ModelBase;
import net.minecraft.RenderBiped;
import net.minecraft.RenderLiving;
import net.minecraft.EntityLiving;
import net.minecraft.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.prupe.mcpatcher.cit.CITUtils;

@Mixin(RenderBiped.class)
public abstract class MixinRenderBiped extends RenderLiving {

    public MixinRenderBiped(ModelBase modelBase, float shadowSize) {
        super(modelBase, shadowSize);
    }

    @Redirect(
        method = "func_130013_c",
        at = @At("TAIL"))
    private void modifyShouldRenderPass(RenderBiped instance, ResourceLocation resourceLocation,
                                        EntityLiving entityLiving, int slotId, float p_77032_3_) {
        this.bindTexture(
            CITUtils.getArmorTexture(resourceLocation, entityLiving, entityLiving.func_130225_q(3 - slotId)));
    }

//    @Redirect(
//        method = "func_82408_c(Lnet/minecraft/EntityLiving;IF)V",
//        at = @At(
//            value = "INVOKE",
//            target = "Lnet/minecraft/RenderBiped;bindTexture(Lnet/minecraft/ResourceLocation;)V"))
//
//    private void modifyFunc_82408_c(RenderBiped instance, ResourceLocation resourceLocation, EntityLiving entityLiving,
//        int slotId, float p_82408_3_) {
//        this.bindTexture(
//            CITUtils.getArmorTexture(resourceLocation, entityLiving, entityLiving.func_130225_q(3 - slotId)));
//    }
}
