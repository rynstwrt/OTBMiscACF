package art.ryanstew.otbmisc.donorcommands

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.MapImageRenderer
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.map.MapView
import java.awt.Image
import java.awt.image.BufferedImage
import java.net.URL
import javax.imageio.ImageIO
import kotlin.math.abs
import kotlin.math.ceil

@CommandAlias("imagemap")
@CommandPermission("otbmisc.imagemap")
class ImageMapCommand(private val plugin: OTBMisc) : BaseCommand()
{
    private val scalingMode = Image.SCALE_DEFAULT
    private val imageType = BufferedImage.TYPE_INT_ARGB_PRE

    @Default
    @CatchUnknown
    fun sendUsageMessage(sender: CommandSender)
    {
        val message = "${plugin.prefix} &7Usages:\n"
            .plus("&7- &a/mapimage create <url>\n")
            .plus("&7- &a/mapimage create <url> <width> <height>")
        sender.sendMessage(message.toChatColor())
    }

    @Subcommand("create")
    fun onCreateCommand(player: Player, url: String, @Optional width: Int?, @Optional height: Int?)
    {
        if (!url.matches("(http(s?):)([/|.\\w\\s-])*\\.(?:jpg|gif|png|jpeg)".toRegex()))
        {
            player.sendMessage("${plugin.prefix} &cThat is not a valid image url!".toChatColor())
            return
        }

        player.sendMessage("${plugin.prefix} &7Attempting to load image... Please wait.".toChatColor())

        plugin.server.scheduler.runTaskAsynchronously(plugin, Runnable()
        {
            var image: BufferedImage
            try
            {
                image = ImageIO.read(URL(url))

                if (width == null || height == null)
                {
                    val nearestWidth = 128 * ceil(abs(image.width.toDouble() / 256)).toInt()
                    val nearestHeight = 128 * ceil(abs(image.height.toDouble() / 256)).toInt()
                    image = image.getScaledInstance(nearestWidth, nearestHeight, scalingMode).toBufferedImage()
                }
                else
                {
                    image = image.getScaledInstance(128 * width, 128 * height, scalingMode).toBufferedImage()
                }
            }
            catch (exc: Exception)
            {
                player.sendMessage("${plugin.prefix} &cThat image could not be loaded!".toChatColor())
                return@Runnable
            }

            val numMapsWidth = ceil(image.width.toDouble() / 128).toInt()
            val numMapsHeight = ceil(image.height.toDouble() / 128).toInt()

            if (numMapsWidth * numMapsHeight > 36)
            {
                player.sendMessage("${plugin.prefix} &cThat image is too large!".toChatColor())
                return@Runnable
            }

            for (y in 0 until numMapsHeight)
            {
                for (x in 0 until numMapsWidth)
                {
                    val map = Bukkit.createMap(player.world)
                    map.scale = MapView.Scale.FARTHEST
                    map.isUnlimitedTracking = true

                    map.renderers.forEach { map.removeRenderer(it) }
                    map.addRenderer(MapImageRenderer(plugin, image.getSubimage(
                        x * 128,
                        y * 128,
                        128,
                        128)))

                    val filledMap = ItemStack(Material.FILLED_MAP, 1)
                    val mapMeta = filledMap.itemMeta as MapMeta

                    mapMeta.mapView = map
                    filledMap.itemMeta = mapMeta

                    player.inventory.addItem(filledMap)
                }
            }

            player.sendMessage("${plugin.prefix} &7Giving you &a${numMapsWidth * numMapsHeight} &7maps for a &a$numMapsWidth&7x&a$numMapsHeight&7 image.".toChatColor())
            player.sendActionBar(Component.text("&7Giving you &a${numMapsWidth * numMapsHeight} &7maps for a &a$numMapsWidth&7x&a$numMapsHeight&7 image.".toChatColor()))
        })
    }


    private fun Image.toBufferedImage(): BufferedImage {
        if (this is BufferedImage) return this

        val bufferedImage = BufferedImage(this.getWidth(null), this.getHeight(null), imageType)

        val graphics2D = bufferedImage.createGraphics()
        graphics2D.drawImage(this, 0, 0, null)
        graphics2D.dispose()

        return bufferedImage
    }
}