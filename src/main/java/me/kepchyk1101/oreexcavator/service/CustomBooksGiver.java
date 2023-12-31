package me.kepchyk1101.oreexcavator.service;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CustomBooksGiver {

    public void give(@NotNull Player player, @NotNull Enchantment ench, int enchLevel,
                     @Nullable List<String> lore, int customModelData) {

        Validate.notNull(player);
        Validate.notNull(ench);

        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        book.addEnchantment(ench, Math.max(enchLevel, 1));
        ItemMeta bookMeta = book.getItemMeta();
        if (lore != null) {
            bookMeta.setLore(lore);
        }
        if (customModelData >= 0) {
            bookMeta.setCustomModelData(customModelData);
        }
        book.setItemMeta(bookMeta);

        player.getInventory().addItem(book);

    }

}
