package my.telir.stonesimulator.util

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

object HeadUtil {
    fun getStoneHead(): ItemStack {
        val item = ItemStack(Material.SKULL_ITEM, 1, 3)
        val meta = item.itemMeta as SkullMeta
        meta.owner = "stone_head_"
        item.itemMeta = meta
        return item
    }
}