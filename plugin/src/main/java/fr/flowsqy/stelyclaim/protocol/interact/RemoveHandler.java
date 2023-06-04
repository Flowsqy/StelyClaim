package fr.flowsqy.stelyclaim.protocol.interact;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.FormattedMessages;
import fr.flowsqy.stelyclaim.api.HandledOwner;
import fr.flowsqy.stelyclaim.api.InteractProtocolHandler;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import fr.flowsqy.stelyclaim.command.ClaimCommand;
import fr.flowsqy.stelyclaim.util.MailManager;
import fr.flowsqy.stelyclaim.util.PillarTextSender;

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
    public <T extends ClaimOwner> boolean interactRegion(RegionManager regionManager, ProtectedRegion region, boolean ownRegion, HandledOwner<T> owner, Actor actor, FormattedMessages messages) {
        regionManager.removeRegion(region.getId());

        messages.sendMessage(actor.getBukkit(), "claim.command.remove" + (ownRegion ? "" : "-other"), "%region%", owner.owner().getName());

        if (actor.isPlayer()) {
            pillarTextSender.sendMessage(actor.getPlayer(), region);
        }
        if (!ownRegion)
            mailManager.sendInfoToOwner(actor, owner.owner(), messages, "remove");

        return true;
    }
}
