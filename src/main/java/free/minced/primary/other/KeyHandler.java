package free.minced.primary.other;


import net.minecraft.client.util.InputUtil;
import free.minced.primary.IHolder;
import org.lwjgl.glfw.GLFW;


public class KeyHandler implements IHolder {

    public static int getKeyIndex(String key) {
        if (key.equalsIgnoreCase("none"))
            return 0;
        try {
            return GLFW.class.getDeclaredField("GLFW_KEY_" + key.toUpperCase()).getInt(null);
        } catch (Exception e) {
            return -1;
        }
    }
    public static boolean isKeyDown(int key) {
        if (key == -1)
            return false;
        return InputUtil.isKeyPressed(mc.getWindow().getHandle(), key);
    }
    public static boolean isMouseButtonPressed(int button) {
        return GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), button) == GLFW.GLFW_PRESS;
    }
    public static boolean isKeyPressed(int key) {
        return GLFW.glfwGetKey(mc.getWindow().getHandle(), key) == GLFW.GLFW_PRESS;
    }

    public static String getKeyboardKey(int key) {
        if (key == -1) {
            return "None";
        } else if (key == 1) {
            return "Right Mouse";
        } else if (key == 2) {
            return "Middle Mouse";
        } else if (key == 3) {
            return "Mouse 4";
        } else if (key == 4) {
            return "Mouse 5";
        } else if (key == 32) {
            return "Space";
        } else if (key == 39) {
            return "Apostrophe";
        } else if (key == 44) {
            return "Comma";
        } else if (key == 45) {
            return "Minus";
        } else if (key == 46) {
            return "Period";
        } else if (key == 47) {
            return "Slash";
        } else if (key == 48) {
            return "0";
        } else if (key == 49) {
            return "1";
        } else if (key == 50) {
            return "2";
        } else if (key == 51) {
            return "3";
        } else if (key == 52) {
            return "4";
        } else if (key == 53) {
            return "5";
        } else if (key == 54) {
            return "6";
        } else if (key == 55) {
            return "7";
        } else if (key == 56) {
            return "8";
        } else if (key == 57) {
            return "9";
        } else if (key == 59) {
            return "SemiColon";
        } else if (key == 61) {
            return "Equal";
        } else if (key == 65) {
            return "A";
        } else if (key == 66) {
            return "B";
        } else if (key == 67) {
            return "C";
        } else if (key == 68) {
            return "D";
        } else if (key == 69) {
            return "E";
        } else if (key == 70) {
            return "F";
        } else if (key == 71) {
            return "G";
        } else if (key == 72) {
            return "H";
        } else if (key == 73) {
            return "I";
        } else if (key == 74) {
            return "J";
        } else if (key == 75) {
            return "K";
        } else if (key == 76) {
            return "L";
        } else if (key == 77) {
            return "M";
        } else if (key == 78) {
            return "N";
        } else if (key == 79) {
            return "O";
        } else if (key == 80) {
            return "P";
        } else if (key == 81) {
            return "Q";
        } else if (key == 82) {
            return "R";
        } else if (key == 83) {
            return "S";
        } else if (key == 84) {
            return "T";
        } else if (key == 85) {
            return "U";
        } else if (key == 86) {
            return "V";
        } else if (key == 87) {
            return "W";
        } else if (key == 88) {
            return "X";
        } else if (key == 89) {
            return "Y";
        } else if (key == 90) {
            return "Z";
        } else if (key == 91) {
            return "LeftBracket";
        } else if (key == 92) {
            return "BackSlash";
        } else if (key == 93) {
            return "RightBracket";
        } else if (key == 96) {
            return "Grave";
        } else if (key == 161) {
            return "World1";
        } else if (key == 162) {
            return "World2";
        } else if (key == 256) {
            return "Escape";
        } else if (key == 257) {
            return "Enter";
        } else if (key == 258) {
            return "Tab";
        } else if (key == 259) {
            return "BackSpace";
        } else if (key == 260) {
            return "Insert";
        } else if (key == 261) {
            return "Delete";
        } else if (key == 262) {
            return "Right";
        } else if (key == 263) {
            return "Left";
        } else if (key == 264) {
            return "Down";
        } else if (key == 265) {
            return "Up";
        } else if (key == 266) {
            return "PageUp";
        } else if (key == 267) {
            return "PageDown";
        } else if (key == 268) {
            return "Home";
        } else if (key == 269) {
            return "End";
        } else if (key == 280) {
            return "Caps Lock";
        } else if (key == 281) {
            return "ScrollLock";
        } else if (key == 282) {
            return "NumLock";
        } else if (key == 283) {
            return "PrintScreen";
        } else if (key == 284) {
            return "Pause";
        } else if (key == 290) {
            return "F1";
        } else if (key == 291) {
            return "F2";
        } else if (key == 292) {
            return "F3";
        } else if (key == 293) {
            return "F4";
        } else if (key == 294) {
            return "F5";
        } else if (key == 295) {
            return "F6";
        } else if (key == 296) {
            return "F7";
        } else if (key == 297) {
            return "F8";
        } else if (key == 298) {
            return "F9";
        } else if (key == 299) {
            return "F10";
        } else if (key == 300) {
            return "F11";
        } else if (key == 301) {
            return "F12";
        } else if (key == 302) {
            return "F13";
        } else if (key == 303) {
            return "F14";
        } else if (key == 304) {
            return "F15";
        } else if (key == 305) {
            return "F16";
        } else if (key == 306) {
            return "F17";
        } else if (key == 307) {
            return "F18";
        } else if (key == 308) {
            return "F19";
        } else if (key == 309) {
            return "F20";
        } else if (key == 310) {
            return "F21";
        } else if (key == 311) {
            return "F22";
        } else if (key == 312) {
            return "F23";
        } else if (key == 313) {
            return "F24";
        } else if (key == 314) {
            return "F25";
        } else if (key == 320) {
            return "NUM 0";
        } else if (key == 321) {
            return "NUM 1";
        } else if (key == 322) {
            return "NUM 2";
        } else if (key == 323) {
            return "NUM 3";
        } else if (key == 324) {
            return "NUM 4";
        } else if (key == 325) {
            return "NUM 5";
        } else if (key == 326) {
            return "NUM 6";
        } else if (key == 327) {
            return "NUM 7";
        } else if (key == 328) {
            return "NUM 8";
        } else if (key == 329) {
            return "NUM 9";
        } else if (key == 330) {
            return "Deci";
        } else if (key == 331) {
            return "Div";
        } else if (key == 332) {
            return "Mul";
        } else if (key == 333) {
            return "Sub";
        } else if (key == 334) {
            return "Add";
        } else if (key == 335) {
            return "Enter";
        } else if (key == 336) {
            return "Equal";
        } else if (key == 340) {
            return "Shift";
        } else if (key == 341) {
            return "LCtrl";
        } else if (key == 342) {
            return "LAlt";
        } else if (key == 343) {
            return "LSuper";
        } else if (key == 344) {
            return "RShift";
        } else if (key == 345) {
            return "RCtrl";
        } else if (key == 346) {
            return "RAlt";
        } else if (key == 347) {
            return "RSuper";
        } else if (key == 348) {
            return "Menu";
        } else if (key == 0) {
            return "None";
        }else {
            return "error";
        }
    }
}
