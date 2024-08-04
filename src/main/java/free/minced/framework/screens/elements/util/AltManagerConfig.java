package free.minced.framework.screens.elements.util;

import com.google.gson.*;
import free.minced.Minced;
import free.minced.framework.screens.elements.AccountElement;
import free.minced.framework.screens.elements.AltManagerElement;
import free.minced.primary.IHolder;
import free.minced.systems.FileHandler;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AltManagerConfig implements IHolder {

    private static final File FILE = new File(FileHandler.DIRECTORY, "Minced/accounts.mncd");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();


    /**
     * Сохраняет аккаунты в файлик
     */
    public static void saveAccounts() {
        if (!FILE.exists()) {
            FILE.getParentFile().mkdirs();
        }
        try {
            FileWriter fileWriter = new FileWriter(FILE);
            JsonArray accountsArray = new JsonArray();

            for (AccountElement account : AltManagerElement.ACCOUNTS) {
                JsonObject accountObject = new JsonObject();
                accountObject.addProperty("Username", account.getUsername());
                accountsArray.add(accountObject);
            }

            JsonObject object = new JsonObject();
            object.add("Accounts", accountsArray);

            fileWriter.write(GSON.toJson(object));

            fileWriter.flush();
            fileWriter.close();
        } catch (IOException exception) {
            exception.printStackTrace();
            Minced.LOGGER.error("[ACCOUNT-MANAGER] Error while saving accounts.");
        }
    }

    /**
     * Читает файлик и добавляет аккаунты в список
     */
    public static void loadAccounts() {
        try {
            if (FILE.exists()) {
                FileReader fileReader = new FileReader(FILE);
                JsonObject object = GSON.fromJson(fileReader, JsonObject.class);

                if (object != null && object.has("Accounts")) {
                    JsonArray accountsArray = object.getAsJsonArray("Accounts");

                    for (JsonElement element : accountsArray) {
                        if (element.isJsonObject()) {
                            JsonObject accountObject = element.getAsJsonObject();
                            String username = accountObject.get("Username").getAsString();

                            // Create a new account object and add it to AltManagerElement.ACCOUNTS
                            AccountElement newAccount = new AccountElement(username);
                            AltManagerElement.ACCOUNTS.add(newAccount);
                        }
                    }
                }

                fileReader.close();
            }
        } catch (Exception e) {
            Minced.LOGGER.error("[ACCOUNT-MANAGER] Error while loading accounts.");
            e.printStackTrace();
        }
    }
}