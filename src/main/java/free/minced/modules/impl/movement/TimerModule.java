package free.minced.modules.impl.movement;

import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.systems.setting.impl.NumberSetting;
import lombok.Getter;

@ModuleDescriptor(name = "Timer", category = ModuleCategory.MOVEMENT)
public class TimerModule extends Module {

    public final NumberSetting speed = new NumberSetting("Speed", this, 0.5, 0.1, 10, 0.1);



}
