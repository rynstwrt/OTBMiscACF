package art.ryanstew.otbmisc.donorcommands

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.ColorUtil.ColorUtil.hexToRGBArray
import art.ryanstew.otbmisc.util.ColorUtil.ColorUtil.isValidHexCode
import art.ryanstew.otbmisc.util.ColorUtil.ColorUtil.rgbArrayInterp
import art.ryanstew.otbmisc.util.ColorUtil.ColorUtil.rgbArrayToHex
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import com.Zrips.CMI.CMI
import org.bukkit.entity.Player

@CommandAlias("gradientnickname|gradientnick|gnick")
@CommandPermission("otbmisc.gradientnick")
@Description("Set your nickname to gradient text!")
class GradientNickCommand(private val plugin: OTBMisc) : BaseCommand()
{
    private val playerManager = CMI.getInstance().playerManager

    @Default
    fun onNoArguments(player: Player)
    {
        player.sendMessage("${plugin.prefix} &cIncorrect usage! Usage: /gnick <hex1> <hex2> <nickname> <true/false (bold)> <true/false (italic)> <true/false (underlined)>".toChatColor())
    }

    @CatchUnknown
    @CommandCompletion("hex1 hex2 nickname true|false true|false true|false")
    fun onGradientNickCommand(player: Player,
                              hex1: String?,
                              hex2: String?,
                              nick: String?,
                              @Default("false") useBold: Boolean,
                              @Default("false") useItalic: Boolean,
                              @Default("false") useUnderline: Boolean)
    {
        if (hex1 == null || hex2 == null || nick == null)
        {
            player.sendMessage("${plugin.prefix} &cIncorrect usage! Usage: /gnick <hex1> <hex2> <nickname> <true/false (bold)> <true/false (italic)> <true/false (underlined)>".toChatColor())
            return
        }

        if (!hex1.isValidHexCode() || !hex2.isValidHexCode())
        {
            player.sendMessage("${plugin.prefix} &cOne or more hex codes entered are not valid!".toChatColor())
            return
        }

        if (nick.length > 20)
        {
            player.sendMessage("${plugin.prefix} &cThat nickname is too long!".toChatColor())
            return
        }

        val rgbArray1 = hexToRGBArray(hex1)
        val rgbArray2 = hexToRGBArray(hex2)

        val chars = nick.chunked(1)

        var formattedNick = ""
        for (i in chars.indices)
        {
            val currentRGBArray = rgbArrayInterp(rgbArray1, rgbArray2, i.toDouble() / chars.size)
            val currentHex = rgbArrayToHex(currentRGBArray)

            formattedNick += "{$currentHex}"

            if (useBold) formattedNick += "&l"
            if (useItalic) formattedNick += "&o"
            if (useUnderline) formattedNick += "&n"

            formattedNick += chars[i]
        }

        playerManager.getUser(player).setNickName(formattedNick, true)
        player.sendMessage("${plugin.prefix} &aSuccessfully set your nickname!".toChatColor())
    }
}