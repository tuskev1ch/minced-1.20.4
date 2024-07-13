package free.minced.modules.impl.misc;

import lombok.Getter;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.systems.setting.impl.BooleanSetting;
import free.minced.systems.setting.impl.MultiBoxSetting;

@Getter
@ModuleDescriptor(name = "NoInteract", category = ModuleCategory.MISC)
public class NoInteract extends Module {

}
