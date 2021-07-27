package art.ryanstew.otbmisc.playercommands

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import kotlin.math.round

@CommandAlias("pay")
@Description("Pay a player a specified amount!")
class PayCommand(private val plugin: OTBMisc) : BaseCommand()
{
    private val econ = plugin.server.servicesManager.getRegistration(Economy::class.java)!!.provider

    @Default
    @CatchUnknown
    @CommandCompletion("@players amount")
    fun withArguments(player: Player,
                      @Name("user") @Optional @Single targetPlayerName: String?,
                      @Optional amount: Double?)
    {
        if (targetPlayerName == null || amount == null)
        {
            player.sendMessage("${plugin.prefix} &cIncorrect usage! Usage: /pay <player> <amount>".toChatColor())
            return
        }

        if (amount < 1)
        {
            player.sendMessage("${plugin.prefix} &cYou must pay at least $1.00!".toChatColor())
            return
        }

        val targetPlayer: Player? = Bukkit.getPlayer(targetPlayerName)
        if (targetPlayer == null)
        {
            player.sendMessage("${plugin.prefix} &cThat player was not found!".toChatColor())
            return
        }

        val playerWorld = player.world.name
        val targetWorld = targetPlayer.world.name
        val sharedWorldsSection = plugin.getMoneyConfig().getConfigurationSection("sharedWorlds")

        if (sharedWorldsSection == null)
        {
            plugin.logger.severe("Could not find sharedWorlds in money.yml!")
            player.sendMessage("${plugin.prefix} &cCould not get money data from the config! Contact Ryan!".toChatColor())
            return
        }

        if (!sharedWorldsSection.getKeys(false).contains(playerWorld)
            || !sharedWorldsSection.getKeys(false).contains(targetWorld)
            || !canWorldsTrade(playerWorld, targetWorld))
        {
            player.sendMessage("${plugin.prefix} &cYou can't send money from this world into that world!".toChatColor())
            return
        }

        if (econ.getBalance(player) < amount)
        {
            player.sendMessage("${plugin.prefix} &cYou don't have that much money!".toChatColor())
            return
        }

        val withdrawResponse = econ.withdrawPlayer(player, amount)
        if (!withdrawResponse.transactionSuccess())
        {
            player.sendMessage("${plugin.prefix} &cCould not withdraw from your account!")
            return
        }

        val depositResponse = econ.depositPlayer(targetPlayer, amount)
        if (!depositResponse.transactionSuccess())
        {
            player.sendMessage("${plugin.prefix} &cCould pay player! Refunding you!")
            econ.depositPlayer(player, amount)
            return
        }

        val displayFloat: Double = round(amount * 100) / 100
        player.sendMessage("${plugin.prefix} &aSuccessfully paid ${targetPlayer.displayName()}&a $$displayFloat&a.".toChatColor())
        targetPlayer.sendMessage("${plugin.prefix} &aReceived $$displayFloat from ${player.displayName()}&a.".toChatColor())
    }

    private fun canWorldsTrade(w1: String, w2: String): Boolean
    {
        if (w1 == w2) return true

        if (!w1.contains("_") && !w2.contains("_")) return false

        if (!w1.contains("_") && w2.contains("_"))
        {
            if (w2.startsWith(w1, true)) return true
        }
        else if (w1.contains("_") && !w2.contains("_"))
        {
            if (w1.startsWith(w2, true)) return true
        }

        return false
    }
}