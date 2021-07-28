package art.ryanstew.otbmisc.listeners

import art.ryanstew.otbmisc.OTBMisc
import com.Zrips.CMI.Modules.tp.Teleportations
import com.Zrips.CMI.events.CMIPlayerTeleportEvent
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class CMIRandomTeleportListener(private val plugin: OTBMisc) : Listener
{
    @EventHandler
    fun onRandomTeleport(e: CMIPlayerTeleportEvent)
    {
        if (e.type != Teleportations.TeleportType.randomTp) return

        val soundString = plugin.getMainConfig().getString("randomTeleportSound")
        if (soundString == null)
        {
            plugin.logger.severe("randomTeleportSound in config.yml could not be found! The RTP sound will not work!")
            return
        }

        try
        {
            e.player.playSound(e.to, Sound.valueOf(soundString), 1.5F, 1.0F)
        }
        catch (exc: IllegalArgumentException)
        {
            plugin.logger.severe("randomTeleportSound in config.yml is not a valid sound! The RTP sound will not work!")
        }
    }
}