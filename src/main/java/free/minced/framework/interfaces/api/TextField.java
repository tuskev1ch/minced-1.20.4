package free.minced.framework.interfaces.api;


import net.minecraft.client.gui.DrawContext;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.screen.Screen;
import free.minced.Minced;
import free.minced.primary.math.MathHandler;
import free.minced.framework.render.DrawHandler;
import free.minced.framework.color.ClientColors;
import free.minced.framework.font.CFontRenderer;
import org.lwjgl.glfw.GLFW;


@Getter @Setter
public class TextField extends CustomElement {

    public static final String CHARS = "?ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
            + "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя"
            + "0123456789"
            + "!@#$%^&*()-_=+[]{}|\\;:'\"<>,./`~"
            + "©™® "; // по клавиатуре ебашил?

    private String text = "";
    private final CFontRenderer font;
    private final String emptyString;
    private final boolean centeredString;
    private boolean focused;
    private float alpha = 1;
    private float cornerRadius = 2;
    private float textPosX, textPosY, normalCursorPosition, animatedCursorPosition;

    /**
     * Конструктор элемента.
     *
     * @param x              позиция элемента по Х
     * @param y              позиция элемента по Y
     * @param width          ширина элемента
     * @param height         высота элемента
     * @param font           шрифт, который будет использоваться для отображения текста
     * @param emptyString    текст, который будет отображаться когда поле пустое.
     * @param centeredString отцентрировать ли текст.
     */
    public TextField(float x, float y, float width, float height, CFontRenderer font, String emptyString, boolean centeredString) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.font = font;
        this.emptyString = emptyString;
        this.centeredString = centeredString;
    }

    @Override
    public void init() {
        setFocused(false);
        super.init();
    }

    @Override
    public void render(DrawContext pDrawContext, int mouseX, int mouseY) {


        normalCursorPosition = Math.min(Math.max(normalCursorPosition, 0), this.text.length());

        // бг
        float padding = 10;

        DrawHandler.drawRound(pDrawContext.getMatrices(), x , y, width + padding, height,3, ClientColors.getBackgroundColor().withAlpha(165 * alpha));

        StringBuilder drawnString = new StringBuilder(this.text);

        // обновляем позицию отображаемой строки по X
        if (centeredString) {
            // получаем ширину "пустого строки" если в текстовом поле ничего не написано, иначе - получаем ширину отображаемой строки
            float fontWidth = font.getStringWidth(this.text.isEmpty() ? this.emptyString : drawnString.toString()) / 2F;
            textPosX = MathHandler.lerp(textPosX, x + width / 2f - fontWidth + 5, 0.5F);
        } else {
            textPosX = x;
        }

        // обновляем позицию отображаемой строки по Y XYETA
        this.textPosY = y + 7.5f;

        // отображаем "пустую строку"
        if (alpha > 0.1) {
            if (text.isEmpty()) {
                this.font.drawString(pDrawContext.getMatrices(), emptyString, textPosX, textPosY, ClientColors.getFontColor().withAlpha(isFocused() ? 150 * alpha : 100 * alpha).getRGB());
            } else { // если все же в поле, что-то написано - отображаем обычную строку
                this.font.drawString(pDrawContext.getMatrices(), drawnString.toString(), textPosX, textPosY, ClientColors.getFontColor().withAlpha(isFocused() ? 255 * alpha : 200 * alpha).getRGB());
            }
        }

        // позиция "курсора" aka палочки ебаной
        normalCursorPosition = Math.min(Math.max(normalCursorPosition, 0), drawnString.length());

        final StringBuilder textBeforeCursor = new StringBuilder();
        for (int i = 0; i < this.normalCursorPosition; ++ i) {
            textBeforeCursor.append(drawnString.charAt(i));
        }

        float cursorOffset = this.font.getStringWidth(textBeforeCursor.toString());

        animatedCursorPosition = MathHandler.lerp(animatedCursorPosition, cursorOffset, 0.5F);

        // рендерим "курсор"
        if (alpha > 0.1) {
            font.drawString(pDrawContext.getMatrices(), System.currentTimeMillis() % 1000 >= 500 ? "" : "|", textPosX + animatedCursorPosition, textPosY, ClientColors.getFontColor().withAlpha(255 * alpha).getRGB());
        }
        super.render(pDrawContext, mouseX, mouseY);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {

        // меняем состояние элемента при клике на него
        boolean overElement = isHovered(x, y, width, height, mouseX, mouseY);
        if (! isFocused() && overElement && button == 0) {
            setFocused(true);
        }

        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {

        // комбинации клавиш не будут работать если элемент не активен
        if (! isFocused()) {
            return;
        }

        // позволяем удалять текст с поля нажатием на BACKSPACE
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            removeText(normalCursorPosition);
            normalCursorPosition--;
            // очищаем строку нажатием на DELETE
        } else if (keyCode == GLFW.GLFW_KEY_DELETE) {
            setText("");
            // позволяем передвигаться нажатием на стрелочки (влево)
        } else if (keyCode == GLFW.GLFW_KEY_LEFT) {
            normalCursorPosition--;
            // позволяем передвигаться нажатием на стрелочки (вправо)
        } else if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            normalCursorPosition++;
            // позволяем вставлять текст в поле сочетанием клавиш CTRL + V
        } else if (Screen.isPaste(keyCode)) {
            String clipboardString = mc.keyboard.getClipboard();
            if (font.getStringWidth(text + clipboardString) >= width) {
                System.out.println("too long");
                return;
            } else {
                addText(clipboardString, normalCursorPosition);
                normalCursorPosition += clipboardString.length();
            }
            // позволяем копировать текст с поля сочетанием клавиш CTRL + C
        } else if (Screen.isCopy(keyCode)) {
            mc.keyboard.setClipboard(text);
            //Minced.getInstance().getNotificationHandler().send(NotificationType.SUCCESS,3000, "copied text from text field");
            // позволяем переходить в начало текста нажатием на HOME
        } else if (keyCode == GLFW.GLFW_KEY_HOME) {
            normalCursorPosition -= text.length();
            // позволяем переходить в конец текста нажатием на END
        } else if (keyCode == GLFW.GLFW_KEY_END) {
            normalCursorPosition += text.length();
        }

        super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
        if (! isFocused() || font.getStringWidth(this.text) > width) return;

        String character = String.valueOf(codePoint);

        normalCursorPosition = Math.min(Math.max(normalCursorPosition, 0), this.text.length());

        // позволяет писать символы в поле.
        if (CHARS.contains(character) && !character.contains("?")) {
            addText(character, normalCursorPosition);
            // обновляем позицию курсора
            normalCursorPosition++;
        }

        super.charTyped(codePoint, modifiers);
    }

    private void removeText(float position) {
        final StringBuilder newText = new StringBuilder();
        for (int i = 0; i < this.text.length(); ++ i) {
            final String character = String.valueOf(this.text.charAt(i));

            if (i != position - 1) {
                newText.append(character);
            }
        }

        this.text = newText.toString();
    }

    private void addText(String text, float position) {
        if (font.getStringWidth(this.text + text) <= width) {
            final StringBuilder newText = new StringBuilder();

            boolean append = false;
            for (int i = 0; i < this.text.length(); i++) {
                final String character = String.valueOf(this.text.charAt(i));

                if (i == position) {
                    append = true;
                    newText.append(text);
                }

                newText.append(character);
            }

            if (! append) {
                newText.append(text);
            }

            this.text = newText.toString();
        }
    }
}