package art.ryanstew.otbmisc.listeners

import art.ryanstew.otbmisc.OTBMisc
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class KeepInvListener(private val plugin: OTBMisc) : Listener
{
    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent)
    {
        if (!plugin.getMainConfig().getStringList("keepInvPlayers").contains(e.entity.uniqueId.toString())) return

        e.keepInventory = true
        e.keepLevel = true
        e.droppedExp = 0
        e.drops.clear()
    }
}