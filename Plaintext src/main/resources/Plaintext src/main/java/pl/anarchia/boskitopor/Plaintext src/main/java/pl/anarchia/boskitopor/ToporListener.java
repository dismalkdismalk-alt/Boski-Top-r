Java
package pl.anarchia.boskitopor;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ToporListener implements Listener {

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final int COOLDOWN_SECONDS = 15;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }

        BoskiToporPlugin plugin = BoskiToporPlugin.getInstance();
        if (!meta.getPersistentDataContainer().has(plugin.getAxeKey(), PersistentDataType.BYTE)) {
            return;
        }

        event.setCancelled(true);

        long currentTime = System.currentTimeMillis();
        if (cooldowns.containsKey(player.getUniqueId())) {
            long lastUse = cooldowns.get(player.getUniqueId());
            long timeLeft = (lastUse + (COOLDOWN_SECONDS * 1000L) - currentTime) / 1000L;

            if (timeLeft > 0) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', 
                                "&cBoski Topór odnawia się! Poczekaj: &e" + timeLeft + "s")));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return;
            }
        }

        Block targetBlock = player.getTargetBlockExact(30);
        Location targetLoc;

        if (targetBlock != null) {
            targetLoc = targetBlock.getLocation();
        } else {
            targetLoc = player.getEyeLocation().add(player.getLocation().getDirection().multiply(30));
        }

        targetLoc.getWorld().strikeLightning(targetLoc);
        targetLoc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, targetLoc, 1);
        targetLoc.getWorld().playSound(targetLoc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.8f);

        for (Entity entity : targetLoc.getWorld().getNearbyEntities(targetLoc, 5.0, 5.0, 5.0)) {
            if (entity instanceof LivingEntity && !entity.equals(player)) {
                LivingEntity target = (LivingEntity) entity;
                target.damage(8.0, player);
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 1));
                target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80, 1));

                Vector push = target.getLocation().toVector().subtract(targetLoc.toVector()).normalize().multiply(1.2).setY(0.5);
                target.setVelocity(push);
            }
        }

        cooldowns.put(player.getUniqueId(), currentTime);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&a&lUŻYTO MOCY BOSKIEGO TOPORA!")));
    }
}
