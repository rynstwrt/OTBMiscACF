package art.ryanstew.otbmisc.playercommands

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CatchUnknown
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import org.bukkit.entity.Player
import org.bukkit.inventory.FurnaceRecipe

@CommandAlias("unsmelthand|unsmelt")
@CommandPermission("otbmisc.unsmelthand")
class UnsmeltHandCommand(private val plugin: OTBMisc) : BaseCommand()
{
    @Default
    @CatchUnknown
    fun onCommand(player: Player)
    {
        val item = player.inventory.itemInMainHand

        val recipeIter = plugin.server.recipeIterator()
        while (recipeIter.hasNext())
        {
            val recipe = recipeIter.next()

            if (recipe !is FurnaceRecipe) continue

            if (recipe.result.type != item.type) continue

            val newItem = recipe.input
            newItem.amount *= item.amount

            player.inventory.remove(item)
            player.inventory.addItem(newItem)

            player.sendMessage("${plugin.prefix} &7Successfully unsmelted the item in your main hand!".toChatColor())
            return
        }

        player.sendMessage("${plugin.prefix} &cYour held item can't be unsmelted!".toChatColor())
    }
}