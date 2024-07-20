package free.minced.modules.impl.combat;


import com.mojang.blaze3d.systems.RenderSystem;
import free.minced.events.impl.mobility.ElytraFixEvent;
import free.minced.systems.helpers.IOtherClientPlayerEntity;
import lombok.Getter;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;

import net.minecraft.item.AxeItem;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import free.minced.Minced;
import free.minced.events.Event;

import free.minced.events.impl.input.EventKeyboardInput;
import free.minced.events.impl.mobility.EventFixVelocity;
import free.minced.events.impl.mobility.EventPlayerTravel;
import free.minced.events.impl.player.EventPlayerJump;
import free.minced.events.impl.player.PacketEvent;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;

import free.minced.modules.impl.movement.Flight;
import free.minced.modules.impl.movement.Speed;
import free.minced.primary.IHolder;
import free.minced.primary.game.MobilityFix;
import free.minced.primary.math.MathHandler;


import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.primary.game.PlayerHandler;
import free.minced.systems.rotations.Rotations;
import free.minced.systems.setting.impl.BooleanSetting;
import free.minced.systems.setting.impl.ModeSetting;
import free.minced.systems.setting.impl.MultiBoxSetting;
import free.minced.systems.setting.impl.NumberSetting;
import org.jetbrains.annotations.NotNull;


import java.util.Comparator;
import java.util.stream.StreamSupport;

import static free.minced.framework.render.DrawHandler.*;

@ModuleDescriptor(name = "AttackAura", category = ModuleCategory.COMBAT)

public class AttackAura extends Module {


    private final ModeSetting mode = new ModeSetting("Mode", this, "Rotate", "Rotate", "Legit");
    private final MultiBoxSetting targets = new MultiBoxSetting("Targets", this, "Players", "Friends", "Invisible", "Mobs", "Animals");
    public final ModeSetting mobilityFix = new ModeSetting("Movement Fix", this, "Off", "Off", "Free", "Focused");


    public BooleanSetting elytraOverride = new BooleanSetting("Elytra Override", this, false);

    public final NumberSetting attackRange = new NumberSetting("Attack Range", this, 3, 2.5F, 6, 0.1F);
    public final NumberSetting preAttackRange = new NumberSetting("Pre Attack Range", this, 3, 0.0f, 6, 0.1F);

    public final NumberSetting attackRangeElytra = new NumberSetting("Elytra Attack Range", this, 3, 1.0f, 6, 0.1F,  () -> !elytraOverride.isEnabled());
    public final NumberSetting preAttackRangeElytra = new NumberSetting("Elytra Pre Attack Range", this, 3, 0.0f, 6, 0.1F,  () -> !elytraOverride.isEnabled());



    public BooleanSetting unpressShield = new BooleanSetting("Unpress Shield", this, false);
    public BooleanSetting resolver = new BooleanSetting("Resolver", this, false);

    public BooleanSetting onlyCriticals = new BooleanSetting("Only Criticals", this, true);
    public BooleanSetting spaceOnly = new BooleanSetting("Space Only", this, false, () -> !onlyCriticals.isEnabled());
    private final BooleanSetting rayCast = new BooleanSetting("Ray Cast", this, false);
    private final BooleanSetting ignoreWalls = new BooleanSetting("Ignore Walls", this, false, () -> !rayCast.isEnabled());


    @Getter
    public static Entity target;

    @Getter
    public static float rotationYaw, rotationPitch, pitchAcceleration = 1F;
    private Vec3d rotationPoint = Vec3d.ZERO, rotationMotion = Vec3d.ZERO;

    private boolean lookingAtHitbox;
    public static double cpsLimit;

    @Override
    public void onEvent(Event e) {
        if (e instanceof UpdatePlayerEvent event) {
            if (cpsLimit > 0) {
                --cpsLimit;
            }

            resolvePlayers();
            auraLogic();
            restorePlayers();

            if (target == null) return;
            Rotations.rotate(rotationYaw, rotationPitch, 1);
        } else if (e instanceof PacketEvent.@NotNull Send event) {
            if (event.getPacket() instanceof PlayerInteractEntityC2SPacket pie && PlayerHandler.getInteractType(pie) != PlayerHandler.InteractType.ATTACK && target != null) {
                e.setCancel(true);
            }
        } else if (e instanceof ElytraFixEvent event) {
            event.setYaw(rotationYaw);
            event.setPitch(rotationPitch);
        } else if (e instanceof EventPlayerJump event) {
            if (target == null) return;
            MobilityFix.onJump(event);
        } else if (e instanceof EventFixVelocity event) {
            if (target == null) return;
            MobilityFix.onPlayerMove(event);
        } else if (e instanceof EventPlayerTravel event) {
            if (target == null) return;
            MobilityFix.modifyVelocity(event);
        } else if (e instanceof EventKeyboardInput event) {
            if (target == null) return;
            MobilityFix.onKeyInput(event);
        }
    }




    @Override
    public void onDisable() {
        target = null;
        cpsLimit = 0;
        resetRotations();
        super.onDisable();
    }

    private void resetRotations() {
        lookingAtHitbox = false;
        rotationPoint = Vec3d.ZERO;
        rotationMotion = Vec3d.ZERO;
        if (mc.player == null) return;
        rotationYaw = mc.player.getYaw();
        rotationPitch = mc.player.getPitch();
    }

    private void auraLogic() {
        target = findTarget();
        if (target == null) {
            resetRotations();
            return;
        }
        updateRotations();
        attackTarget();
    }

    public Vec3d getLegitLook(Entity target) {

        float minMotionXZ = 0.003f;
        float maxMotionXZ = 0.03f;

        float minMotionY = 0.001f;
        float maxMotionY = 0.03f;

        double lenghtX = target.getBoundingBox().getLengthX();
        double lenghtY = target.getBoundingBox().getLengthY();
        double lenghtZ = target.getBoundingBox().getLengthZ();


        // Задаем начальную скорость точки
        if (rotationMotion.equals(Vec3d.ZERO))
            rotationMotion = new Vec3d(MathHandler.random(-0.05f, 0.05f), MathHandler.random(-0.05f, 0.05f), MathHandler.random(-0.05f, 0.05f));

        rotationPoint = rotationPoint.add(rotationMotion);

        // Сталкиваемся с хитбоксом по X
        if (rotationPoint.x >= (lenghtX - 0.05) / 2f)
            rotationMotion = new Vec3d(-MathHandler.random(minMotionXZ, maxMotionXZ), rotationMotion.getY(), rotationMotion.getZ());

        // Сталкиваемся с хитбоксом по Y
        if (rotationPoint.y >= lenghtY)
            rotationMotion = new Vec3d(rotationMotion.getX(), -MathHandler.random(minMotionY, maxMotionY), rotationMotion.getZ());

        // Сталкиваемся с хитбоксом по Z
        if (rotationPoint.z >= (lenghtZ - 0.05) / 2f)
            rotationMotion = new Vec3d(rotationMotion.getX(), rotationMotion.getY(), -MathHandler.random(minMotionXZ, maxMotionXZ));

        // Сталкиваемся с хитбоксом по -X
        if (rotationPoint.x <= -(lenghtX - 0.05) / 2f)
            rotationMotion = new Vec3d(MathHandler.random(minMotionXZ, 0.03f), rotationMotion.getY(), rotationMotion.getZ());

        // Сталкиваемся с хитбоксом по -Y
        if (rotationPoint.y <= 0.05)
            rotationMotion = new Vec3d(rotationMotion.getX(), MathHandler.random(minMotionY, maxMotionY), rotationMotion.getZ());

        // Сталкиваемся с хитбоксом по -Z
        if (rotationPoint.z <= -(lenghtZ - 0.05) / 2f)
            rotationMotion = new Vec3d(rotationMotion.getX(), rotationMotion.getY(), MathHandler.random(minMotionXZ, maxMotionXZ));

        // Добавляем джиттер
        rotationPoint.add(MathHandler.random(-0.03f, 0.03f), 0f, MathHandler.random(-0.03f, 0.03f));

        float[] rotation;

        // Если мы перестали смотреть на цель
        if (!PlayerHandler.checkRtx(rotationYaw, rotationPitch, getAttackRange(), ignoreWalls.isEnabled(), rayCast.isEnabled())) {
            float[] rotation1 = PlayerHandler.calcAngle(target.getPos().add(0, target.getEyeHeight(target.getPose()) / 2f, 0));

            // Проверяем видимость центра игрока
            if (PlayerHandler.squaredDistanceFromEyes(target.getPos().add(0, target.getEyeHeight(target.getPose()) / 2f, 0)) <= MathHandler.getPow2Value(getAttackRange())
                    && PlayerHandler.checkRtx(rotation1[0], rotation1[1], getAttackRange(), ignoreWalls.isEnabled(), rayCast.isEnabled())) {
                // наводим на центр
                rotationPoint = new Vec3d(MathHandler.random(-0.1f, 0.1f), target.getEyeHeight(target.getPose()) / (MathHandler.random(1.8f, 2.5f)), MathHandler.random(-0.1f, 0.1f));
            } else {
                // Сканим хитбокс на видимую точку
                float halfBox = (float) (lenghtX / 2f);

                for (float x1 = -halfBox; x1 <= halfBox; x1 += 0.05f) {
                    for (float z1 = -halfBox; z1 <= halfBox; z1 += 0.05f) {
                        for (float y1 = 0.05f; y1 <= target.getBoundingBox().getLengthY(); y1 += 0.15f) {

                            Vec3d v1 = new Vec3d(target.getX() + x1, target.getY() + y1, target.getZ() + z1);

                            // Скипаем, если вне досягаемости
                            if (PlayerHandler.squaredDistanceFromEyes(v1) > MathHandler.getPow2Value(getAttackRange())) continue;

                            rotation = PlayerHandler.calcAngle(v1);
                            if (PlayerHandler.checkRtx(rotation[0], rotation[1], getAttackRange(), ignoreWalls.isEnabled(), rayCast.isEnabled())) {
                                // Наводимся, если видим эту точку
                                rotationPoint = new Vec3d(x1, y1, z1);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return target.getPos().add(rotationPoint);
    }

    private void updateRotations() {


        Vec3d targetVec;

        if (mc.player.isFallFlying() || Minced.getInstance().getModuleHandler().get(Flight.class).isEnabled()) {
            targetVec = target.getEyePos();
        } else {
            if (mode.is("Rotate")) {
                targetVec = target.getEyePos();
            } else if (mode.is("Legit")) {
                targetVec = getLegitLook(target);
            } else {
                return;
            }
        }


        if (targetVec == null)
            return;

        pitchAcceleration = PlayerHandler.checkRtx(rotationYaw, rotationPitch, getAttackRange(), ignoreWalls.isEnabled(), rayCast.isEnabled())
                ? 1F : pitchAcceleration < 8F ? pitchAcceleration * 1.65F : 8F;

        float delta_yaw = MathHelper.wrapDegrees((float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(targetVec.z - mc.player.getZ(), (targetVec.x - mc.player.getX()))) - 90) - rotationYaw);
        float delta_pitch = ((float) (-Math.toDegrees(Math.atan2(targetVec.y - (mc.player.getPos().y + mc.player.getEyeHeight(mc.player.getPose())), Math.sqrt(Math.pow((targetVec.x - mc.player.getX()), 2) + Math.pow(targetVec.z - mc.player.getZ(), 2))))) - rotationPitch);

        float yawStep =  MathHandler.random(65, 75);
        float pitchStep = pitchAcceleration + MathHandler.random(-1F, 1F);


        if (delta_yaw > 180)
            delta_yaw = delta_yaw - 180;

        float deltaYaw = MathHelper.clamp(MathHelper.abs(delta_yaw), -yawStep, yawStep);
        float deltaPitch = MathHelper.clamp(delta_pitch, -pitchStep, pitchStep);

        float newYaw = rotationYaw + (delta_yaw > 0 ? deltaYaw : -deltaYaw);
        float newPitch = MathHelper.clamp(rotationPitch + deltaPitch, -90.0F, 90.0F);

        double gcdFix = (Math.pow(mc.options.getMouseSensitivity().getValue() * 0.6 + 0.2, 3.0)) * 1.2;

        rotationYaw = (float) (newYaw - (newYaw - rotationYaw) % gcdFix);
        rotationPitch = (float) (newPitch - (newPitch - rotationPitch) % gcdFix);

        MobilityFix.fixRotation = rotationYaw;

        lookingAtHitbox = PlayerHandler.checkRtx(rotationYaw, rotationPitch, getAttackRange(), ignoreWalls.isEnabled(), rayCast.isEnabled());

    }

    public void resolvePlayers() {
        if (resolver.isEnabled()) {
            for (PlayerEntity player : mc.world.getPlayers()) {
                if (player instanceof OtherClientPlayerEntity) {
                    ((IOtherClientPlayerEntity) player).resolve();
                }
            }
        }
    }

    public void restorePlayers() {
        if (resolver.isEnabled()) {
            for (PlayerEntity player : mc.world.getPlayers()) {
                if (player instanceof OtherClientPlayerEntity) {
                    ((IOtherClientPlayerEntity) player).releaseResolver();
                }
            }
        }
    }

    private LivingEntity findTarget() {
        return StreamSupport.stream(mc.world.getEntities().spliterator(), false)
                .filter(entity -> entity instanceof LivingEntity && entity != mc.player && isValid((LivingEntity) entity))
                .min(Comparator.comparing(entity -> entity.squaredDistanceTo(mc.player)))
                .map(entity -> (LivingEntity) entity)
                .orElse(null);

    }

    private float getDistance(Entity targetEntity) {
        return PlayerHandler.squaredDistanceFromEyes(targetEntity.getPos().add(0, targetEntity.getEyeHeight(targetEntity.getPose()) / 2f, 0));
    }
    private float getAttackRange() {
        return elytraOverride.isEnabled() && mc.player.isFallFlying() ? attackRangeElytra.getValue().floatValue() : attackRange.getValue().floatValue();
    }
    private float getPreAttackRange() {
        return elytraOverride.isEnabled() && mc.player.isFallFlying() ? preAttackRangeElytra.getValue().floatValue() : preAttackRange.getValue().floatValue();
    }

    private boolean isValid(LivingEntity targetEntity) {

        if (targetEntity == null ||
                targetEntity instanceof ArmorStandEntity ||
                !targetEntity.isAlive() ||
                getDistance(targetEntity) >= MathHandler.getPow2Value((getAttackRange() + getPreAttackRange()))) {
            return false;
        }
        if (Minced.getInstance().getModuleHandler().get(AntiBot.class).isEnabled() && AntiBot.isBot(targetEntity)) return false;
        if (Minced.getInstance().getPartnerHandler().isFriend(targetEntity) && !targets.get("Friends").isEnabled()) return false;

        return (targetEntity instanceof PlayerEntity && targets.get("Players").isEnabled()) ||
                (targetEntity instanceof MobEntity && targets.get("Mobs").isEnabled()) ||
                (targetEntity.isInvisible() && targets.get("Invisible").isEnabled()) ||
                (targetEntity instanceof AnimalEntity && targets.get("Animals").isEnabled());
    }
    private boolean shouldCancelCrit() {
        return !onlyCriticals.isEnabled()
                || mc.player.getAbilities().flying
                || mc.player.hasStatusEffect(StatusEffects.LEVITATION)
                || (mc.player.isFallFlying() || Minced.getInstance().getModuleHandler().get(Flight.class).isEnabled())
                || mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
                || PlayerHandler.isPlayerInWeb()
                || mc.player.isHoldingOntoLadder();
    }
    private boolean canCrit() {


        if (cpsLimit > 0) return false;

        if (mc.player.getAttackCooldownProgress(0.5f) < 0.9F) return false;

        boolean mergeWithSpeed = !Minced.getInstance().getModuleHandler().get(Speed.class).isEnabled() || mc.player.isOnGround();

        if (!mc.options.jumpKey.isPressed() && mergeWithSpeed && spaceOnly.isEnabled()) return true;

        if (mc.player.isInLava()) return true;

        if (!mc.options.jumpKey.isPressed() && PlayerHandler.isAboveWater()) return true;

        if (!shouldCancelCrit()) return !mc.player.isOnGround() && mc.player.fallDistance > 0.0f;


        return true;
    }

    private boolean shieldBreaker(boolean instant) {
        int axe = -1;
        for (int i = 0; i < 9; ++i) {
            if (mc.player.getInventory().getStack(i).getItem() instanceof AxeItem) {
                axe = i;
            }
        }
        if (axe == -1) return false;

        if (!(target instanceof PlayerEntity)) return false;
        if (!((PlayerEntity) target).isUsingItem() && !instant) return false;
        if (((PlayerEntity) target).getOffHandStack().getItem() != Items.SHIELD && ((PlayerEntity) target).getMainHandStack().getItem() != Items.SHIELD)
            return false;

        IHolder.sendPacket(new UpdateSelectedSlotC2SPacket(axe));
        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);
        IHolder.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));

        cpsLimit = 10;

        return true;
    }

    private void attackTarget() {
        if (getDistance(target) >= MathHandler.getPow2Value(getAttackRange())) return;

        if (!lookingAtHitbox && rayCast.isEnabled()) return;

        if (!canCrit()) return;

        if (shieldBreaker(false))
            return;

        boolean negativeFlag = shouldCancelCrit();
        boolean shouldBackSprint = mc.player.isSprinting() && !negativeFlag;
        if (onlyCriticals.isEnabled() && !negativeFlag) {
            PlayerHandler.disableSprint();
        }

        cpsLimit = 10;


        if (unpressShield.isEnabled()) {
            if (mc.player.isUsingItem()) {
                if (mc.player.getOffHandStack().getItem() == Items.SHIELD || mc.player.getMainHandStack().getItem() == Items.SHIELD) {
                    mc.player.stopUsingItem();
                }
            }
        }

        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);

        if (onlyCriticals.isEnabled() && shouldBackSprint) {
            PlayerHandler.enableSprint();
        }

    }

}
