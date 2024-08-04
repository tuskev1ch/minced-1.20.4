package free.minced.modules.impl.display;

import lombok.Getter;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import org.lwjgl.glfw.GLFW;

@Getter
@ModuleDescriptor(name = "ClickGUI", category = ModuleCategory.DISPLAY, hidden = true, key = GLFW.GLFW_KEY_RIGHT_SHIFT)
public class ClickGUI extends Module {

}