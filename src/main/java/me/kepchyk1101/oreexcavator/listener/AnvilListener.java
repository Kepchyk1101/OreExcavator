package me.kepchyk1101.oreexcavator.listener;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import me.kepchyk1101.oreexcavator.OreExcavator;
import me.kepchyk1101.oreexcavator.util.PickaxeUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnvilListener implements Listener {

    Plugin plugin;

    @EventHandler
    public void onPrepareAnvil1(PrepareAnvilEvent event) {

        ItemStack result = event.getResult();
        if (result == null) return;

        AnvilInventory inventory = event.getInventory();
        ItemStack base = inventory.getItem(0);
        ItemStack additional = inventory.getItem(1);

        if (base != null && base.containsEnchantment(OreExcavator.getOreExcavationEnch())) {
            result.addEnchantment(OreExcavator.getOreExcavationEnch(), 1);
            event.setResult(result);
        }

        if (additional != null && additional.containsEnchantment(OreExcavator.getOreExcavationEnch())) {
            result.addEnchantment(OreExcavator.getOreExcavationEnch(), 1);
            event.setResult(result);
        }

    }

    @EventHandler
    public void onPrepareAnvil2(PrepareAnvilEvent event) {

        AnvilInventory inventory = event.getInventory();
        ItemStack base = inventory.getItem(0);
        ItemStack additional = inventory.getItem(1);

        if (base == null || additional == null) return;
        if (!PickaxeUtil.isPickaxe(base.getType())) return;
        if (additional.getType() != Material.ENCHANTED_BOOK) return;
        if (!additional.containsEnchantment(OreExcavator.getOreExcavationEnch())) return;

        ItemStack result = new ItemStack(base);
        result.addEnchantment(OreExcavator.getOreExcavationEnch(), 1);
        ItemMeta resultMeta = result.getItemMeta();
        List<String> lore = resultMeta.getLore();
        LinkedList<String> newLore = lore != null ? new LinkedList<>(lore) : new LinkedList<>();
        newLore.addFirst("§7Ore Excavation");
        resultMeta.setLore(newLore);
        result.setItemMeta(resultMeta);

        event.setResult(result);

        int repairCostLvls = 1;
        Bukkit.getScheduler().runTask(plugin, () -> {
            inventory.setRepairCost(repairCostLvls);
            event.getView().getPlayer().setWindowProperty(InventoryView.Property.REPAIR_COST, repairCostLvls);
        });

    }

}
