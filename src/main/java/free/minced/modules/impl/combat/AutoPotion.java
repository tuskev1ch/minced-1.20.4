package free.minced.modules.impl.combat;


import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.Hand;
import free.minced.events.Event;
import free.minced.events.impl.player.EventAfterRotate;
import free.minced.events.impl.player.EventPostSync;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.primary.IHolder;
import free.minced.primary.time.TimerHandler;
import free.minced.systems.setting.impl.BooleanSetting;

@ModuleDescriptor(name = "AutoPotion", category = ModuleCategory.COMBAT)

public class AutoPotion extends Module {
    // тут пиздец коду)) зато заработал
    private final BooleanSetting autoOff = new BooleanSetting("Auto Off", this, true);

    public final TimerHandler timer = new TimerHandler();
    private boolean spoofed = false;
    float rotprev;

    private int findPotionSlot(int id) {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.getInventory().getStack(i);

            if (stack.getItem() == Items.SPLASH_POTION) {

                for (StatusEffectInstance effect1 : PotionUtil.getPotionEffects(stack)) {
                    StatusEffect id2 = null;

                    switch (id) {
                        case 5 -> id2 = StatusEffects.STRENGTH;
                        case 1 -> id2 = StatusEffects.SPEED;
                        case 12 -> id2 = StatusEffects.FIRE_RESISTANCE;
                    }

                    if (effect1.getEffectType() == id2) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    private boolean canBuff(StatusEffect effect, int id) {
        if (mc.player.hasStatusEffect(effect)) return false; // Если зелье уже забафанно, то скипаем

        int slot = findPotionSlot(id); // Ищем слот с зельем

        return slot != -1; // Если слот не найден скипаем
// Если всё заебись то возвращаем true
    }

    private boolean canBuff() {
        return (canBuff(StatusEffects.STRENGTH, 5) || canBuff(StatusEffects.SPEED, 1) || canBuff(StatusEffects.FIRE_RESISTANCE, 12)) && mc.player.isOnGround() && timer.passed(1000);
    }


    @Override
    public void onEvent(Event e) {
        if (e instanceof EventAfterRotate eventAfterRotate) {

            if ( shouldThrow() ) {
                rotprev = mc.player.getPitch();
                mc.player.setPitch(90);
                spoofed = true;
            }
        }
        if (e instanceof EventPostSync) {


            if (shouldThrow() && spoofed) {

                handlePotion(StatusEffects.STRENGTH, 5); // сила
                handlePotion(StatusEffects.SPEED, 1); // скорость
                handlePotion(StatusEffects.FIRE_RESISTANCE, 12); // огнестойкость

                IHolder.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
                mc.player.setPitch(rotprev);
                timer.reset();
                spoofed = false;
                if (this.autoOff.isEnabled()) this.toggle();
            }
        }
    }

    private boolean shouldThrow() {
        return (canBuff() && mc.world.getBlockState(mc.player.getBlockPos().add(0, -1, 0)).getBlock() != Blocks.AIR);
    }

    private void handlePotion(StatusEffect effect, int id) {
        if (mc.player.hasStatusEffect(effect)) return; // Если зелье уже забафанно, то скипаем

        int slot = findPotionSlot(id); // Ищем слот с зельем

        if (slot == -1) return; // Если слот не найден скипаем

        IHolder.sendPacket(new UpdateSelectedSlotC2SPacket(slot));

        IHolder.sendSequencedPacket(id2 -> new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, id2));
    }
}


