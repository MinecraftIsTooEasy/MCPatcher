package moddedmite.mcpatcher.mixin.mcpatcherforge.base;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;

import javax.imageio.ImageIO;

import net.minecraft.Minecraft;
import net.minecraft.DynamicTexture;
import net.minecraft.TextureManager;
import net.minecraft.ResourceManager;
import net.minecraft.ResourceLocation;
import net.minecraft.Session;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Multimap;
import com.prupe.mcpatcher.MCPatcherUtils;
import com.prupe.mcpatcher.cc.Colorizer;
import com.prupe.mcpatcher.cit.CITUtils;
import com.prupe.mcpatcher.ctm.CTMUtils;
import com.prupe.mcpatcher.hd.FontUtils;
import com.prupe.mcpatcher.mal.resource.TexturePackChangeHandler;
import com.prupe.mcpatcher.mal.tile.TileLoader;
import com.prupe.mcpatcher.mob.MobRandomizer;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    @Shadow
    public abstract ResourceManager getResourceManager();

    @Shadow
    @Final
    private static ResourceLocation locationMojangPng;

    @Inject(
        method = "<init>",
        at = @At("RETURN"))
    private void modifyConstructor(Session par1Session, int par2, int par3, boolean par4, boolean par5, File par6File, File par7File, File par8File, Proxy par9Proxy, String par10Str, CallbackInfo ci) {
        MCPatcherUtils.setMinecraft(par6File, par7File, "1.6.4-MITE", "5.0.3");
    }

    @Inject(
        method = "startGame()V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/ReloadableResourceManager;registerReloadListener(Lnet/minecraft/ResourceManagerReloadListener;)V",
            ordinal = 0))
    private void modifyStartGame1(CallbackInfo ci) {
        TexturePackChangeHandler.earlyInitialize("com.prupe.mcpatcher.mal.tile.TileLoader", "init");
        TexturePackChangeHandler.earlyInitialize("com.prupe.mcpatcher.ctm.CTMUtils", "reset");
        TexturePackChangeHandler.earlyInitialize("com.prupe.mcpatcher.cit.CITUtils", "init");
        TexturePackChangeHandler.earlyInitialize("com.prupe.mcpatcher.hd.FontUtils", "init");
        TexturePackChangeHandler.earlyInitialize("com.prupe.mcpatcher.mob.MobRandomizer", "init");
        TexturePackChangeHandler.earlyInitialize("com.prupe.mcpatcher.cc.Colorizer", "init");
    }

    @Inject(
        method = "startGame()V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/TextureManager;loadTextureMap(Lnet/minecraft/ResourceLocation;Lnet/minecraft/TextureMap;)Z",
            remap = false,
            shift = At.Shift.BEFORE))
    private void modifyStartGame2(CallbackInfo ci) {
        TexturePackChangeHandler.beforeChange1();
    }

    @Inject(
        method = "startGame()V",
        at = @At(
            value = "INVOKE",
            target = "Lorg/lwjgl/opengl/GL11;glViewport(IIII)V",
            remap = false,
            shift = At.Shift.AFTER))
    private void modifyStartGame3(CallbackInfo ci) {
        TexturePackChangeHandler.afterChange1();
    }

//    @Redirect(
//        method = "loadScreen()V",
//        at = @At(
//            value = "INVOKE",
//            target = "Lnet/minecraft/TextureManager;getDynamicTextureLocation(Ljava/lang/String;Lnet/minecraft/DynamicTexture;)Lnet/minecraft/ResourceLocation;"))
//    private ResourceLocation modifyLoadScreen(TextureManager renderEngine, String p_110578_1_,
//        DynamicTexture p_110578_2_) throws IOException {
//        return renderEngine.getDynamicTextureLocation(
//            "logo",
//            new DynamicTexture(
//                ImageIO.read(
//                    this.getResourceManager()
//                        .getResource(locationMojangPng)
//                        .getInputStream())));
//    }

    @Inject(method = "runGameLoop()V", at = @At(value = "HEAD"))
    private void modifyRunGameLoop(CallbackInfo ci) {
        TexturePackChangeHandler.checkForTexturePackChange();
    }
}
