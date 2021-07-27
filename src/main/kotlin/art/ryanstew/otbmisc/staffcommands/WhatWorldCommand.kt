package art.ryanstew.otbmisc.staffcommands

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("whatworld|ww")
@CommandPermission("otbmisc.whatworld")
@Description("See what world a specified user is in!")
class WhatWorldCommand(private val plugin: OTBMisc) : BaseCommand()
{
    @Default
    @CatchUnknown
    @CommandCompletion("@players")
    fun onWhatWorldCommand(sender: CommandSender, @Optional @Single playerName: String?)
    {
        if (sender !is Player && playerName == null)
        {
            sender.sendMessage("${plugin.prefix} &cIncorrect usage! Usage: /whatworld <player>".toChatColor())
            return
        }

        val targetPlayer = playerName?.let { Bukkit.getPlayer(it) } ?: sender as Player
        sendWhatWorldMessage(sender, targetPlayer)
    }

    private fun sendWhatWorldMessage(sender: CommandSender, targetPlayer: Player?)
    {
        var message = "${plugin.prefix} &cThat player was not found!"

        if (targetPlayer != null)
            message = "${plugin.prefix} &6${targetPlayer.displayName()}&a is in the world \"${targetPlayer.world.name}\""

        sender.sendMessage(message.toChatColor())
    }

}