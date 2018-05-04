package cc.lasmgratel.lwcautosell;

import com.earth2me.essentials.Essentials;
import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.InventoryHolder;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.StreamSupport;

public class AutoSellTask implements Runnable {
    private LWCAutoSell plugin;
    private Essentials essentials;

    public AutoSellTask(LWCAutoSell plugin, Essentials essentials) {
        this.plugin = plugin;
        this.essentials = essentials;
    }

    @Override
    public void run() {
        plugin.getInternalConfig().getLocations().forEach(location -> {
            InventoryHolder holder = (InventoryHolder) location.getBlock().getState();
            Protection protection = LWC.getInstance().findProtection(location);

            String owner = protection.getOwner() == null ? "Server" : protection.getOwner();

            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(owner));

            if (!LWCAutoSell.getEcon().hasAccount(player))
                LWCAutoSell.getEcon().createPlayerAccount(player);

            AtomicReference<BigDecimal> total = new AtomicReference<>(new BigDecimal(0));

            StreamSupport.stream(holder.getInventory().spliterator(), false)
                    .filter(Objects::nonNull)
                    .peek(itemStack -> total.set(total.get().add(essentials.getWorth().getPrice(itemStack).multiply(new BigDecimal(itemStack.getAmount())))))
                    .forEach(holder.getInventory()::remove);

            LWCAutoSell.getEcon().depositPlayer(player, total.get().doubleValue());

            if (essentials.getOfflineUser(owner) != null)
                essentials.getOfflineUser(owner).addMail("You have earned " + total + " from " + location);
        });
    }
}
