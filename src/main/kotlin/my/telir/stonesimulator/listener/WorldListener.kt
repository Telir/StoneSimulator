package my.telir.stonesimulator.listener

import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.weather.WeatherChangeEvent

class WorldListener : Listener {
    private val hostileMobs = listOf(
        EntityType.BLAZE, EntityType.CAVE_SPIDER, EntityType.CREEPER, EntityType.ENDERMAN, EntityType.ENDERMITE,
        EntityType.EVOKER, EntityType.GHAST, EntityType.GUARDIAN, EntityType.HUSK, EntityType.MAGMA_CUBE,
        EntityType.SHULKER, EntityType.SILVERFISH, EntityType.SKELETON, EntityType.SLIME, EntityType.SPIDER,
        EntityType.STRAY, EntityType.VINDICATOR, EntityType.WITCH, EntityType.WITHER, EntityType.ILLUSIONER,
        EntityType.WITHER_SKELETON, EntityType.ZOMBIE, EntityType.PIG_ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.VEX
    )

    @EventHandler
    fun onCreatureSpawn(e: CreatureSpawnEvent) {
        if (e.entityType in hostileMobs) e.isCancelled = true
    }

    @EventHandler
    fun onWeatherChange(e: WeatherChangeEvent) {
        if (e.toWeatherState()) if (e.world.isThundering) e.world.isThundering = false
    }
}