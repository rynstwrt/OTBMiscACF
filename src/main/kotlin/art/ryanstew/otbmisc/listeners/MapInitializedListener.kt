package art.ryanstew.otbmisc.listeners

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.MapImageRenderer
import art.ryanstew.otbmisc.util.MiscUtil.Util.toBufferedImage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.MapInitializeEvent
import org.bukkit.map.MapView
import java.awt.Image
import java.awt.image.BufferedImage
import java.net.URL
import javax.imageio.ImageIO
import kotlin.math.abs
import kotlin.math.ceil

class MapInitializedListener(private val plugin: OTBMisc) : Listener
{
    @EventHandler
    fun onMapInitialized(e: MapInitializeEvent)
    {
        val mapSection = plugin.getMapConfig().getConfigurationSection("maps")
        if (mapSection == null)
        {
            plugin.server.logger.severe("maps section in imagemaps.yml could not be found! Image maps will not load!")
            return
        }

        if (!mapSection.contains(e.map.id.toString())) return

        val url = plugin.getMapConfig().getString("maps.${e.map.id}.url")
        if (url == null)
        {
            plugin.server.logger.severe("Map with id ${e.map.id} is configured incorrectly! Skipping its load!")
            return
        }

        val x = plugin.getMapConfig().getInt("maps.${e.map.id}.x")
        val y = plugin.getMapConfig().getInt("maps.${e.map.id}.y")

        e.map.scale = MapView.Scale.FARTHEST
        e.map.isUnlimitedTracking = true
        e.map.renderers.forEach { e.map.removeRenderer(it) }

        plugin.server.scheduler.runTaskAsynchronously(plugin, Runnable()
        {
            var image: BufferedImage
            try
            {
                image = ImageIO.read(URL(url))

                val width = plugin.getMapConfig().get("maps.${e.map.id}.width")
                val height = plugin.getMapConfig().get("maps.${e.map.id}.height")

                var scaledWidth = 128 * ceil(abs(image.width.toDouble() / 256)).toInt()
                var scaledHeight = 128 * ceil(abs(image.height.toDouble() / 256)).toInt()

                if (width != null && height != null)
                {
                    scaledWidth = 128 * (width as Int)
                    scaledHeight = 128 * (height as Int)
                }

                image = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_DEFAULT).toBufferedImage()
            }
            catch (exc: Exception)
            {
                plugin.server.logger.info("Map with id ${e.map.id}'s image could not be loaded!")
                return@Runnable
            }

            e.map.addRenderer(MapImageRenderer(plugin, image.getSubimage(
                x * 128,
                y * 128,
                128,
                128
            )))
        })
    }
}