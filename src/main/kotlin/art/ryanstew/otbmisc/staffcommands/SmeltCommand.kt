package art.ryanstew.otbmisc.staffcommands

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.ItemStack

@CommandAlias("smelt|furnace")
@CommandPermission("otbmisc.smelt")
@Description("Smelt the item in your main hand!")
class SmeltCommand(private val plugin: OTBMisc) : BaseCommand()
{
    @Default
    @CatchUnknown
    fun onSmeltCommand(player: Player)
    {
        val item: ItemStack = player.inventory.itemInMainHand

        if (item.type == Material.AIR)
        {
            player.sendMessage("${plugin.prefix} &cYou can't smelt air!".toChatColor())
            return
        }

        val smeltResult = getSmeltResult(item)
        if (smeltResult == null)
        {
            player.sendMessage("${plugin.prefix} &cThis item can't be smelted!".toChatColor())
            return
        }

        player.inventory.remove(item)
        player.inventory.addItem(ItemStack(smeltResult.type, smeltResult.amount * item.amount))

        player.sendMessage("${plugin.prefix} &aSuccessfully smelt your held item!".toChatColor())
    }

    private fun getSmeltResult(item: ItemStack): ItemStack?
    {
        val rIter = plugin.server.recipeIterator()

        while (rIter.hasNext())
        {
            val recipe = rIter.next()

            if (recipe is FurnaceRecipe && recipe.input.type == item.type) return recipe.result
        }

        return null
    }
}