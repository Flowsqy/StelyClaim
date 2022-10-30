package fr.flowsqy.stelyclaim.protocol.interact;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.FormattedMessages;
import fr.flowsqy.stelyclaim.api.InteractProtocolHandler;
import fr.flowsqy.stelyclaim.command.ClaimCommand;
import fr.flowsqy.stelyclaim.util.MailManager;
import fr.flowsqy.stelyclaim.util.PillarTextSender;
import org.bukkit.entity.Player;

public class RemoveHandler implements InteractProtocolHandler {

    private final MailManager mailManager;
    private final PillarTextSender pillarTextSender;

    public RemoveHandler(StelyClaimPlugin plugin) {
        this.mailManager = plugin.getMailManager();
        this.pillarTextSender = new PillarTextSender(plugin.getMessages(), "current", plugin.getPillarData());
    }

    @Override
    public String getPermission() {
        return ClaimCommand.Permissions.REMOVE;
    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public <T extends ClaimOwner> boolean interactRegion(RegionManager regionManager, ProtectedRegion region, boolean ownRegion, ClaimHandler<T> handler, T owner, Player sender, FormattedMessages messages) {
        regionManager.removeRegion(region.getId());

        messages.sendMessage(sender, "claim.command.remove" + (ownRegion ? "" : "-other"), "%region%", owner.getName());

        pillarTextSender.sendMessage(sender, region);

        if (!ownRegion)
            mailManager.sendInfoToOwner(sender, owner, messages, "remove");

        return true;
    }
}
