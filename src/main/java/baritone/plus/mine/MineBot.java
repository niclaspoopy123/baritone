package baritone.plus.mine;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.stream.Collectors;

/**
 * MineBot — improved automated mining module.
 * <p>
 * Features:
 * <ul>
 *   <li>Ore priority system (diamond > emerald > gold > iron > coal > lapis > redstone)</li>
 *   <li>Vein mining — mines entire connected ore veins</li>
 *   <li>Configurable search radius</li>
 *   <li>Avoids mining into lava</li>
 * </ul>
 */
public class MineBot {

    /** Ores ordered from highest to lowest priority. */
    private static final List<Block> ORE_PRIORITY = Arrays.asList(
            Blocks.DIAMOND_ORE,
            Blocks.EMERALD_ORE,
            Blocks.GOLD_ORE,
            Blocks.IRON_ORE,
            Blocks.LAPIS_ORE,
            Blocks.REDSTONE_ORE,
            Blocks.COAL_ORE
    );

    private boolean enabled;
    private int searchRadius = 8;
    private boolean veinMine = true;
    private boolean avoidLava = true;

    private BlockPos currentTarget;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            currentTarget = null;
        }
    }

    public int getSearchRadius() {
        return searchRadius;
    }

    public void setSearchRadius(int radius) {
        this.searchRadius = Math.max(1, Math.min(radius, 32));
    }

    public boolean isVeinMine() {
        return veinMine;
    }

    public void setVeinMine(boolean veinMine) {
        this.veinMine = veinMine;
    }

    public boolean isAvoidLava() {
        return avoidLava;
    }

    public void setAvoidLava(boolean avoidLava) {
        this.avoidLava = avoidLava;
    }

    /**
     * Called every client tick while the player is in a world.
     */
    public void tick(MinecraftClient client) {
        if (!enabled || client.player == null || client.world == null) {
            return;
        }

        if (currentTarget != null) {
            BlockState state = client.world.getBlockState(currentTarget);
            if (!isOre(state.getBlock())) {
                currentTarget = null;
            }
        }

        if (currentTarget == null) {
            currentTarget = findBestOre(client);
        }

        if (currentTarget == null) {
            return;
        }

        mineTarget(client);
    }

    private BlockPos findBestOre(MinecraftClient client) {
        BlockPos playerPos = client.player.getBlockPos();
        BlockPos best = null;
        int bestPriority = Integer.MAX_VALUE;
        double bestDist = Double.MAX_VALUE;

        for (int x = -searchRadius; x <= searchRadius; x++) {
            for (int y = -searchRadius; y <= searchRadius; y++) {
                for (int z = -searchRadius; z <= searchRadius; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    Block block = client.world.getBlockState(pos).getBlock();
                    int priority = ORE_PRIORITY.indexOf(block);
                    if (priority < 0) {
                        continue;
                    }
                    if (avoidLava && hasAdjacentLava(client, pos)) {
                        continue;
                    }
                    double dist = pos.getSquaredDistance(client.player.getPos(), false);
                    if (priority < bestPriority || (priority == bestPriority && dist < bestDist)) {
                        bestPriority = priority;
                        bestDist = dist;
                        best = pos;
                    }
                }
            }
        }
        return best;
    }

    /**
     * Returns all positions in the connected vein of the same ore type.
     */
    public List<BlockPos> getVein(MinecraftClient client, BlockPos start) {
        if (client.world == null) {
            return Collections.emptyList();
        }
        Block oreType = client.world.getBlockState(start).getBlock();
        if (!isOre(oreType)) {
            return Collections.emptyList();
        }

        Set<BlockPos> visited = new LinkedHashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = pos.offset(dir);
                if (!visited.contains(neighbor)
                        && client.world.getBlockState(neighbor).getBlock() == oreType) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return new ArrayList<>(visited);
    }

    private void mineTarget(MinecraftClient client) {
        if (currentTarget == null) {
            return;
        }

        double dist = currentTarget.getSquaredDistance(client.player.getPos(), false);
        if (dist > 5.0 * 5.0) {
            // Out of reach — a pathfinder (e.g. Baritone) should walk us there
            return;
        }

        Vec3d center = new Vec3d(
                currentTarget.getX() + 0.5,
                currentTarget.getY() + 0.5,
                currentTarget.getZ() + 0.5
        );
        client.player.lookAt(center);

        if (client.interactionManager != null) {
            Direction face = Direction.UP;
            client.interactionManager.updateBlockBreakingProgress(currentTarget, face);
        }
    }

    private boolean hasAdjacentLava(MinecraftClient client, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            Block adjacent = client.world.getBlockState(pos.offset(dir)).getBlock();
            if (adjacent == Blocks.LAVA) {
                return true;
            }
        }
        return false;
    }

    private boolean isOre(Block block) {
        return ORE_PRIORITY.contains(block);
    }
}
