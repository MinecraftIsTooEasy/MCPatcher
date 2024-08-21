package moddedmite.mcpatcher.mixin.mcpatcherforge.mob;

import net.minecraft.EntityLiving;
import net.minecraft.EntityLivingBase;
import net.minecraft.NBTTagCompound;

import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.prupe.mcpatcher.mob.MobRandomizer;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase {

    @Inject(method = "writeEntityToNBT(Lnet/minecraft/NBTTagCompound;)V", at = @At("HEAD"))
    private void modifyWriteEntityToNBT(NBTTagCompound tagCompound, CallbackInfo ci) {
        MobRandomizer.ExtraInfo.writeToNBT((EntityLiving) ReflectHelper.dyCast(this), tagCompound);
    }

    @Inject(method = "readEntityFromNBT(Lnet/minecraft/NBTTagCompound;)V", at = @At("HEAD"))
    private void modifyReadEntityFromNBT(NBTTagCompound tagCompound, CallbackInfo ci) {
        MobRandomizer.ExtraInfo.readFromNBT((EntityLiving) ReflectHelper.dyCast(this), tagCompound);
    }
}
