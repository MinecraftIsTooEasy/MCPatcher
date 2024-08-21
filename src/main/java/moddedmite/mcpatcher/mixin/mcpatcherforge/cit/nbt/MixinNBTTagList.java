package moddedmite.mcpatcher.mixin.mcpatcherforge.cit.nbt;

import java.util.List;

import net.minecraft.NBTBase;
import net.minecraft.NBTTagList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import jss.notfine.util.NBTTagListExpansion;

@Mixin(NBTTagList.class)
public class MixinNBTTagList implements NBTTagListExpansion {

    @Shadow
    private List<NBTBase> tagList;

    public NBTBase tagAt(final int n) {
        return this.tagList.get(n);
    }
}
