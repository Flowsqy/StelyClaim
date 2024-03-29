package fr.flowsqy.stelyclaim.protocol.selection;

public class SelectionProtocolOLD {
    /*

    private final StelyClaimPlugin plugin;

    private final boolean expandRegion;
    private final int maxY;
    private final int minY;
    private final PillarTextSender newPillarTextSender;
    private final PillarTextSender previousPillarTextSender;

    public SelectionProtocolOLD(StelyClaimPlugin plugin) {
        this.plugin = plugin;
        final YamlConfiguration configuration = plugin.getConfiguration();
        expandRegion = configuration.getBoolean("expand-selection-y.expand", false);
        maxY = configuration.getInt("expand-selection-y.max", 255);
        minY = configuration.getInt("expand-selection-y.min", 0);
        newPillarTextSender = new PillarTextSender(plugin.getMessages(), "new", plugin.getPillarData());
        previousPillarTextSender = new PillarTextSender(plugin.getMessages(), "previous", plugin.getPillarData());
    }

    public <T extends ClaimOwner> boolean process(@NotNull World world, @NotNull Actor actor, @NotNull HandledOwner<T> handledOwner, @NotNull Protocol protocol) {
        final ClaimHandler<T> handler = handledOwner.handler();
        final FormattedMessages messages = handler.getMessages();
        if (!actor.isPlayer()) {
            throw new IllegalArgumentException("Actor should be a player");
        }
        final Player player = actor.getPlayer();
        final LocalSession session = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player));
        final com.sk89q.worldedit.world.World weWorld = new BukkitWorld(world);

        final Region selection;
        try {
            selection = session.getSelection(weWorld);
        } catch (IncompleteRegionException exception) {
            messages.sendMessage(player, "claim.selection.empty");
            return false;
        }

        if (!(selection instanceof CuboidRegion)) {
            messages.sendMessage(player, "claim.selection.cuboid");
            return false;
        }

        final T owner = handledOwner.owner();
        final boolean ownRegion = owner.own(player);

        if (!ownRegion) {
            if (protocol == Protocol.DEFINE && !player.hasPermission(ClaimCommand.Permissions.getOtherPerm(ClaimCommand.Permissions.DEFINE))) {
                messages.sendMessage(player, "help.define");
                return false;
            } else if (!player.hasPermission(ClaimCommand.Permissions.getOtherPerm(ClaimCommand.Permissions.REDEFINE))) {
                messages.sendMessage(player, "help.redefine");
                return false;
            }
        }

        final RegionManager regionManager = RegionNameManager.getRegionManager(weWorld, player, messages);

        if (regionManager == null)
            return false;

        final String regionName = RegionNameManager.getRegionName(handler, owner);

        final ProtectedRegion region;
        if (protocol == Protocol.DEFINE) {
            if (RegionNameManager.mustNotExist(regionManager, regionName, owner.getName(), ownRegion, player, messages)) {
                return false;
            }
            region = null;
        } else {
            region = RegionNameManager.mustExist(regionManager, regionName, owner.getName(), ownRegion, actor, messages);
            if (region == null) {
                return false;
            }
        }

        if (expandRegion) {
            try {
                final CuboidRegion cuboidSelection = (CuboidRegion) selection;
                selection.expand(
                        BlockVector3.ZERO.withY(maxY - cuboidSelection.getMaximumY()),
                        BlockVector3.ZERO.withY(minY - cuboidSelection.getMinimumY())
                );
            } catch (RegionOperationException e) {
                messages.sendMessage(player, "util.error", "%error%", "ExpandSelection");
                return false;
            }
        }

        final ProtectedCuboidRegion newRegion = new ProtectedCuboidRegion(regionName,
                selection.getMaximumPoint(),
                selection.getMinimumPoint()
        );

        final ApplicableRegionSet intersecting = regionManager.getApplicableRegions(newRegion);

        boolean overlapSame = false;
        final StringBuilder builder = new StringBuilder();
        for (ProtectedRegion overlapRegion : intersecting) {
            if (overlapRegion == region) {
                overlapSame = true;
                continue;
            }
            if (builder.length() > 0)
                builder.append(", ");

            final String id = overlapRegion.getId();
            final String overlappingRegionName;
            if (RegionNameManager.isCorrectId(id)) {
                final String[] partId = id.split("_", 3);
                final ClaimHandler<?> overlappingHandler = plugin.getProtocolManager().getHandler(partId[1]);
                if (overlappingHandler != null) {
                    overlappingRegionName = overlappingHandler.getOwner(partId[2]).getName();
                } else {
                    overlappingRegionName = id;
                }
            } else {
                overlappingRegionName = id;
            }
            builder.append(overlappingRegionName);
        }

        if (builder.length() != 0) {
            messages.sendMessage(player, "claim.selection.overlap", "%regions%", builder.toString());
            return false;
        }

        if (protocol == Protocol.DEFINE) {
            handler.getDefineModifier().modify(player, newRegion, owner);

            regionManager.addRegion(newRegion);

            messages.sendMessage(player, "claim.command.define" + (ownRegion ? "" : "-other"), "%region%", owner.getName());
        } else {
            if (!overlapSame) {
                messages.sendMessage(player, "claim.selection.redefinenotoverlap");
            }

            newRegion.copyFrom(region);
            handler.getRedefineModifier().modify(player, newRegion, owner);
            regionManager.addRegion(newRegion);

            messages.sendMessage(player, "claim.command.redefine" + (ownRegion ? "" : "-other"), "%region%", owner.getName());

            // Previous pillar manage

            previousPillarTextSender.sendMessage(player, region);
        }

        // Pillars manage

        newPillarTextSender.sendMessage(player, newRegion);

        // Mail manage

        if (!ownRegion) {
            plugin.getMailManager().sendInfoToOwner(
                    actor,
                    owner,
                    messages,
                    protocol == Protocol.DEFINE ? "define" : "redefine"
            );
        }

        return true;
    }

    public enum Protocol {

        DEFINE,
        REDEFINE

    }
*/
}
