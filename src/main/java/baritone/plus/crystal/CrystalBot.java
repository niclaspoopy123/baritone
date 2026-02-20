package baritone.plus.crystal;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.stream.StreamSupport;

/**
 * CrystalBot — automated End Crystal PvP module.
 * <p>
 * Features:
 * <ul>
 *   <li>Auto-places End Crystals on obsidian/bedrock near enemy players</li>
 *   <li>Auto-breaks nearby crystals for damage</li>
 *   <li>Configurable place range, break range, and target range</li>
 * </ul>
 */
public class CrystalBot {

    private boolean enabled;
    private double placeRange = 4.5;
    private double breakRange = 5.0;
    private double targetRange = 10.0;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public double getPlaceRange() {
        return placeRange;
    }

    public void setPlaceRange(double placeRange) {
        this.placeRange = Math.max(1.0, Math.min(placeRange, 6.0));
    }

    public double getBreakRange() {
        return breakRange;
    }

    public void setBreakRange(double breakRange) {
        this.breakRange = Math.max(1.0, Math.min(breakRange, 6.0));
    }

    public double getTargetRange() {
        return targetRange;
    }

    public void setTargetRange(double targetRange) {
        this.targetRange = Math.max(1.0, Math.min(targetRange, 20.0));
    }

    /**
     * Called every client tick while the player is in a world.
     */
    public void tick(MinecraftClient client) {
        if (!enabled || client.player == null || client.world == null) {
            return;
        }

        breakNearestCrystal(client);
        placeCrystal(client);
    }

    private void breakNearestCrystal(MinecraftClient client) {
        StreamSupport.stream(client.world.getEntities().spliterator(), false)
                .filter(e -> e instanceof EnderCrystalEntity)
                .filter(e -> e.squaredDistanceTo(client.player) <= breakRange * breakRange)
                .min(Comparator.comparingDouble(e -> e.squaredDistanceTo(client.player)))
                .ifPresent(crystal -> {
                    client.interactionManager.attackEntity(client.player, crystal);
                    client.player.swingHand(Hand.MAIN_HAND);
                });
    }

    private void placeCrystal(MinecraftClient client) {
        if (!isHoldingCrystal(client)) {
            return;
        }

        PlayerEntity target = findNearestPlayer(client);
        if (target == null) {
            return;
        }

        BlockPos bestPos = findBestPlacement(client, target);
        if (bestPos == null) {
            return;
        }

        Vec3d hitVec = new Vec3d(bestPos.getX() + 0.5, bestPos.getY() + 1.0, bestPos.getZ() + 0.5);
        BlockHitResult hitResult = new BlockHitResult(hitVec, Direction.UP, bestPos, false);
        client.interactionManager.interactBlock(client.player, client.world, Hand.MAIN_HAND, hitResult);
    }

    private boolean isHoldingCrystal(MinecraftClient client) {
        return client.player.getMainHandStack().getItem() == Items.END_CRYSTAL
                || client.player.getOffHandStack().getItem() == Items.END_CRYSTAL;
    }

    private PlayerEntity findNearestPlayer(MinecraftClient client) {
        return client.world.getPlayers().stream()
                .filter(p -> p != client.player)
                .filter(p -> p.isAlive())
                .filter(p -> p.squaredDistanceTo(client.player) <= targetRange * targetRange)
                .min(Comparator.comparingDouble(p -> p.squaredDistanceTo(client.player)))
                .orElse(null);
    }

    private BlockPos findBestPlacement(MinecraftClient client, PlayerEntity target) {
        BlockPos playerPos = client.player.getBlockPos();
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;

        int range = (int) Math.ceil(placeRange);
        for (int x = -range; x <= range; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    if (!isValidPlacement(client, pos)) {
                        continue;
                    }
                    double distToPlayer = pos.getSquaredDistance(client.player.getPos(), false);
                    if (distToPlayer > placeRange * placeRange) {
                        continue;
                    }
                    double distToTarget = pos.getSquaredDistance(target.getPos(), false);
                    if (distToTarget < bestDist) {
                        bestDist = distToTarget;
                        best = pos;
                    }
                }
            }
        }
        return best;
    }

    private boolean isValidPlacement(MinecraftClient client, BlockPos pos) {
        var block = client.world.getBlockState(pos).getBlock();
        if (block != Blocks.OBSIDIAN && block != Blocks.BEDROCK) {
            return false;
        }
        return client.world.getBlockState(pos.up()).isAir()
                && client.world.getBlockState(pos.up(2)).isAir();
    }
}
