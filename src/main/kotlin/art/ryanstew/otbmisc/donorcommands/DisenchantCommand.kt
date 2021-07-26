package art.ryanstew.otbmisc.donorcommands

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player

@CommandAlias("disenchant")
@CommandPermission("otbmisc.disenchant")
@Description("Remove specified enchantments from your held item!")
class DisenchantCommand(private val plugin: OTBMisc) : BaseCommand()
{

    @Default
    fun onNoArguments(player: Player)
    {
        player.sendMessage("${plugin.prefix} &cIncorrect usage! Usage: /disenchant <enchantment>.".toChatColor())
    }

    @CatchUnknown
    @CommandCompletion("@enchantTabCompleteString")
    fun baseCommand(player: Player, @Name("enchantment") @Split(" ") enchantStrings: Array<String>?)
    {
        if (enchantStrings.isNullOrEmpty())
        {
            player.sendMessage("${plugin.prefix} &cIncorrect usage! Usage: /disenchant <enchantment>".toChatColor())
            return
        }

        val listOfEnchants = mutableListOf<Enchantment>()
        for (enchantString in enchantStrings)
        {
            if (!enchantString.lowercase().matches("[a-z0-9/._-]+".toRegex()) || Enchantment.getByKey(NamespacedKey.minecraft(enchantString.lowercase())) == null)
            {
                player.sendMessage("${plugin.prefix} &c$enchantString is not a valid enchantment!".toChatColor())
                return
            }

            listOfEnchants.add(Enchantment.getByKey(NamespacedKey.minecraft(enchantString.lowercase()))!!)
        }

        val item = player.inventory.itemInMainHand

        if (item.type == Material.AIR)
        {
            player.sendMessage("${plugin.prefix} &cYou must be holding an item in your main hand!".toChatColor())
            return
        }

        if (!item.hasItemMeta() || !item.itemMeta!!.hasEnchants())
        {
            player.sendMessage("${plugin.prefix} &cThat item is not enchanted!".toChatColor())
            return
        }

        val meta = item.itemMeta!!

        for (enchant in listOfEnchants)
        {
            if (!meta.enchants.containsKey(enchant))
            {
                player.sendMessage("${plugin.prefix} &c${enchant.key.key} is not on your held item!".toChatColor())
                return
            }

            meta.removeEnchant(enchant)
        }

        item.itemMeta = meta
        player.sendMessage("${plugin.prefix} &aSuccessfully removed those enchants from your item!".toChatColor())
    }
}
