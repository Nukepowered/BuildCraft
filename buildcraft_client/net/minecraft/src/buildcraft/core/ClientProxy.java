package net.minecraft.src.buildcraft.core;

import net.minecraft.src.ItemStack;
import net.minecraft.src.StringTranslate;

public class ClientProxy extends SidedProxy {

    @Override
    public String getItemStackLocalizedName(ItemStack stack) {
        String unlocalizedName =  stack.getItem().getLocalItemName(stack);
        return StringTranslate.getInstance().translateNamedKey(unlocalizedName).trim();
    }
}
