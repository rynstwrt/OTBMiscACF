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
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.user.User
import net.luckperms.api.node.NodeType
import net.luckperms.api.node.types.PrefixNode
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.stream.Collectors

@CommandAlias("gradientsetprefix|gradientprefix|gsetprefix|gprefix")
@CommandPermission("otbmisc.gradientsetprefix")
@Description("Set your prefix to gradient text!")
class GradientSetPrefixCommand(private val plugin: OTBMisc) : BaseCommand()
{
    private val luckPerms: LuckPerms = LuckPermsProvider.get()

    @Default
    fun onNoArguments(player: Player)
    {
        player.sendMessage("${plugin.prefix} &cIncorrect usage! Usage: /gprefix <hex1> <hex2> <prefix> <true/false (bold)> <true/false (italic)> <true/false (underlined)>".toChatColor())
    }

    @CatchUnknown
    @CommandCompletion("hex1 hex2 prefix true|false true|false true|false")
    fun onGradientNickCommand(player: Player,
                              hex1: String?,
                              hex2: String?,
                              prefix: String?,
                              @Default("false") useBold: Boolean,
                              @Default("false") useItalic: Boolean,
                              @Default("false") useUnderline: Boolean)
    {
        if (hex1 == null || hex2 == null || prefix == null)
        {
            player.sendMessage("${plugin.prefix} &cIncorrect usage! Usage: /gprefix <hex1> <hex2> <prefix> <true/false (bold)> <true/false (italic)> <true/false (underlined)>".toChatColor())
            return
        }

        if (!hex1.isValidHexCode() || !hex2.isValidHexCode())
        {
            player.sendMessage("${plugin.prefix} &cOne or more hex codes entered are not valid!".toChatColor())
            return
        }

        if (prefix.length > 12)
        {
            player.sendMessage("${plugin.prefix} &cThat prefix is too long!".toChatColor())
            return
        }
        
        val prefixNoFormat = ChatColor.stripColor(prefix.toChatColor())!!.replace(" ", "")
        
        if (prefixNoFormat.isEmpty())
        {
            player.sendMessage("${plugin.prefix} &cThat prefix is too short!".toChatColor())
            return
        }

        val bannedPrefixes = plugin.getMainConfig().getStringList("bannedPrefixes")
        val matchedBannedPrefixes = bannedPrefixes.filter { prefixNoFormat.replace(" ", "").contains(it, true) }

        if (matchedBannedPrefixes.isNotEmpty())
        {
            player.sendMessage("${plugin.prefix} &cThat prefix is banned!".toChatColor())
            return
        }

        val rgbArray1 = hexToRGBArray(hex1)
        val rgbArray2 = hexToRGBArray(hex2)

        val chars = prefix.chunked(1)

        var formattedPrefix = ""
        for (i in chars.indices)
        {
            val currentRGBArray = rgbArrayInterp(rgbArray1, rgbArray2, i.toDouble() / chars.size)
            val currentHex = rgbArrayToHex(currentRGBArray)

            formattedPrefix += hexToMCCode(currentHex)

            if (useBold) formattedPrefix += "&l"
            if (useItalic) formattedPrefix += "&o"
            if (useUnderline) formattedPrefix += "&n"

            formattedPrefix += chars[i]
        }

        val user = luckPerms.getPlayerAdapter((Player::class.java)).getUser(player)
        removePlayerPrefixNodes(user)

        val newPrefixNode = PrefixNode.builder("&8&l[&r${formattedPrefix.trim()}&8&l]&r", 2000).build()
        user.data().add(newPrefixNode)
        luckPerms.userManager.saveUser(user)

        player.sendMessage("${plugin.prefix} &aSuccessfully set your donor prefix!".toChatColor())
    }

    private fun removePlayerPrefixNodes(user: User): Boolean
    {
        val prefixNodes = user.getNodes(NodeType.PREFIX).stream()
            .filter(NodeType.PREFIX::matches).collect(Collectors.toList())

        if (prefixNodes.isEmpty()) return false

        prefixNodes.forEach { user.data().remove(it) }
        luckPerms.userManager.saveUser(user)

        return true
    }
}