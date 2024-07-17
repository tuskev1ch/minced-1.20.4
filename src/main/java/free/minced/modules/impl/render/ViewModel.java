package free.minced.modules.impl.render;

import free.minced.events.Event;
import free.minced.events.impl.render.EventHeldItemRenderer;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.systems.setting.impl.BooleanSetting;
import free.minced.systems.setting.impl.NumberSetting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4x3dc;

@ModuleDescriptor(name = "ViewModel", category = ModuleCategory.RENDER)
public class ViewModel extends Module {

    private final BooleanSetting customScale = new BooleanSetting("Custom Scale", this, false);
    private final NumberSetting customScaleValue = new NumberSetting("Scale Value", this, 1f, 0.1f, 1.5f, 0.1, () -> !customScale.isEnabled());


    public final NumberSetting leftX = new NumberSetting("Left X", this, 0, -1, 1, 0.1);
    public final NumberSetting leftY = new NumberSetting("Left Y", this, 0, -1, 1, 0.1);
    public final NumberSetting leftZ = new NumberSetting("Left Z", this, 0, -1, 1, 0.1);

    public final NumberSetting rightX = new NumberSetting("Right X", this, 0, -1, 1, 0.1);
    public final NumberSetting rightY = new NumberSetting("Right Y", this, 0, -1, 1, 0.1);
    public final NumberSetting rightZ = new NumberSetting("Right Z", this, 0, -1, 1, 0.1);

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventHeldItemRenderer eventHeldItemRenderer) {
            if (eventHeldItemRenderer.getHand() == Hand.MAIN_HAND) {
                eventHeldItemRenderer.getStack().translate(rightX.getValue().doubleValue(), rightY.getValue().doubleValue(), rightZ.getValue().doubleValue());
                if (customScale.isEnabled()) {
                    eventHeldItemRenderer.getStack().scale(customScaleValue.getValue().floatValue(), customScaleValue.getValue().floatValue(), customScaleValue.getValue().floatValue());
                }
            } else {
                eventHeldItemRenderer.getStack().translate(-leftX.getValue().doubleValue(), leftY.getValue().doubleValue(), leftZ.getValue().doubleValue());
                if (customScale.isEnabled()) {
                    eventHeldItemRenderer.getStack().scale(customScaleValue.getValue().floatValue(), customScaleValue.getValue().floatValue(), customScaleValue.getValue().floatValue());
                }
            }
        }
    }
}