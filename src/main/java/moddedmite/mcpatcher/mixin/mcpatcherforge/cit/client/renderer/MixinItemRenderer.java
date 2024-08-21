package moddedmite.mcpatcher.mixin.mcpatcherforge.cit.client.renderer;

import net.minecraft.ItemRenderer;
import net.minecraft.EntityLivingBase;
import net.minecraft.ItemStack;
import net.minecraft.Icon;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.prupe.mcpatcher.cit.CITUtils;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

    @Redirect(
        method = "renderItem",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/EntityLivingBase;getItemIcon(Lnet/minecraft/ItemStack;I)Lnet/minecraft/Icon;"))
    private Icon modifyRenderItem1(EntityLivingBase instance, ItemStack item, int renderPass, EntityLivingBase entity,
        ItemStack item2, int renderPass1) {
        return CITUtils.getIcon(entity.getItemIcon(item2, renderPass1), item2, renderPass1);
    }

//    @Redirect(
//        method = "renderItem",
//        at = @At(value = "INVOKE", target = "Lnet/minecraft/ItemStack;hasEffect(I)Z"),
//        remap = false)
//    private boolean modifyRenderItem3(ItemStack item, int pass, EntityLivingBase entity, ItemStack itemStack,
//        int renderPass) {
//        return !CITUtils.renderEnchantmentHeld(item, renderPass) && item.hasEffect() && (boolean) Settings.MODE_GLINT_WORLD.option.getStore();
//    }
}
