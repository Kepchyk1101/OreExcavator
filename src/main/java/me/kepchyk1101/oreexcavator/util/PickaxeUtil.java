package me.kepchyk1101.oreexcavator.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UtilityClass
@SuppressWarnings("unused")
public class PickaxeUtil {

    List<Material> PICKAXE_TYPES = new ArrayList<>();

    static {
        PICKAXE_TYPES.addAll(Arrays.asList(
                Material.WOODEN_PICKAXE,
                Material.STONE_PICKAXE,
                Material.GOLDEN_PICKAXE,
                Material.IRON_PICKAXE,
                Material.DIAMOND_PICKAXE,
                Material.NETHERITE_PICKAXE
        ));
    }

    public boolean isPickaxe(Material material) {
        return PICKAXE_TYPES.contains(material);
    }

    public boolean isPickaxe(ItemStack itemStack) {
        return isPickaxe(itemStack.getType());
    }

}
