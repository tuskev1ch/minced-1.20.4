package free.minced.framework.screens;

import lombok.Getter;
import lombok.Setter;
import free.minced.framework.render.GuiHandler;
import free.minced.primary.IHolder;

@Getter
@Setter
public class MenuScreen extends GuiHandler implements IHolder {

    public float x, y, width, height;


}