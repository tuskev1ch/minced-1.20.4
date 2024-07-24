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

    final BooleanSetting customScale = new BooleanSetting("Custom Scale", this, false);
    final NumberSetting customScaleValue = new NumberSetting("Scale Value", this, 1f, 0.1f, 1.5f, 0.1, () -> !customScale.isEnabled());


    final NumberSetting leftX = new NumberSetting("Left X", this, 0, -1, 1, 0.1);
    final NumberSetting leftY = new NumberSetting("Left Y", this, 0, -1, 1, 0.1);
    final NumberSetting leftZ = new NumberSetting("Left Z", this, 0, -1, 1, 0.1);

    final NumberSetting rightX = new NumberSetting("Right X", this, 0, -1, 1, 0.1);
    final NumberSetting rightY = new NumberSetting("Right Y", this, 0, -1, 1, 0.1);
    final NumberSetting rightZ = new NumberSetting("Right Z", this, 0, -1, 1, 0.1);
    
}