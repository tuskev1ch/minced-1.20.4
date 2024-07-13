package free.minced.modules.impl.combat;


import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import free.minced.events.Event;
import free.minced.events.impl.player.EventSync;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.primary.chat.ChatHandler;
import free.minced.primary.time.TimerHandler;
import free.minced.systems.setting.impl.ModeSetting;
import org.lwjgl.glfw.GLFW;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;
@ModuleDescriptor(name = "AntiBot", category = ModuleCategory.COMBAT)

public class AntiBot extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", this, "Motion", "Motion", "UUID");

    public static ArrayList<PlayerEntity> bots = new ArrayList<>();
    private final TimerHandler timer = new TimerHandler();
    private int botsNumber = 0;
    private int ticks = 0;

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventSync e) {
            onSync(e);
        }
    }

    public void onSync(EventSync e) {
        mc.world.getPlayers().forEach(this::isABot);
        if (AttackAura.target instanceof PlayerEntity ent) isABot(ent);

        bots.forEach(b -> {
                try {
                    mc.world.removeEntity(b.getId(), Entity.RemovalReason.KILLED);
                } catch (Exception ignored) {
                }
        });

        if (timer.passedMs(10000)) {
            bots.clear();
            botsNumber = 0;
            ticks = 0;
            timer.reset();
        }
    }

    private void isABot(PlayerEntity ent) {
        if (bots.contains(ent))
            return;

        if (mode.is("UUID")) {
            if (!bots.contains(ent) &&
                    !ent.getUuid().equals(UUID.nameUUIDFromBytes(("OfflinePlayer:" + ent.getName().getString()).getBytes(StandardCharsets.UTF_8))) && ent instanceof OtherClientPlayerEntity
                    && !ent.getName().getString().contains("-")) {
                //ChatHandler.display(ent.getName().getString() + "  bot!");
                ++botsNumber;
                bots.add(ent);
            }
        } else if (mode.is("Motion")) {
            double speed = (ent.getX() - ent.prevX) * (ent.getX() - ent.prevX) + (ent.getZ() - ent.prevZ) * (ent.getZ() - ent.prevZ);
            if (speed > 0.5 && !bots.contains(ent)) {
                if (ticks >= 3) {
                    //ChatHandler.display(ent.getName().getString() + "  bot!");
                    ++botsNumber;
                    bots.add(ent);
                }
                ticks++;
            }
        }
    }
}
