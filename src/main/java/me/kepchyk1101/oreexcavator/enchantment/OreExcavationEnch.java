package me.kepchyk1101.oreexcavator.enchantment;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import me.kepchyk1101.oreexcavator.service.OreExcavatorService;
import me.kepchyk1101.oreexcavator.util.BlockUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OreExcavationEnch extends Enchantment implements Listener {

    OreExcavatorService oreExcavatorService;
    String name;

    public OreExcavationEnch(@NotNull NamespacedKey key, @NotNull String name,
                             @NotNull OreExcavatorService oreExcavatorService) {
        super(key);
        this.name = name;
        this.oreExcavatorService = oreExcavatorService;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @NotNull
    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.TOOL;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(@NotNull Enchantment other) {
        return false;
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack item) {
        return true;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (!itemInMainHand.getEnchantments().containsKey(this)) return;

        Block initialBlock = event.getBlock();
        if (!BlockUtil.isOre(initialBlock.getType())) return;

        oreExcavatorService.excavate(initialBlock, player);

    }

}
