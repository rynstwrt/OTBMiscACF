package art.ryanstew.otbmisc.listeners

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDispenseEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.inventory.ItemStack

class TNTListener(private val plugin: OTBMisc) : Listener
{
    private val bannedItems = setOf(Material.TNT, Material.TNT_MINECART)

    // disable TNT crafting
    @EventHandler
    fun onPrepareItemCraft(e: PrepareItemCraftEvent)
    {
        if (e.inventory.result == null ||
            !bannedItems.contains(e.inventory.result!!.type) ||
            plugin.getMainConfig().getStringList("tntWhitelistedWorlds").contains(e.viewers[0].world.name)) return

        e.inventory.result = ItemStack(Material.AIR)
        e.viewers[0].sendMessage("${plugin.prefix} &cTNT crafting is disabled in this world!".toChatColor())
    }

    // disable TNT placing
    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent)
    {
        if (!bannedItems.contains(e.block.type) ||
            plugin.getMainConfig().getStringList("tntWhitelistedWorlds").contains(e.block.world.name)) return

        e.isCancelled = true
        e.player.sendMessage("${plugin.prefix} &cYou can't place TNT in this world!".toChatColor())
    }

    // disable TNT from dispensers
    @EventHandler
    fun onBlockDispense(e: BlockDispenseEvent)
    {
        if (e.block.type != Material.DISPENSER ||
            !bannedItems.contains(e.item.type) ||
            plugin.getMainConfig().getStringList("tntWhitelistedWorlds").contains(e.block.world.name)) return

        e.isCancelled = true
    }
}