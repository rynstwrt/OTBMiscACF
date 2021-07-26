package art.ryanstew.otbmisc.donorcommands

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.user.User
import net.luckperms.api.node.NodeType
import net.luckperms.api.node.types.PrefixNode
import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player
import java.util.stream.Collectors

@CommandAlias("prefix|setprefix|donorprefix")
@CommandPermission("otbmisc.setprefix")
@Description("Set your donor prefix!")
class SetPrefixCommand(private val plugin: OTBMisc) : BaseCommand()
{
    private val luckPerms: LuckPerms = LuckPermsProvider.get()

    @Default
    fun onNoPrefix(player: Player)
    {
        player.sendMessage("${plugin.prefix} &cIncorrect usage! Usage: /setprefix <prefix>".toChatColor())
    }

    @Subcommand("none")
    fun onNone(player: Player)
    {
        val user = luckPerms.getPlayerAdapter(Player::class.java).getUser(player)
        val removedPrefix = removePlayerPrefixNodes(user)
        val message = if (removedPrefix) "${plugin.prefix} &aSuccessfully removed your donor prefix!" else "${plugin.prefix} &cYou did not have a donor prefix to remove!"
        player.sendMessage(message.toChatColor())
    }

    @CatchUnknown
    @CommandCompletion("prefix")
    fun onArgument(player: Player, prefix: String)
    {
        val prefixNoFormat = ChatColor.stripColor(prefix.toChatColor())

        if (prefixNoFormat.isEmpty())
        {
            player.sendMessage("${plugin.prefix} &cThat prefix is too short!".toChatColor())
            return
        }

        if (prefixNoFormat.length > 12)
        {
            player.sendMessage("${plugin.prefix} &cThat prefix is too long!".toChatColor())
            return
        }

        val bannedPrefixes = plugin.getMainConfig().getStringList("bannedPrefixes")
        val matchedBannedPrefixes = bannedPrefixes.filter { prefixNoFormat.replace(" ", "").contains(it, true) }

        if (matchedBannedPrefixes.isNotEmpty())
        {
            player.sendMessage("${plugin.prefix} &cThat prefix is banned!".toChatColor())
            return
        }

        val user = luckPerms.getPlayerAdapter((Player::class.java)).getUser(player)
        removePlayerPrefixNodes(user)

        val formattedPrefix = "&8&l[&r${prefix.trim()}&8&l]&r"
        val newPrefixNode = PrefixNode.builder(formattedPrefix, 2000).build()
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