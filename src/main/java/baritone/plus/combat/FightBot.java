package baritone.plus.combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * FightBot — automated melee combat module.
 * <p>
 * Features:
 * <ul>
 *   <li>Auto-targets nearest hostile mob or (optionally) player</li>
 *   <li>Respects attack cool-down to maximise DPS</li>
 *   <li>Configurable reach, target priority, and player-targeting</li>
 * </ul>
 */
public class FightBot {

    private boolean enabled;
    private boolean targetPlayers;
    private double reach = 4.5;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isTargetPlayers() {
        return targetPlayers;
    }

    public void setTargetPlayers(boolean targetPlayers) {
        this.targetPlayers = targetPlayers;
    }

    public double getReach() {
        return reach;
    }

    public void setReach(double reach) {
        this.reach = Math.max(1.0, Math.min(reach, 6.0));
    }

    /**
     * Called every client tick while the player is in a world.
     */
    public void tick(MinecraftClient client) {
        if (!enabled || client.player == null || client.world == null) {
            return;
        }

        float cooldown = client.player.getAttackCooldownProgress(0.5f);
        if (cooldown < 1.0f) {
            return;
        }

        LivingEntity target = findTarget(client);
        if (target == null) {
            return;
        }

        client.player.lookAt(target, 30.0f, 30.0f);
        client.interactionManager.attackEntity(client.player, target);
        client.player.swingHand(Hand.MAIN_HAND);
    }

    private LivingEntity findTarget(MinecraftClient client) {
        List<LivingEntity> candidates = StreamSupport
                .stream(client.world.getEntities().spliterator(), false)
                .filter(e -> e instanceof LivingEntity)
                .map(e -> (LivingEntity) e)
                .filter(e -> e != client.player)
                .filter(e -> e.isAlive())
                .filter(e -> e.squaredDistanceTo(client.player) <= reach * reach)
                .filter(e -> isValidTarget(e))
                .sorted(Comparator.comparingDouble(e -> e.squaredDistanceTo(client.player)))
                .collect(Collectors.toList());

        return candidates.isEmpty() ? null : candidates.get(0);
    }

    private boolean isValidTarget(LivingEntity entity) {
        if (entity instanceof HostileEntity) {
            return true;
        }
        if (targetPlayers && entity instanceof PlayerEntity) {
            return true;
        }
        return false;
    }
}
