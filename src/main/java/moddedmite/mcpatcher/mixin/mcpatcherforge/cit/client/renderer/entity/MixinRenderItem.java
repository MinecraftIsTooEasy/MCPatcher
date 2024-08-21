package moddedmite.mcpatcher.mixin.mcpatcherforge.cit.client.renderer.entity;

import net.minecraft.FontRenderer;
import net.minecraft.OpenGlHelper;
import net.minecraft.Render;
import net.minecraft.RenderItem;
import net.minecraft.TextureManager;
import net.minecraft.Item;
import net.minecraft.ItemStack;
import net.minecraft.Icon;
import net.minecraft.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.prupe.mcpatcher.cit.CITUtils;

@Mixin(RenderItem.class)
public abstract class MixinRenderItem extends Render {

    @Final
    @Shadow
    private static ResourceLocation RES_ITEM_GLINT;

    @Shadow
    public float zLevel;

    @Shadow
    protected abstract void renderGlint(int p_77018_1_, int p_77018_2_, int p_77018_3_, int p_77018_4_, int p_77018_5_);

    // TODO: figure out if ForgeHooksClient#renderEntityItem also needs work

//    @Redirect(
//        method = "doRender",
//        at = @At(
//            value = "INVOKE",
//            target = "Lnet/minecraft/Item;getIcon(Lnet/minecraft/ItemStack;I)Lnet/minecraft/Icon;",
//            remap = false))
//    private Icon modifyDoRender(Item item, ItemStack itemStack, int pass) {
//        return CITUtils.getIcon(item.getIconIndex(itemStack), itemStack, pass);
//    }
//
//    @Redirect(
//        method = "renderDroppedItem(Lnet/minecraft/EntityItem;Lnet/minecraft/Icon;IFFFFI)V",
//        at = @At(value = "INVOKE", target = "Lnet/minecraft/ItemStack;hasEffect(I)Z", remap = false),
//        remap = false)
//    private boolean modifyRenderDroppedItem(ItemStack instance, int pass) {
//        return !CITUtils.renderEnchantmentDropped(instance) && instance.hasEffect(pass) && (boolean) Settings.MODE_GLINT_WORLD.option.getStore();
//    }
//
//    @Inject(
//        method = "renderItemIntoGUI(Lnet/minecraft/FontRenderer;Lnet/minecraft/TextureManager;Lnet/minecraft/ItemStack;IIZ)V",
//        at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glColorMask(ZZZZ)V", remap = false, ordinal = 0),
//        remap = false)
//    private void modifyRenderItemIntoGUI1(FontRenderer fontRenderer, TextureManager manager, ItemStack itemStack, int x,
//        int y, boolean renderEffect, CallbackInfo ci) {
//        GL11.glDepthMask(false);
//    }
//
//    @Inject(
//        method = "renderItemIntoGUI(Lnet/minecraft/FontRenderer;Lnet/minecraft/TextureManager;Lnet/minecraft/ItemStack;IIZ)V",
//        at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V", remap = false, ordinal = 4),
//        remap = false)
//    private void modifyRenderItemIntoGUI2(FontRenderer fontRenderer, TextureManager manager, ItemStack itemStack, int x,
//        int y, boolean renderEffect, CallbackInfo ci) {
//        GL11.glDepthMask(true);
//    }
//
//    @Redirect(
//        method = "renderItemIntoGUI(Lnet/minecraft/FontRenderer;Lnet/minecraft/TextureManager;Lnet/minecraft/ItemStack;IIZ)V",
//        at = @At(
//            value = "INVOKE",
//            target = "Lnet/minecraft/Item;getIcon(Lnet/minecraft/ItemStack;I)Lnet/minecraft/Icon;",
//            remap = false),
//        remap = false)
//    private Icon modifyRenderItemIntoGUI3(Item item, ItemStack itemStack, int pass) {
//        return CITUtils.getIcon(item.getIcon(itemStack, pass), itemStack, pass);
//    }
//
//    // if I don't do this the transparency in the inventory breaks, I'm sure there's a much better way of doing it but
//    // my open gl knowledge is pretty much none-existent atm
//
//    @Redirect(
//        method = "renderItemIntoGUI(Lnet/minecraft/FontRenderer;Lnet/minecraft/TextureManager;Lnet/minecraft/ItemStack;IIZ)V",
//        at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V", remap = false, ordinal = 10),
//        remap = false)
//    private void cancelAlpha3(int cap) {
//
//    }
//
//    @Redirect(
//        method = "renderItemIntoGUI(Lnet/minecraft/FontRenderer;Lnet/minecraft/TextureManager;Lnet/minecraft/ItemStack;IIZ)V",
//        at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V", remap = false, ordinal = 8),
//        remap = false)
//    private void cancelAlpha4(int cap) {
//
//    }
//
//    @Inject(
//        method = "renderItemAndEffectIntoGUI(Lnet/minecraft/FontRenderer;Lnet/minecraft/TextureManager;Lnet/minecraft/ItemStack;II)V",
//        at = @At(
//            value = "INVOKE",
//            target = "Lnet/minecraft/RenderItem;renderItemIntoGUI(Lnet/minecraft/FontRenderer;Lnet/minecraft/TextureManager;Lnet/minecraft/item/ItemStack;IIZ)V",
//            remap = false))
//    private void modifyRenderItemAndEffectIntoGUI1(FontRenderer fontRenderer, TextureManager manager,
//        ItemStack itemStack, int x, int y, CallbackInfo ci) {
//        // Moved to before call, will not trigger with forge event
//        GL11.glEnable(GL11.GL_ALPHA_TEST);
//        GL11.glAlphaFunc(GL11.GL_GREATER, 0.01f);
//    }

    /**
     * Forge added a false && to the targeted if statement, this adds the entire statement back
     * TODO: target forges event render class instead & check compatibility
     */
    @SuppressWarnings("DuplicatedCode")
    @Inject(
        method = "renderItemAndEffectIntoGUI(Lnet/minecraft/FontRenderer;Lnet/minecraft/TextureManager;Lnet/minecraft/ItemStack;II)V",
        at = @At(value = "FIELD", target = "Lnet/minecraft/RenderItem;zLevel:F", ordinal = 2))
    private void modifyRenderItemAndEffectIntoGUI2(FontRenderer fontRenderer, TextureManager manager,
        ItemStack itemStack, int x, int y, CallbackInfo ci) {
        if (!CITUtils.renderEnchantmentGUI(itemStack, x, y, this.zLevel) && itemStack.hasEffect()) {
            GL11.glDepthFunc(GL11.GL_EQUAL);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDepthMask(false);
            manager.bindTexture(RES_ITEM_GLINT);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glColor4f(0.5F, 0.25F, 0.8F, 1.0F);
            this.renderGlint(x * 431278612 + y * 32178161, x - 2, y - 2, 20, 20);
            GL11.glBlendFunc(770, 771);
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
        }
    }
}
