package art.ryanstew.otbmisc.util

import net.md_5.bungee.api.ChatColor
import java.awt.Image
import java.awt.image.BufferedImage
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

        fun Image.toBufferedImage(): BufferedImage
        {
            if (this is BufferedImage) return this

            val bufferedImage = BufferedImage(this.getWidth(null), this.getHeight(null), BufferedImage.TYPE_INT_ARGB_PRE)

            val graphics2D = bufferedImage.createGraphics()
            graphics2D.drawImage(this, 0, 0, null)
            graphics2D.dispose()

            return bufferedImage
        }
    }
}