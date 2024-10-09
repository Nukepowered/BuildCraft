package net.minecraft.src.buildcraft.core;

import net.minecraft.src.ItemStack;

public abstract class SidedProxy {

    protected SidedProxy() {
        INSTANCE = this;
    }

    public static SidedProxy INSTANCE;

    public abstract String getItemStackLocalizedName(ItemStack stack);
}
