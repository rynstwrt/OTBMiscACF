package art.ryanstew.otbmisc.util

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import java.awt.image.BufferedImage
import java.io.IOException

class MapImageRenderer(private val plugin: OTBMisc, private val image: BufferedImage) : MapRenderer()
{
    private var done = false

    override fun render(map: MapView, canvas: MapCanvas, player: Player)
    {
        try
        {
            if (done) return

            canvas.drawImage(0, 0, image)
            done = true
        }
        catch (exc: IOException)
        {
            player.sendMessage("${plugin.prefix} &cCould not load that image!".toChatColor())
            player.inventory.remove(ItemStack(Material.FILLED_MAP, 1))
        }
    }
}