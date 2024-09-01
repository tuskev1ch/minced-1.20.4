package free.minced.modules.impl.combat;


import com.mojang.blaze3d.systems.RenderSystem;
import free.minced.Minced;
import free.minced.events.Event;
import free.minced.events.impl.input.EventKeyboardInput;
import free.minced.events.impl.mobility.ElytraFixEvent;
import free.minced.events.impl.mobility.EventFixVelocity;
import free.minced.events.impl.mobility.EventPlayerTravel;
import free.minced.events.impl.player.EventPlayerJump;
import free.minced.events.impl.player.EventSync;
import free.minced.events.impl.player.PacketEvent;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.events.impl.render.Render3DEvent;
import free.minced.framework.color.ClientColors;
import free.minced.framework.color.ColorHandler;
import free.minced.framework.render.DrawHandler;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.modules.impl.movement.Flight;
import free.minced.modules.impl.movement.Speed;
import free.minced.primary.IHolder;
import free.minced.primary.game.MobilityFix;
import free.minced.primary.game.PlayerHandler;
import free.minced.primary.math.MathHandler;
import free.minced.primary.other.Path;
import free.minced.systems.helpers.IOtherClientPlayerEntity;
import free.minced.systems.rotations.Rotations;
import free.minced.systems.setting.impl.BooleanSetting;
import free.minced.systems.setting.impl.ModeSetting;
import free.minced.systems.setting.impl.MultiBoxSetting;
import free.minced.systems.setting.impl.NumberSetting;
import it.unimi.dsi.fastutil.Stack;
import lombok.Getter;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
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
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.stream.StreamSupport;

import static free.minced.framework.render.DrawHandler.endRender;
import static free.minced.framework.render.DrawHandler.setupRender;
import static free.minced.primary.IAccess.BUILDER;
import static free.minced.primary.IAccess.TESSELLATOR;

@ModuleDescriptor(name = "TPAura", category = ModuleCategory.COMBAT)
public class TPAura extends Module {


    private final MultiBoxSetting targets = new MultiBoxSetting("Targets", this, "Players", "Friends", "Invisible", "Mobs", "Animals");

    public final NumberSetting attackRange = new NumberSetting("Attack Range", this, 3, 2.5F, 32, 0.1F);
    public final NumberSetting step = new NumberSetting("Step", this, 5, 1, 5, 1);

    public final BooleanSetting drawStep = new BooleanSetting("Draw Step", this, true);
    public final BooleanSetting rotate = new BooleanSetting("Rotate", this, true);
    public final BooleanSetting calculateY = new BooleanSetting("Calculate Y", this, true);
    public final BooleanSetting shieldBreaker = new BooleanSetting("Shield Breaker", this, true);

    public Path path;


    @Getter
    public static Entity target;

    @Getter
    public static float rotationYaw, rotationPitch, pitchAcceleration = 1F;
    private Vec3d rotationPoint = Vec3d.ZERO, rotationMotion = Vec3d.ZERO;

    public static double cpsLimit;

    @Override
    public void onEvent(Event e) {
        if (e instanceof UpdatePlayerEvent event) {
            if (cpsLimit > 0) {
                --cpsLimit;
            }

            auraLogic();

            if (mc.player == null) return;
            if (rotate.isEnabled()) {
                Rotations.rotate(rotationYaw, rotationPitch);
            }
        } else if (e instanceof EventSync eventSync) {
            if (mc.player == null || !rotate.isEnabled()) return;

            mc.player.setBodyYaw(rotationYaw);
            mc.player.setHeadYaw(rotationYaw);
            
        } else if (e instanceof PacketEvent.@NotNull Send event) {
            if (event.getPacket() instanceof PlayerInteractEntityC2SPacket pie && PlayerHandler.getInteractType(pie) != PlayerHandler.InteractType.ATTACK && target != null) {
                e.setCancel(true);
            }
        } else if (e instanceof Render3DEvent event) {
            if (path == null || !drawStep.isEnabled()) return;
            for (Vec3d entity : path.getPath()) {

                double x = (entity.x) - mc.getEntityRenderDispatcher().camera.getPos().getX();
                double y = (entity.y) - mc.getEntityRenderDispatcher().camera.getPos().getY();
                double z = (entity.z) - mc.getEntityRenderDispatcher().camera.getPos().getZ();

                MatrixStack stack = event.getStack();
                stack.push();
                setupRender();
                RenderSystem.disableCull();
                RenderSystem.disableDepthTest();
                RenderSystem.setShader(GameRenderer::getPositionColorProgram);
                BUILDER.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
                float cos;
                float sin;
                for (int i = 0; i <= 30; i++) {
                    cos = (float) (x + Math.cos(i * 6.28 / 30) * target.getWidth() * 0.8);
                    sin = (float) (z + Math.sin(i * 6.28 / 30) * target.getWidth() * 0.8);

                    BUILDER.vertex(stack.peek().getPositionMatrix(), cos, (float) y, sin).color(ColorHandler.applyOpacity(ClientColors.getTheme().getAccentColor().darker(), 255).getRGB()).next();
                }
                TESSELLATOR.draw();
                RenderSystem.enableCull();
                endRender();
                RenderSystem.enableDepthTest();
                stack.pop();
            }
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
        path = null;
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
        path = new Path(mc.player.getPos(), target.getPos());
        path.calculatePath(step.getValue().intValue(), calculateY.isEnabled());

        updateRotations();
        attackTarget();
    }


    private void updateRotations() {
        if (!rotate.isEnabled()) return;

        Vec3d targetVec;

         targetVec = target.getEyePos();
            



        if (targetVec == null)
            return;

        pitchAcceleration = mc.player.isFallFlying() ? 90 : (PlayerHandler.checkRtx(rotationYaw, rotationPitch, attackRange.getValue().floatValue(), true, false)
                ? 1F : pitchAcceleration < 8F ? pitchAcceleration * 1.65F : 8F);

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

        MobilityFix.rotationYaw = rotationYaw;
        MobilityFix.rotationPitch = rotationPitch;


    }

    private LivingEntity findTarget() {
        return (LivingEntity) StreamSupport.stream(mc.world.getEntities().spliterator(), false)
                .filter(entity -> entity instanceof LivingEntity && entity != mc.player && isValid((LivingEntity) entity))
                .min(Comparator.comparing(entity -> entity.squaredDistanceTo(mc.player))).orElse(null);

    }

    private float getDistance(Entity targetEntity) {
        return PlayerHandler.squaredDistanceFromEyes(targetEntity.getPos().add(0, targetEntity.getEyeHeight(targetEntity.getPose()) / 2f, 0));
    }


    private boolean isValid(LivingEntity targetEntity) {

        if (targetEntity == null ||
                targetEntity instanceof ArmorStandEntity ||
                !targetEntity.isAlive() ||
                getDistance(targetEntity) >= MathHandler.getPow2Value((attackRange.getValue().floatValue() + 0))) {
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
        return mc.player.getAbilities().flying
                || mc.player.hasStatusEffect(StatusEffects.LEVITATION)
                || (mc.player.isFallFlying() || Minced.getInstance().getModuleHandler().get(Flight.class).isEnabled())
                || mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
                || PlayerHandler.isPlayerInWeb()
                || mc.world.getBlockState(mc.player.getBlockPos()).getBlock() == Blocks.LADDER;
    }
    private boolean canCrit() {


        if (mc.player.getAttackCooldownProgress(0.5f) < 0.9F) return false;


        return true;
    }

    private boolean shieldBreaker() {
        int axe = -1;
        for (int i = 0; i < 9; ++i) {
            if (mc.player.getInventory().getStack(i).getItem() instanceof AxeItem) {
                axe = i;
            }
        }
        if (axe == -1) return false;
        if (!shieldBreaker.isEnabled()) return false;

        if (!(target instanceof PlayerEntity)) return false;
        if (!((PlayerEntity) target).isUsingItem()) return false;
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
        if (getDistance(target) >= MathHandler.getPow2Value(attackRange.getValue().floatValue())) return;
        
        if (!canCrit() || cpsLimit != 0) return;

        if (shieldBreaker())
            return;

        cpsLimit = 10;

        IHolder.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + 0.01, mc.player.getZ(), false));


        for (Vec3d vec : path.getPath()) {
            IHolder.sendPacket(new PlayerMoveC2SPacket.Full(vec.x, vec.y, vec.z, mc.player.getYaw(), mc.player.getPitch(), false));
        }
        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);

        Collections.reverse(path.getPath());
        for (Vec3d vec : path.getPath()) {
            IHolder.sendPacket(new PlayerMoveC2SPacket.Full(vec.x, vec.y, vec.z, mc.player.getYaw(), mc.player.getPitch(), false));
        }


    }

}
