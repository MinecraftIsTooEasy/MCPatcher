package moddedmite.mcpatcher.mixin.mcpatcherforge.cit.nbt;

import java.util.Collection;
import java.util.Map;

import net.minecraft.NBTBase;
import net.minecraft.NBTTagCompound;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import jss.notfine.util.NBTTagCompoundExpansion;

@Mixin(NBTTagCompound.class)
public abstract class MixinNBTTagCompound implements NBTTagCompoundExpansion {

    @Shadow
    private Map<String, NBTBase> tagMap;

    public Collection<NBTBase> getTags() {
        return this.tagMap.values();
    }
}
