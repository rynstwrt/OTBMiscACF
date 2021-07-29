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
import org.bukkit.inventory.ItemStack

@CommandAlias("unsmeltinventory|unsmeltinv")
@CommandPermission("otbmisc.unsmeltinventory")
class UnsmeltInventoryCommand(private val plugin: OTBMisc) : BaseCommand()
{
    @Default
    @CatchUnknown
    fun onCommand(player: Player)
    {
        val invContents: Array<out ItemStack?> = player.inventory.storageContents

        var amountUnsmelted = 0
        for (itemStack in invContents)
        {
            if (itemStack == null) continue

            val recipes = plugin.server.recipeIterator()
            while (recipes.hasNext())
            {
                val recipe = recipes.next()
                if (recipe !is FurnaceRecipe
                    || recipe.result.type != itemStack.type)
                    continue

                val newItem = recipe.input
                newItem.amount *= itemStack.amount

                player.inventory.remove(itemStack)
                player.inventory.addItem(newItem)

                amountUnsmelted += itemStack.amount
            }
        }

        player.sendMessage("${plugin.prefix} &7Successfully unsmelted &a$amountUnsmelted&7 items.".toChatColor())
    }
}