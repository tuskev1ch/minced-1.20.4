package free.minced.framework.render;




public class GuiHandler {

    /**
     * Вычисляет среднюю точку коробки с заданной высотой объекта и высотой коробки.
     *
     * @param objectHeight высота объекта внутри коробки
     * @param boxHeight    высота коробки
     */
    public static float getMiddleOfBox(float objectHeight, float boxHeight) {
        return (boxHeight / 2f - objectHeight / 2f);
    }

    /**
     * Проверяет, находится ли мышь внутри заданного квадрата
     *
     * @param x      Х позиция квадрата
     * @param y      Y позиция квадрата
     * @param width  Ширина квадрата
     * @param height высота квадрата
     * @param mouseX Текущая X позиция мыши
     * @param mouseY Текущая Y позиция мыши
     */
    public static boolean isHovered(final double x, final double y, final double width, final double height, final int mouseX, final int mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    /**
     * Проверяет, находится ли мышь внутри заданного квадрата
     *
     * @param x      Х позиция квадрата
     * @param y      Y позиция квадрата
     * @param width  Ширина квадрата
     * @param height высота квадрата
     * @param mouseX Текущая X позиция мыши
     * @param mouseY Текущая Y позиция мыши
     */

    public static boolean isHovered(final double x, final double y, final double width, final double height, final double mouseX, final double mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}