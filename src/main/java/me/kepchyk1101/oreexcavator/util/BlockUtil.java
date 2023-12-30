package me.kepchyk1101.oreexcavator.util;


import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UtilityClass
@SuppressWarnings("unused")
public class BlockUtil {

    List<Material> ORE_TYPES = new ArrayList<>();

    static {
        ORE_TYPES.addAll(Arrays.asList(
                Material.COAL_ORE,
                Material.IRON_ORE,
                Material.GOLD_ORE,
                Material.DIAMOND_ORE,
                Material.REDSTONE_ORE,
                Material.LAPIS_ORE,
                Material.NETHER_GOLD_ORE,
                Material.NETHER_QUARTZ_ORE,
                Material.EMERALD_ORE,
                Material.ANCIENT_DEBRIS
        ));
    }

    public boolean isOre(Material material) {
        return ORE_TYPES.contains(material);
    }

    public boolean isOre(Block block) {
        return isOre(block.getType());
    }

}
