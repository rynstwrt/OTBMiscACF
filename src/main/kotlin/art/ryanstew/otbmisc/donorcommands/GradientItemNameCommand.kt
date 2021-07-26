package art.ryanstew.otbmisc.donorcommands

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.ColorUtil.ColorUtil.hexToMCCode
import art.ryanstew.otbmisc.util.ColorUtil.ColorUtil.hexToRGBArray
import art.ryanstew.otbmisc.util.ColorUtil.ColorUtil.isValidHexCode
import art.ryanstew.otbmisc.util.ColorUtil.ColorUtil.rgbArrayInterp
import art.ryanstew.otbmisc.util.ColorUtil.ColorUtil.rgbArrayToHex
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import org.bukkit.Material
import org.bukkit.entity.Player

@CommandAlias("gradientitemname|gitemname")
@CommandPermission("otbmisc.gradientitemname")
@Description("Set your held item's name to gradient text!")
class GradientItemNameCommand(private val plugin: OTBMisc) : BaseCommand()
{
    @Default
    fun onNoArguments(player: Player)
    {
        player.sendMessage("${plugin.prefix} &cIncorrect usage! Usage: /gitemname <hex1> <hex2> <item name> <bold (true/false)> <italic (true/false)> <underlined (true/false)>".toChatColor())
    }

    @CatchUnknown
    @CommandCompletion("hex1 hex2 Item_Name_Here true|false true|false true|false")
    fun onWithArguments(
        player: Player,
        hex1: String?,
        hex2: String?,
        itemName: String?,
        @Default("false") useBold: Boolean,
        @Default("false") useItalic: Boolean,
        @Default("false") useUnderline: Boolean)
    {
        if (hex1 == null || hex2 == null || itemName == null)
        {
            player.sendMessage("${plugin.prefix} &cIncorrect usage! Usage: /gitemname <hex1> <hex2> <item name> <bold (true/false)> <italic (true/false)> <underlined (true/false)>".toChatColor())
            return
        }

        if (!hex1.isValidHexCode() || !hex2.isValidHexCode())
        {
            player.sendMessage("${plugin.prefix} &cOne or more hex codes entered are not valid!".toChatColor())
            return
        }

        val item = player.inventory.itemInMainHand

        if (item.type == Material.AIR)
        {
            player.sendMessage("${plugin.prefix} &cYou must be holding an item in your main hand!".toChatColor())
            return
        }

        val rgbArray1 = hexToRGBArray(hex1)
        val rgbArray2 = hexToRGBArray(hex2)

        val chars = itemName.chunked(1)

        var formattedItemName = ""
        for (i in chars.indices)
        {
            val currentRGBArray = rgbArrayInterp(rgbArray1, rgbArray2, i / chars.size.toDouble())
            val currentHex = rgbArrayToHex(currentRGBArray)

            formattedItemName += hexToMCCode(currentHex)

            if (useBold) formattedItemName += "&l"
            if (useItalic) formattedItemName += "&o"
            if (useUnderline) formattedItemName += "&n"

            var suffix = chars[i]
            if (chars[i] == "_") suffix = " "

            formattedItemName += suffix
        }

        val meta = item.itemMeta
        meta?.setDisplayName(formattedItemName.toChatColor())

        item.itemMeta = meta
        player.sendMessage("${plugin.prefix} &aSuccessfully set your held item's name!".toChatColor())
    }
}