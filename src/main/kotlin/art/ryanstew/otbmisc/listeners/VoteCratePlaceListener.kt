package art.ryanstew.otbmisc.listeners

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class VoteCratePlaceListener : Listener
{
    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockPlace(e: BlockPlaceEvent)
    {
        if (e.block.type != Material.ENDER_CHEST
            || e.itemInHand.itemMeta == null
            || !e.itemInHand.itemMeta!!.hasDisplayName()
            || !e.itemInHand.itemMeta!!.displayName().toString().contains("crate", true)
            || !e.isCancelled) return

        e.itemInHand.amount -= 1
    }
}