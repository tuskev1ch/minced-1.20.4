package free.minced.systems.command;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.Formatting;
import free.minced.primary.IHolder;
import free.minced.primary.chat.ChatHandler;
import free.minced.systems.command.api.CommandInfo;

@Getter
@Setter
public abstract class Command implements IHolder {

    private final String displayName, description;
    private final String[] aliases;
    private final CommandInfo annotation = this.getClass().getAnnotation(CommandInfo.class);


    public Command() {
        this.displayName = annotation.name();
        this.description = annotation.description();
        this.aliases = annotation.aliases();

    }

    public abstract void execute(String[] args);

    /**
     * Отправляет сообщение об ошибке в чат
     */

    protected final void error() {
        ChatHandler.display(Formatting.RED + "Invalid command usage." + Formatting.RESET);
    }

    /**
     * Отправляет сообщение об ошибке с правильным вариантом использования команды
     */
    protected final void error(String usage) {
        error();
        ChatHandler.display("Correct Usage: " + usage);
    }


    /**
     * Используется для упрощенного доступа к утилке
     * @param message сообщение, которое будет отправлено
     */
    protected final void send(String message) {
        ChatHandler.display(message);
    }
}