package free.minced.primary;

import free.minced.primary.chat.ChatHandler;

public class UnknownModuleException extends RuntimeException {

    private final String moduleName;

    public UnknownModuleException(String moduleName) {
        super("%s is not found!".formatted(moduleName));
        this.moduleName = moduleName;
    }

    public void display() {
        ChatHandler.display("%s is not found!".formatted(moduleName) + ", sorry dude for the inconvenience");
    }

}