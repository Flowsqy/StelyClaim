package fr.flowsqy.stelyclaim.command;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import fr.flowsqy.stelyclaim.io.BedrockManager;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Collections;
import java.util.List;

public class BedrockCommand implements TabExecutor {

    private final ConfigurationFormattedMessages messages;
    private final BedrockManager manager;

    public BedrockCommand(StelyClaimPlugin plugin) {
        this.messages = plugin.getMessages();
        this.manager = plugin.getBreakManager();

        Bukkit.getPluginManager().registerEvents(new BedrockListener(), plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return messages.sendMessage(sender, "util.onlyplayer");
        }

        final Player player = (Player) sender;

        if (manager.toggle(player.getName(), true))
            return messages.sendMessage(player, "bedrock.enable");

        return messages.sendMessage(player, "bedrock.disable");
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }

    private final class BedrockListener implements Listener {

        @EventHandler(priority = EventPriority.MONITOR)
        public void onInteract(PlayerInteractEvent event) {
            if (event.getAction() != Action.LEFT_CLICK_BLOCK)
                return;

            final Block block = event.getClickedBlock();
            if (block == null)
                return;

            if (block.getType() != Material.BEDROCK)
                return;

            final Player player = event.getPlayer();

            if (player.getGameMode() != GameMode.SURVIVAL)
                return;

            if (!manager.has(player.getName()))
                return;

            final BlockBreakEvent blockEvent = new BlockBreakEvent(block, player);
            Bukkit.getPluginManager().callEvent(blockEvent);
            if (blockEvent.isCancelled())
                return;

            block.setType(Material.AIR);
            player.playEffect(block.getLocation(), Effect.STEP_SOUND, Material.BEDROCK);

        }

    }

}
