package art.ryanstew.otbmisc.listeners

import art.ryanstew.otbmisc.OTBMisc
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class JoinCommandListener(private val plugin: OTBMisc) : Listener
{
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent)
    {
        val joinCommand = plugin.getMainConfig().getString("commandOnJoin")

        if (joinCommand == null)
        {
            plugin.logger.severe("commandOnJoin in config.yml is null! Can't run commands on join!")
            return
        }

        e.player.performCommand(joinCommand)
    }
}