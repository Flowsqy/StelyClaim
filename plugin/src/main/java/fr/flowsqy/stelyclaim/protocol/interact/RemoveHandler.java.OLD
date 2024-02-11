package fr.flowsqy.stelyclaim.protocol.interact;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.api.InteractProtocolHandler;
import fr.flowsqy.stelyclaim.api.action.ActionContext;
import fr.flowsqy.stelyclaim.api.action.ActionResult;
import org.jetbrains.annotations.NotNull;

public class RemoveHandler implements InteractProtocolHandler {

    /*
    private final MailManager mailManager;
    private final PillarTextSender pillarTextSender;*/

    public RemoveHandler(/*StelyClaimPlugin plugin*/) {
        /*this.mailManager = plugin.getMailManager();
        this.pillarTextSender = new PillarTextSender(plugin.getMessages(), "current", plugin.getPillarData());*/
    }

    @Override
    public void interactRegion(@NotNull RegionManager regionManager, @NotNull ProtectedRegion region, @NotNull ActionContext context) {
        regionManager.removeRegion(region.getId());

        /*
        messages.sendMessage(actor.getBukkit(), "claim.command.remove" + (ownRegion ? "" : "-other"), "%region%", owner.owner().getName());

        if (actor.isPlayer()) {
            pillarTextSender.sendMessage(actor.getPlayer(), region);
        }
        if (!ownRegion)
            mailManager.sendInfoToOwner(actor, owner.owner(), messages, "remove");
         */
        context.setResult(new ActionResult(InteractProtocol.SUCCESS, true));
    }
}
