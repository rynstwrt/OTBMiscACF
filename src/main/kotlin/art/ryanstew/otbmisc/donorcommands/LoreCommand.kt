package art.ryanstew.otbmisc.donorcommands

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import org.bukkit.entity.Player

@CommandAlias("lore|setlore")
@CommandPermission("otbmisc.lore")
@Description("Set the lore of your held item!")
class LoreCommand(private val plugin: OTBMisc) : BaseCommand()
{
    @Default
    fun sendHelpPage(player: Player, @Default("false") incorrectUsage: Boolean)
    {
        var message = ""

        if (incorrectUsage) message += "&cIncorrect usage!\n"

        message += "&0\n" +
                "&6✧･ﾟ: *✧･ﾟ:* &e&l&nLORE&6 *:ﾟ･✧* :･ﾟ✧\n" +
                "&7• &e/lore add <text> &7- &fAdd a line of lore to your held item.\n" +
                "&7• &e/lore set <line> <text> &7- &fSet an existing line of lore to your held item.\n" +
                "&7• &e/lore clear [<line>] &7- &fClear a line of lore/the lore from your held item.\n" +
                "&7• &e/lore view &7- &fView the lines of lore on your held item.\n" +
                "&6✧･ﾟ: *✧･ﾟ::･ﾟ✧*:ﾟ･ﾟ:*✧･ﾟ::･ﾟ✧* :･ﾟ✧" +
                "\n&0"

        player.sendMessage(message.toChatColor())
    }

    @CatchUnknown
    fun onIncorrectUsage(player: Player) = sendHelpPage(player, true)

    @Subcommand("add")
    @CommandCompletion("<text>")
    fun onAddCommand(player: Player, text: String)
    {
        val item = player.inventory.itemInMainHand

        if (item.itemMeta == null)
        {
            player.sendMessage("${plugin.prefix} &cThat item can not have its lore set!".toChatColor())
            return
        }

        val meta = item.itemMeta!!
        val lore = meta.lore ?: mutableListOf<String>()
        lore.add(text.replace("&", "§"))

        if (lore.size > 4)
        {
            player.sendMessage("${plugin.prefix} &cYour item already has 4 lines of lore!".toChatColor())
            return
        }

        meta.lore = lore
        item.itemMeta = meta

        player.sendMessage("${plugin.prefix} &aSuccessfully added lore to your held item!".toChatColor())
    }

    @Subcommand("set")
    @CommandCompletion("<lineNumber> <text>")
    fun onSetCommand(player: Player, lineNumber: Int, text: String)
    {
        val item = player.inventory.itemInMainHand

        if (!item.hasItemMeta()
            || item.itemMeta!!.lore == null
            || item.itemMeta!!.lore!!.isEmpty())
        {
            player.sendMessage("${plugin.prefix} &cThis item does not have lore set!".toChatColor())
            return
        }

        val meta = item.itemMeta!!
        val lore = meta.lore!!

        if (lineNumber < 1 || lineNumber > lore.size)
        {
            player.sendMessage("${plugin.prefix} &cThat line number is not valid or doesn't exist!".toChatColor())
            return
        }

        lore[lineNumber - 1] = text.replace("&", "§")
        meta.lore = lore
        item.itemMeta = meta

        player.sendMessage("${plugin.prefix} &aSuccessfully set lore line $lineNumber of your held item!".toChatColor())
    }

    @Subcommand("clear")
    @CommandCompletion("[<lineNumber>]")
    fun onClearCommand(player: Player, @Optional lineNumber: Int?)
    {
        val item = player.inventory.itemInMainHand

        if (!item.hasItemMeta()
            || item.itemMeta!!.lore == null
            || item.itemMeta!!.lore!!.isEmpty())
        {
            player.sendMessage("${plugin.prefix} &cThis item does not have lore set!".toChatColor())
            return
        }

        val meta = item.itemMeta!!
        val lore = meta.lore!!

        if (lineNumber == null)
        {
            // clear all lore lines
            lore.clear()
            player.sendMessage("${plugin.prefix} &aSuccessfully cleared your held item's lore!".toChatColor())
        }
        else if (lineNumber < 1 || lineNumber > lore.size)
        {
            player.sendMessage("${plugin.prefix} &cThat line number is not valid or doesn't exist!".toChatColor())
            return
        }
        else
        {
            // clear the specified lore line
            lore.removeAt(lineNumber - 1)
            player.sendMessage("${plugin.prefix} &aSuccessfully cleared lore line $lineNumber from your held item".toChatColor())
        }

        meta.lore = lore
        item.itemMeta = meta
    }

    @Subcommand("view")
    fun onViewCommand(player: Player)
    {
        val item = player.inventory.itemInMainHand

        if (!item.hasItemMeta()
            || item.itemMeta!!.lore == null
            || item.itemMeta!!.lore!!.isEmpty())
        {
            player.sendMessage("${plugin.prefix} &cThis item does not have lore set!".toChatColor())
            return
        }

        var message = "&eYour held item's lore lines:\n"
        val lore = item.itemMeta!!.lore!!
        for (i in lore.indices)
        {
            message += "&7${i + 1}) ${lore[i].replace("§", "&")}\n"
        }

        player.sendMessage(message.toChatColor())
    }

}