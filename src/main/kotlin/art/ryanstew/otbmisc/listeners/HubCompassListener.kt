package art.ryanstew.otbmisc.listeners

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class HubCompassListener(private val plugin: OTBMisc) : Listener
{
    private var compass: ItemStack? = null

    private fun sendConfigErrorMessage(variable: String)
    {
        plugin.logger.severe("config.yml's value for \"$variable\" is invalid or non-existent! Related features will not work.")
    }

    @EventHandler
    fun onPlayerWorldChange(e: PlayerChangedWorldEvent)
    {
        val hubWorld = plugin.getMainConfig().getString("hubWorld")
        if (hubWorld == null)
        {
            sendConfigErrorMessage("hubWorld")
            return
        }

        if (!e.player.world.name.equals(hubWorld, true)) return

        val compassSection = plugin.getMainConfig().getConfigurationSection("hubCompass")
        if (compassSection == null)
        {
            sendConfigErrorMessage("hubCompass")
            return
        }

        val compassName = compassSection.getString("name")
        if (compassName == null)
        {
            sendConfigErrorMessage("hubCompass.name")
            return
        }

        val compassLore = compassSection.getStringList("lore").map { Component.text(it.toChatColor()) }

        compass = ItemStack(Material.COMPASS, 1)

        val compassMeta = compass!!.itemMeta
        compassMeta.displayName(Component.text(compassName.toChatColor()))
        compassMeta.lore(compassLore)
        compass!!.itemMeta = compassMeta

        plugin.server.scheduler.scheduleSyncDelayedTask(plugin, {
            e.player.inventory.clear()
            e.player.inventory.addItem(compass!!)
        }, 1L)
    }

    @EventHandler
    fun onRightClickCompass(e: PlayerInteractEvent)
    {
        val hubWorld = plugin.getMainConfig().getString("hubWorld")
        if (hubWorld == null)
        {
            sendConfigErrorMessage("hubWorld")
            return
        }

        if (!e.hasItem()
            || (e.action != Action.RIGHT_CLICK_AIR && e.action != Action.RIGHT_CLICK_BLOCK)
            || !e.player.world.name.equals(hubWorld, true)
            || e.item!!.type != Material.COMPASS)
            return

        val contentSection = plugin.getMainConfig().getConfigurationSection("hubCompass.content")
        if (contentSection == null)
        {
            sendConfigErrorMessage("hubCompass.content")
            return
        }

        val inventorySize = plugin.getMainConfig().getInt("hubCompass.slotCount", 54)
        val inventory = plugin.server.createInventory(null, inventorySize, Component.text("&lOTB Teleporter".toChatColor()))

        val itemPaths = contentSection.getKeys(false)
        for (itemPath in itemPaths)
        {
            val name = contentSection.getString("$itemPath.name")
            val materialString = contentSection.getString("$itemPath.material")
            val lore = contentSection.getStringList("$itemPath.lore").map { Component.text(it.replace("&", "§")) }

            if (name == null || materialString == null)
            {
                sendConfigErrorMessage("hubCompass.content.$itemPath")
                continue
            }

            var material: Material?
            try
            {
                material = Material.valueOf(materialString)
            }
            catch (exc: IllegalArgumentException)
            {
                sendConfigErrorMessage("hubCompass.content.$itemPath.material")
                continue
            }

            val item = ItemStack(material, 1)
            item.addUnsafeEnchantment(Enchantment.CHANNELING, 1)

            val meta = item.itemMeta
            meta.displayName(Component.text(name.toChatColor()))
            meta.lore(lore)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)

            item.itemMeta = meta
            inventory.addItem(item)
        }

        val closeButton = ItemStack(Material.BARRIER, 1)
        val closeMeta = closeButton.itemMeta
        closeMeta.displayName(Component.text("&c&lExit".toChatColor()))
        closeButton.itemMeta = closeMeta

        inventory.setItem(inventory.size - 1, closeButton)
        e.player.openInventory(inventory)
    }

    @EventHandler
    fun onInventoryItemClick(e: InventoryClickEvent)
    {
        val hubWorld = plugin.getMainConfig().getString("hubWorld")
        if (hubWorld == null)
        {
            sendConfigErrorMessage("hubWorld")
            return
        }

        if (e.whoClicked.world.name != hubWorld
            || e.inventory.type != InventoryType.CHEST
            || e.currentItem == null
            || e.inventory.size != plugin.getMainConfig().getInt("hubCompass.slotCount", 54))
            return

        e.isCancelled = true
        e.whoClicked.closeInventory()

        val commandString = plugin.hubCompassMap[e.currentItem!!.type] ?: return
        val commands = commandString.split("|").map { it.replace("{player}", e.whoClicked.name) }

        for (command in commands)
        {
            if (command.startsWith("-p"))
            {
                (e.whoClicked as Player).performCommand(command.replace("-p ", ""))
                continue
            }

            plugin.server.dispatchCommand(plugin.server.consoleSender, command)
        }
    }
}