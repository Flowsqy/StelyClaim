package fr.flowsqy.stelyclaim.protocol.domain;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.ClaimMessage;
import fr.flowsqy.stelyclaim.api.ClaimOwner;
import fr.flowsqy.stelyclaim.api.InteractProtocolHandler;
import fr.flowsqy.stelyclaim.command.ClaimCommand;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DomainProtocol implements InteractProtocolHandler {

    private final Protocol protocol;
    private final OfflinePlayer target;

    public DomainProtocol(Protocol protocol, OfflinePlayer target) {
        this.protocol = protocol;
        this.target = target;
    }

    @Override
    public String getPermission() {
        return protocol.getPermission();
    }

    @Override
    public String getName() {
        return protocol.getName();
    }

    @Override
    public <T extends ClaimOwner> boolean interactRegion(RegionManager regionManager, ProtectedRegion region, boolean ownRegion, ClaimHandler<T> handler, T owner, Player sender, ClaimMessage messages) {
        final DefaultDomain domain = protocol.getDomain().apply(region);
        final UUID uuid = target.getUniqueId();
        final boolean state = protocol.isState();
        final boolean result = protocol.getContains().apply(domain, uuid);
        if ((state && result) || (!state && !result)) {
            messages.sendMessage(
                    sender,
                    "claim.domain." + (protocol.isOwner() ? "owner" : "member") + "." + (state ? "already" : "not") + (ownRegion ? "" : "-other"),
                    "%region%", "%target%",
                    owner.getName(), target.getName()
            );
            return false;
        }
        protocol.getAction().accept(domain, uuid);
        messages.sendMessage(
                sender,
                "claim.command." + protocol.getName() + (ownRegion ? "" : "-other"),
                "%region%", "%target%",
                owner.getName(), target.getName()
        );

        if (!ownRegion) {
            StelyClaimPlugin.getInstance().getMailManager().sendInfoToOwner(sender, owner, messages, protocol.getName(), target);
        }

        return true;
    }

    public enum Protocol {

        ADDMEMBER(
                ProtectedRegion::getMembers, DefaultDomain::contains, DefaultDomain::addPlayer,
                ClaimCommand.Permissions.ADDMEMBER, "addmember",
                true, false
        ),
        REMOVEMEMBER(
                ProtectedRegion::getMembers, DefaultDomain::contains, DefaultDomain::removePlayer,
                ClaimCommand.Permissions.REMOVEMEMBER, "removemember",
                false, false
        ),
        ADDOWNER(
                ProtectedRegion::getOwners, DefaultDomain::contains, DefaultDomain::addPlayer,
                ClaimCommand.Permissions.ADDOWNER, "addowner",
                true, true
        ),
        REMOVEOWNER(
                ProtectedRegion::getOwners, DefaultDomain::contains, DefaultDomain::removePlayer,
                ClaimCommand.Permissions.REMOVEOWNER, "removeowner",
                false, true
        );

        private final Function<ProtectedRegion, DefaultDomain> domain;
        private final BiFunction<DefaultDomain, UUID, Boolean> contains;
        private final BiConsumer<DefaultDomain, UUID> action;
        private final String permission;
        private final String name;
        private final boolean state;
        private final boolean owner;

        Protocol(
                Function<ProtectedRegion, DefaultDomain> domain, BiFunction<DefaultDomain, UUID, Boolean> contains, BiConsumer<DefaultDomain, UUID> action,
                String permission, String name,
                boolean state, boolean owner
        ) {
            this.domain = domain;
            this.contains = contains;
            this.action = action;
            this.permission = permission;
            this.name = name;
            this.state = state;
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

        public String getPermission() {
            return permission;
        }

        public String getName() {
            return name;
        }

        public boolean isState() {
            return state;
        }

        public boolean isOwner() {
            return owner;
        }
    }

}
