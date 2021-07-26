package art.ryanstew.otbmisc.util

import net.md_5.bungee.api.ChatColor
import java.text.DecimalFormat

class MiscUtil
{
    companion object Util
    {
        val moneyFormat: DecimalFormat = DecimalFormat("#,##0.00")

        fun String.toChatColor(): String
        {
            return ChatColor.translateAlternateColorCodes('&', this)
        }
    }
}