package art.ryanstew.otbmisc.listeners

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class CreeperAwwManListener(private val plugin: OTBMisc) : Listener
{
    @EventHandler
    fun onMessageContainingCreeper(e: AsyncChatEvent)
    {
        if (!e.message().toString().contains("creeper", true)
            || plugin.getMainConfig().getStringList("noCreeperMessagePlayers").contains(e.player.uniqueId.toString())) return

        e.player.sendMessage("&0\n&7✧･ﾟ:*✧･ﾟ:----:ﾟ･✧*:ﾟ･✧\n&aCreeper! Aww man!\n&7✧･ﾟ:*✧･ﾟ:----:ﾟ･✧*:ﾟ･✧\n&0".toChatColor())
        e.player.playSound(e.player.location, Sound.ENTITY_GENERIC_EXPLODE, 2f, .5f)
    }
}