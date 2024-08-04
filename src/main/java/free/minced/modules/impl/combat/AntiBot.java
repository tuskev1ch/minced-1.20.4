package free.minced.modules.impl.combat;


import com.mojang.authlib.GameProfile;
import free.minced.Minced;
import free.minced.events.impl.player.PacketEvent;
import free.minced.events.impl.player.TickEvent;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import free.minced.events.Event;
import free.minced.events.impl.player.EventSync;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.primary.time.TimerHandler;
import free.minced.systems.setting.impl.ModeSetting;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@ModuleDescriptor(name = "AntiBot", category = ModuleCategory.COMBAT)

public class AntiBot extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", this, "Beta", "Beta", "Motion", "UUID");


    final Set<UUID> suspectSet = new HashSet<>();
    static final Set<UUID> botSet = new HashSet<>();

    public static final ArrayList<PlayerEntity> bots = new ArrayList<>();
    private final TimerHandler timer = new TimerHandler();
    private int botsNumber = 0;
    private int ticks = 0;

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventSync e) {
            if (mode.is("Motion") || mode.is("UUID")) {
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
        }

        if (event instanceof PacketEvent packet) {
            if (mode.is("Beta")) {
                if (packet.getPacket() instanceof PlayerListS2CPacket listPacket) {
                    checkPlayerAfterSpawn(listPacket);
                } else if (packet.getPacket() instanceof PlayerRemoveS2CPacket removePacket) {
                    removePlayerBecauseLeftServer(removePacket);
                }
            }
        }
        if (event instanceof TickEvent tick) {
            if (mode.is("Beta")) {
                if (!suspectSet.isEmpty()) {
                    mc.world.getPlayers().stream()
                            .filter(player -> suspectSet.contains(player.getUuid()))
                            .forEach(this::evaluateSuspectPlayer);
                }
            }
        }
    }
    @Override
    public void onDisable() {
        reset();
        bots.clear();
        botsNumber = 0;
        ticks = 0;
        timer.reset();
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




    private void checkPlayerAfterSpawn(PlayerListS2CPacket listS2CPacket) {
        listS2CPacket.getPlayerAdditionEntries().forEach(entry -> {
            GameProfile profile = entry.profile();
            if (profile == null || isRealPlayer(entry, profile)) {
                return;
            }

            if (isDuplicateProfile(profile)) {
                botSet.add(profile.getId());
            } else {
                suspectSet.add(profile.getId());
            }
        });
    }

    private void removePlayerBecauseLeftServer(PlayerRemoveS2CPacket removeS2CPacket) {
        removeS2CPacket.profileIds().forEach(uuid -> {
            suspectSet.remove(uuid);
            botSet.remove(uuid);
        });
    }

    private boolean isRealPlayer(PlayerListS2CPacket.Entry entry, GameProfile profile) {
        return entry.latency() < 2 || (profile.getProperties() != null && !profile.getProperties().isEmpty());
    }


    private void evaluateSuspectPlayer(PlayerEntity player) {
        Iterable<ItemStack> armor = null;

        if (!isFullyEquipped(player)) {
            armor = player.getArmorItems();
        }
        if ((isFullyEquipped(player) || hasArmorChanged(player, armor))) {
            botSet.add(player.getUuid());
        }
        suspectSet.remove(player.getUuid());
    }

    public boolean isDuplicateProfile(GameProfile profile) {
        return mc.getNetworkHandler().getPlayerList().stream()
                .filter(player -> player.getProfile().getName().equals(profile.getName()) && !player.getProfile().getId().equals(profile.getId()))
                .count() == 1;
    }

    public boolean isFullyEquipped(PlayerEntity entity) {
        return IntStream.rangeClosed(0, 3)
                .mapToObj(entity.getInventory()::getArmorStack)
                .allMatch(stack -> stack.getItem() instanceof ArmorItem && !stack.hasEnchantments());
    }

    public boolean hasArmorChanged(PlayerEntity entity, Iterable<ItemStack> prevArmor) {
        if (prevArmor == null) {
            return true;
        }

        List<ItemStack> currentArmorList = StreamSupport.stream(entity.getArmorItems().spliterator(), false).toList();
        List<ItemStack> prevArmorList = StreamSupport.stream(prevArmor.spliterator(), false).toList();

        return !IntStream.range(0, Math.min(currentArmorList.size(), prevArmorList.size()))
                .allMatch(i -> currentArmorList.get(i).equals(prevArmorList.get(i))) || currentArmorList.size() != prevArmorList.size();
    }

    public static boolean isBot(LivingEntity entity) {
        return Minced.getInstance().getModuleHandler().get(AntiBot.class).mode.is("Beta") ? botSet.contains(entity.getUuid()) : AntiBot.bots.contains(entity);

    }

    public void reset() {
        suspectSet.clear();
        botSet.clear();
    }
}
