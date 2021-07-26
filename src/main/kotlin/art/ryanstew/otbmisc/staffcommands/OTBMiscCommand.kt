package art.ryanstew.otbmisc.staffcommands

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import org.bukkit.command.CommandSender

@CommandAlias("otbmisc")
@CommandPermission("otbmisc.reload")
@Description("The base command of OTBMisc!")
class OTBMiscCommand(private val plugin: OTBMisc) : BaseCommand()
{
    @Subcommand("reload")
    fun onReloadArg(sender: CommandSender)
    {
        plugin.reloadMainConfig()
        plugin.reloadMoneyConfig()
        plugin.reloadHomeConfig()
        sender.sendMessage("${plugin.prefix} &aReloaded all configs successfully!".toChatColor())
    }

    @CatchUnknown @Default
    fun onNoArgsOrUnknownArgs(sender: CommandSender)
    {
        sender.sendMessage("${plugin.prefix} &cIncorrect usage! Usage: /otbmisc reload.".toChatColor())
    }


}