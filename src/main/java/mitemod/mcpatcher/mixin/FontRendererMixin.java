package mitemod.mcpatcher.mixin;

import com.prupe.mcpatcher.hd.FontUtils;
import mitemod.mcpatcher.api.IFontRenderer;
import net.minecraft.FontRenderer;
import net.minecraft.Minecraft;
import net.minecraft.ResourceLocation;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Mixin(FontRenderer.class)
public class FontRendererMixin implements IFontRenderer {
    @Unique
    private ResourceLocation defaultFont;
    @Unique
    private ResourceLocation hdFont;
    @Unique
    public float[] charWidthf;
    @Unique
    public boolean isHD;
    @Unique
    public float fontAdj;

    @Shadow private final ResourceLocation locationFontTexture;
    @Shadow private int[] charWidth = new int[256];
    @Shadow private boolean unicodeFlag;

    public FontRendererMixin(ResourceLocation locationFontTexture) {
        this.locationFontTexture = locationFontTexture;
    }


    @Override
    public ResourceLocation getDefaultFont() {
        return this.defaultFont;
    }

    @Override
    public void setDefaultFont(ResourceLocation var1) {
        this.defaultFont = var1;
    }

    @Override
    public ResourceLocation getHDFont() {
        return this.hdFont;
    }

    @Override
    public void setHDFont(ResourceLocation var1) {
        this.hdFont = var1;
    }

    @Inject(method = "readFontTexture", at = @At("RETURN"))
    private void inject(CallbackInfo ci) {
        BufferedImage var1;
        try {
            var1 = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(this.locationFontTexture).getInputStream());
        }
        catch (IOException var17) {
            throw new RuntimeException(var17);
        }
        int var2 = var1.getWidth();
        int var3 = var1.getHeight();
        int[] var4 = new int[var2 * var3];
        this.charWidthf = FontUtils.computeCharWidthsf(ReflectHelper.dyCast(this), this.locationFontTexture, var1, var4, this.charWidth);
    }

    @Inject(method = "getStringWidth", at = @At("HEAD"), cancellable = true)
    private void inject1(String par1Str, CallbackInfoReturnable<Integer> cir) {
        if (this.isHD) {
            cir.setReturnValue ((int)FontUtils.getStringWidthf(ReflectHelper.dyCast(this), par1Str));
        }
    }

//    @Overwrite
//    private float renderCharAtPos(int par1, char par2, boolean par3) {
//        return par2 == 32 ? this.charWidthf[32] : (par1 > 0 && !this.unicodeFlag ? this.renderDefaultChar(par1 + 32, par3) : this.renderUnicodeChar(par2, par3));
//    }

    @Shadow
    private float renderDefaultChar(int par1, boolean par2) {
        return 0;
    }
}
