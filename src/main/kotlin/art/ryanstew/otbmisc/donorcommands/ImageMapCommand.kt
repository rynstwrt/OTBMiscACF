package art.ryanstew.otbmisc.donorcommands

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.MapImageRenderer
import art.ryanstew.otbmisc.util.MiscUtil.Util.toBufferedImage
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import co.aikar.commands.annotation.Optional
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
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.abs
import kotlin.math.ceil

@CommandAlias("imagemap")
@CommandPermission("otbmisc.imagemap")
class ImageMapCommand(private val plugin: OTBMisc) : BaseCommand()
{
    private val playersWaitingOnMaps = mutableListOf<UUID>()

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
    @CommandCompletion("<url> [<width>] [<height>]")
    fun onCreateCommand(player: Player, url: String, @Optional width: Int?, @Optional height: Int?)
    {
        if (playersWaitingOnMaps.contains(player.uniqueId))
        {
            player.sendMessage("${plugin.prefix} &7Please wait for your current image to finish loading.".toChatColor())
            return
        }

        if (!url.matches("(http(s?):)([/|.\\w\\s-])*\\.(?:jpg|gif|png|jpeg)".toRegex()))
        {
            player.sendMessage("${plugin.prefix} &cThat is not a valid image url!".toChatColor())
            return
        }

        playersWaitingOnMaps.add(player.uniqueId)
        player.sendMessage("${plugin.prefix} &7Attempting to load image... Please wait.".toChatColor())

        plugin.server.scheduler.runTaskAsynchronously(plugin, Runnable()
        {
            var image: BufferedImage
            try
            {
                image = ImageIO.read(URL(url))

                var scaledWidth = 128 * ceil(abs(image.width.toDouble() / 256)).toInt()
                var scaledHeight = 128 * ceil(abs(image.height.toDouble() / 256)).toInt()

                if (width != null && height != null)
                {
                    scaledWidth = 128 * width
                    scaledHeight = 128 * height
                }

                image = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_DEFAULT).toBufferedImage()
            }
            catch (exc: Exception)
            {
                playersWaitingOnMaps.remove(player.uniqueId)
                player.sendMessage("${plugin.prefix} &cThat image could not be loaded!".toChatColor())
                return@Runnable
            }

            val numMapsWidth = ceil(image.width.toDouble() / 128).toInt()
            val numMapsHeight = ceil(image.height.toDouble() / 128).toInt()

            if (numMapsWidth * numMapsHeight > 36)
            {
                playersWaitingOnMaps.remove(player.uniqueId)
                player.sendMessage("${plugin.prefix} &cThat image is too large!".toChatColor())
                return@Runnable
            }

            val mapsToDrop = mutableListOf<ItemStack>()
            for (y in 0 until numMapsHeight)
            {
                for (x in 0 until numMapsWidth)
                {
                    val map = Bukkit.createMap(player.world)
                    map.scale = MapView.Scale.FARTHEST
                    map.isUnlimitedTracking = true
                    map.renderers.forEach { map.removeRenderer(it) }

                    map.addRenderer(
                        MapImageRenderer(plugin, image.getSubimage(
                        x * 128,
                        y * 128,
                        128,
                        128))
                    )

                    val filledMap = ItemStack(Material.FILLED_MAP, 1)
                    val mapMeta = filledMap.itemMeta as MapMeta

                    mapMeta.mapView = map
                    filledMap.itemMeta = mapMeta

                    if (player.inventory.firstEmpty() == -1)
                        mapsToDrop.add(filledMap)
                    else
                        player.inventory.addItem(filledMap)

                    plugin.getMapConfig().set("maps.${map.id}.url", url)
                    plugin.getMapConfig().set("maps.${map.id}.x", x)
                    plugin.getMapConfig().set("maps.${map.id}.y", y)

                    if (width != null && height != null)
                    {
                        plugin.getMapConfig().set("maps.${map.id}.width", width)
                        plugin.getMapConfig().set("maps.${map.id}.height", height)
                    }
                }
            }

            plugin.saveMapConfig()
            playersWaitingOnMaps.remove(player.uniqueId)

            player.sendMessage("${plugin.prefix} &7Giving you &a${numMapsWidth * numMapsHeight} &7maps for a &a$numMapsWidth&7x&a$numMapsHeight&7 image.".toChatColor())
            player.sendActionBar(Component.text("&7Giving you &a${numMapsWidth * numMapsHeight} &7maps for a &a$numMapsWidth&7x&a$numMapsHeight&7 image.".toChatColor()))

            if (mapsToDrop.isNotEmpty())
            {
                player.sendMessage("${plugin.prefix} &cYour inventory is full and couldn't hold some of the maps!".toChatColor())

                plugin.server.scheduler.scheduleSyncDelayedTask(plugin) { mapsToDrop.forEach { player.world.dropItemNaturally(player.location, it) } }
            }
        })
    }
}