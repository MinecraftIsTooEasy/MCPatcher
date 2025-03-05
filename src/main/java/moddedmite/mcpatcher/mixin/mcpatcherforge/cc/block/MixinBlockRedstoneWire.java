package moddedmite.mcpatcher.mixin.mcpatcherforge.cc.block;

import java.util.Random;

import net.minecraft.Block;
import net.minecraft.BlockRedstoneWire;
import net.minecraft.IBlockAccess;
import net.minecraft.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.prupe.mcpatcher.cc.ColorizeBlock;
import com.prupe.mcpatcher.cc.Colorizer;

@Mixin(BlockRedstoneWire.class)
public abstract class MixinBlockRedstoneWire {

    // Not much more compatible but it's the thought that counts
    @ModifyReturnValue(method = "colorMultiplier(Lnet/minecraft/IBlockAccess;III)I", at = @At("RETURN"))
    public int modifyColorMultiplier(int defaultColor, IBlockAccess worldIn, int x, int y, int z) {
        if (ColorizeBlock.colorizeBlock((Block) (Object) this, worldIn, x, y, z)) {
            return ColorizeBlock.blockColor;
        }
        return ColorizeBlock.colorizeRedstoneWire(worldIn, x, y, z, defaultColor);
    }

    //TODO class not found Args
//    @ModifyArgs(
//        method = "randomDisplayTick(Lnet/minecraft/World;IIILjava/util/Random;)V",
//        at = @At(value = "INVOKE", target = "Lnet/minecraft/World;spawnParticle(Lnet/minecraft/EnumParticle;DDDDDD)V"))
//    private void modifyRandomDisplayTick(Args args, World worldIn, int x, int y, int z, Random random) {
//        if (ColorizeBlock.computeRedstoneWireColor(worldIn.getBlockMetadata(x, y, z))) {
//            float f1 = Colorizer.setColor[0];
//            float f2 = Colorizer.setColor[1];
//            float f3 = Colorizer.setColor[2];
//
//            if (f2 < 0.0f) {
//                f2 = 0.0f;
//            }
//
//            if (f3 < 0.0f) {
//                f3 = 0.0f;
//            }
//
//            args.set(4, (double) f1);
//            args.set(5, (double) f2);
//            args.set(6, (double) f3);
//        }
//    }
}
