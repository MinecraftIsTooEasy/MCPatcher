package moddedmite.mcpatcher.mixin.mcpatcherforge.cit.client.renderer.entity;

import net.minecraft.AbstractClientPlayer;
import net.minecraft.ModelBase;
import net.minecraft.RenderBiped;
import net.minecraft.RenderPlayer;
import net.minecraft.RendererLivingEntity;
import net.minecraft.Entity;
import net.minecraft.EntityLivingBase;
import net.minecraft.ItemStack;
import net.minecraft.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.prupe.mcpatcher.cit.CITUtils;

@Mixin(RenderPlayer.class)
public abstract class MixinRenderPlayer extends RendererLivingEntity {

    public MixinRenderPlayer(ModelBase modelBase, float shadowSize) {
        super(modelBase, shadowSize);
    }

//    @Redirect(
//        method = "shouldRenderPass(Lnet/minecraft/AbstractClientPlayer;IF)I",
//        at = @At(
//            value = "INVOKE",
//            target = "Lnet/minecraft/RenderBiped;getArmorResource(Lnet/minecraft/Entity;Lnet/minecraft/ItemStack;ILjava/lang/String;)Lnet/minecraft/ResourceLocation;",
//            remap = false))
//    private ResourceLocation modifyShouldRenderPass(Entity entity, ItemStack stack, int slot, String type,
//        AbstractClientPlayer player) {
//        return CITUtils
//            .getArmorTexture(RenderBiped.getArmorResource(player, stack, slot, type), (EntityLivingBase) entity, stack);
//    }
//
//    @Redirect(
//        method = "func_82408_c(Lnet/minecraft/client/entity/AbstractClientPlayer;IF)V",
//        at = @At(
//            value = "INVOKE",
//            target = "Lnet/minecraft/client/renderer/entity/RenderBiped;getArmorResource(Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;ILjava/lang/String;)Lnet/minecraft/util/ResourceLocation;",
//            remap = false))
//    private ResourceLocation modifyFunc_82408_c(Entity entity, ItemStack stack, int slot, String type,
//        AbstractClientPlayer player) {
//        return CITUtils
//            .getArmorTexture(RenderBiped.getArmorResource(player, stack, slot, type), (EntityLivingBase) entity, stack);
//    }
}
