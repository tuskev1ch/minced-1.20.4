package free.minced.modules.impl.misc;

import free.minced.events.Event;
import free.minced.events.impl.input.EventClickSlot;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.systems.setting.impl.NumberSetting;
import net.minecraft.item.Item;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

import static free.minced.primary.other.KeyHandler.isKeyPressed;

@ModuleDescriptor(name = "ItemScroller", category = ModuleCategory.MISC)
    public class ItemScroller extends Module {
        public final NumberSetting delay = new NumberSetting("Delay", this, 80f, 0, 500f, 1f);
        private boolean pauseListening = false;

        @Override
        public void onEvent(Event event) {
            if (event instanceof EventClickSlot e) {
                if ((isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) || isKeyPressed(GLFW.GLFW_KEY_RIGHT_SHIFT))
                        && (isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL) || isKeyPressed(GLFW.GLFW_KEY_RIGHT_CONTROL))
                        && e.getSlotActionType() == SlotActionType.THROW
                        && !pauseListening) {
                    Item copy = mc.player.currentScreenHandler.slots.get(e.getSlot()).getStack().getItem();
                    pauseListening = true;
                    for (int i2 = 0; i2 < mc.player.currentScreenHandler.slots.size(); ++i2) {
                        if (mc.player.currentScreenHandler.slots.get(i2).getStack().getItem() == copy)
                            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i2, 1, SlotActionType.THROW, mc.player);
                    }
                    pauseListening = false;
                }
            }
        }
    }