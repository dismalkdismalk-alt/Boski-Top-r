Java
package pl.anarchia.boskitopor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class BoskiToporPlugin extends JavaPlugin implements CommandExecutor {

    private static BoskiToporPlugin instance;
    private NamespacedKey axeKey;

    @Override
    public void onEnable() {
        instance = this;
        axeKey = new NamespacedKey(this, "boski_topor");

        if (getCommand("boskitopor") != null) {
            getCommand("boskitopor").setExecutor(this);
        }
        Bukkit.getPluginManager().registerEvents(new ToporListener(), this);
    }

    public static BoskiToporPlugin getInstance() {
        return instance;
    }

    public NamespacedKey getAxeKey() {
        return axeKey;
    }

    public ItemStack createBoskiTopor() {
        ItemStack item = new ItemStack(Material.NETHERITE_AXE);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&lBOSKI TOPÓR &7[&cEVENT&7]"));

            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Legendarny oręż stworzony do władania piorunami."));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Kliknij &ePPM&7, aby razić wroga mocą niebios!"));
            lore.add("");
            lore.add(ChatColor.translateAlternateColorCodes('&', "&8» &eEfekt: &fPiorun, Spowolnienie II i Odrzut"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&8» &eCooldown: &a15 sekund"));
            lore.add("");
            lore.add(ChatColor.translateAlternateColorCodes('&', "&c&lANARCHIA.GG EVENT"));

            meta.setLore(lore);
            meta.addEnchant(Enchantment.DAMAGE_ALL, 5, true);
            meta.addEnchant(Enchantment.DURABILITY, 3, true);
            meta.setUnbreakable(true);

            meta.getPersistentDataContainer().set(axeKey, PersistentDataType.BYTE, (byte) 1);

            item.setItemMeta(meta);
        }

        return item;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("boskitopor.admin")) {
            sender.sendMessage(ChatColor.RED + "Brak uprawnień!");
            return true;
        }

        Player target;

        if (args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Gracz jest offline.");
                return true;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Podaj gracza z poziomu konsoli!");
                return true;
            }
            target = (Player) sender;
        }

        target.getInventory().addItem(createBoskiTopor());
        target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aOtrzymałeś &e&lBOSKI TOPÓR&a!"));
        return true;
    }
}
