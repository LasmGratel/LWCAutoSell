package cc.lasmgratel.lwcautosell;

import com.griefcraft.lwc.LWC;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public class CommandAutosell implements CommandExecutor {
    private final LWCAutoSell plugin;

    public CommandAutosell(LWCAutoSell plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))
            return false;

        if (!sender.hasPermission("autosell")) {
            sender.sendMessage("You don't have permission to execute this command");
            return false;
        }

        Player player = (Player) sender;
        Block block = player.getTargetBlock(null, 8);
        if (LWC.getInstance().findProtection(block) == null) {
            player.sendMessage(block.getState().getClass() + " is not protected");
            return false;
        }

        if (!LWC.getInstance().findProtection(block).getBukkitOwner().equals(sender)) {
            player.sendMessage("You are not the owner of this container! Owner is: " + LWC.getInstance().findProtection(block).getBukkitOwner().getDisplayName());
            return false;
        }

        try {
            InventoryHolder.class.cast(block.getState());
        } catch (ClassCastException e) {
            player.sendMessage(e.getLocalizedMessage());
        }

        plugin.getInternalConfig().getLocations().add(block.getLocation());
        plugin.getLogger().info("Added auto selling location " + block.getLocation());
        plugin.saveConfig();
        return true;
    }
}
