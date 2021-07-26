package art.ryanstew.otbmisc.util

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import org.bukkit.entity.Player

class HomeUtil
{
    companion object HomeUtil
    {
        fun playerHasHomes(plugin: OTBMisc, player: Player): Boolean
        {
            return plugin.getHomeConfig().getConfigurationSection("homes")?.getKeys(false)?.contains(player.uniqueId.toString()) ?: false
        }

        fun getHomeListMessage(plugin: OTBMisc, homeNames: Set<String>): String
        {
            var message = "${plugin.prefix} &7Here are your saved homes: &a"
            message += homeNames.joinToString("&7, &a")
            return "$message&7.".toChatColor()
        }
    }
}