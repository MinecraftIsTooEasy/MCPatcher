package jss.notfine.util.itembreakparticles;

import net.minecraft.ItemStack;
import net.minecraft.Icon;

public interface IWorldSpawnItemBreakParticle {

    void spawnItemBreakParticle(ItemStack itemStack, String particleName, double x, double y, double z, double velocityX, double velocityY, double velocityZ);

}
