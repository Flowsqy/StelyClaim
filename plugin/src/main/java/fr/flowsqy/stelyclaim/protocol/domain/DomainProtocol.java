package fr.flowsqy.stelyclaim.protocol.domain;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.api.InteractProtocolHandler;
import fr.flowsqy.stelyclaim.api.LockableCounter;
import fr.flowsqy.stelyclaim.api.action.ActionContext;
import fr.flowsqy.stelyclaim.api.action.ActionResult;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DomainProtocol implements InteractProtocolHandler {

    public static final int CANT_MODIFY;
    public static final int MODIFY;

    static {
        final LockableCounter register = ActionResult.REGISTER;
        try {
            register.lock();
            CANT_MODIFY = register.get();
            MODIFY = register.get();
        } finally {
            register.unlock();
        }
    }

    private final Protocol protocol;
    //private final MailManager mailManager;
    private final OfflinePlayer target;

    public DomainProtocol(@NotNull Protocol protocol, /*MailManager mailManager,*/ @NotNull OfflinePlayer target) {
        this.protocol = protocol;
        //this.mailManager = mailManager;
        this.target = target;
    }

    @Override
    public void interactRegion(@NotNull RegionManager regionManager, @NotNull ProtectedRegion region, @NotNull ActionContext context) {
        final DefaultDomain domain = protocol.getDomain().apply(region);
        final UUID uuid = target.getUniqueId();
        final boolean add = protocol.isAdd();
        final boolean containsTarget = protocol.getContains().apply(domain, uuid);
        if (add == containsTarget) {
            context.setResult(new ActionResult(CANT_MODIFY, false));
            return;
            /*
            messages.sendMessage(
                    actor.getBukkit(),
                    "claim.domain." + (protocol.isOwner() ? "owner" : "member") + "." + (add ? "already" : "not") + (ownRegion ? "" : "-other"),
                    "%region%", "%target%",
                    owner.getName(), target.getName()
            );
            return false;*/
        }
        protocol.getAction().accept(domain, uuid);
        context.setResult(new ActionResult(MODIFY, true));
        /*
        messages.sendMessage(
                actor.getBukkit(),
                "claim.command." + protocol.getName() + (ownRegion ? "" : "-other"),
                "%region%", "%target%",
                owner.getName(), target.getName()
        );

        if (!ownRegion) {
            mailManager.sendInfoToOwner(actor, owner, messages, protocol.getName(), target);
        }*/
    }

    public enum Protocol {

        ADD_MEMBER(
                ProtectedRegion::getMembers, DefaultDomain::contains, DefaultDomain::addPlayer,
                true, false
        ),
        REMOVE_MEMBER(
                ProtectedRegion::getMembers, DefaultDomain::contains, DefaultDomain::removePlayer,
                false, false
        ),
        ADD_OWNER(
                ProtectedRegion::getOwners, DefaultDomain::contains, DefaultDomain::addPlayer,
                true, true
        ),
        REMOVE_OWNER(
                ProtectedRegion::getOwners, DefaultDomain::contains, DefaultDomain::removePlayer,
                false, true
        );

        private final Function<ProtectedRegion, DefaultDomain> domain;
        private final BiFunction<DefaultDomain, UUID, Boolean> contains;
        private final BiConsumer<DefaultDomain, UUID> action;
        private final boolean add;
        private final boolean owner;

        Protocol(
                Function<ProtectedRegion, DefaultDomain> domain, BiFunction<DefaultDomain, UUID, Boolean> contains, BiConsumer<DefaultDomain, UUID> action,
                boolean add, boolean owner
        ) {
            this.domain = domain;
            this.contains = contains;
            this.action = action;
            this.add = add;
            this.owner = owner;
        }

        public Function<ProtectedRegion, DefaultDomain> getDomain() {
            return domain;
        }

        public BiFunction<DefaultDomain, UUID, Boolean> getContains() {
            return contains;
        }

        public BiConsumer<DefaultDomain, UUID> getAction() {
            return action;
        }

        public boolean isAdd() {
            return add;
        }

        public boolean isOwner() {
            return owner;
        }
    }

}
