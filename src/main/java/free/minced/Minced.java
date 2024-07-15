package free.minced;



import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.logging.LogUtils;
import free.minced.addition.ProfileHandler;
import lombok.Getter;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import free.minced.framework.interfaces.InterfaceScreen;
import free.minced.framework.render.shaders.ShaderHandler;
import free.minced.modules.api.ModuleManager;
import free.minced.systems.FileHandler;
import free.minced.systems.command.api.CommandHandler;
import free.minced.systems.config.api.ConfigHandler;
import free.minced.systems.draggable.DraggableHandler;
import free.minced.systems.macros.MacrosHandler;
import free.minced.systems.partner.PartnerHandler;
import free.minced.systems.theme.PrimaryTheme;
import free.minced.systems.theme.Theme;
import free.minced.systems.theme.ThemeHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.management.Notification;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Optional;



@Getter
public class Minced implements ModInitializer {

	private static final Minced INSTANCE = new Minced();

	public static String NAME = "Minced", BUILD = "Free", VERSION = "1.20.4";
	public static String BUILDED_STRING = Minced.NAME + " " + BUILD + " - " + VERSION;
	public static String SITE = "http://mincedclient.ru/";

	public static final Logger LOGGER = LogManager.getLogger(NAME.toUpperCase());

	public static long initTime;
	public static boolean isOutdated;


	private FileHandler fileHandler = new FileHandler();

	private DraggableHandler draggableHandler = new DraggableHandler();
	private ModuleManager moduleHandler = new ModuleManager();
	private ConfigHandler configHandler = new ConfigHandler();;
	private MacrosHandler macrosHandler = new MacrosHandler();

	private CommandHandler commandHandler = new CommandHandler();
	private PartnerHandler partnerHandler = new PartnerHandler();
	private ThemeHandler themeHandler = new ThemeHandler();
	private InterfaceScreen interfaceScreen = new InterfaceScreen();



	boolean initializated = false;

	@Override
	public void onInitialize() {
		if (!initializated) {
			verifyVersion();
			if (isOutdated) {

			}

			if (isLithiumPresent() || isFuturePresent()) { // Работает
				System.out.println("Lithium Or Future detected [minced:BLACKLISTED-MODS]");
				return;
			}
			initTime = System.currentTimeMillis();

			ShaderHandler.initShaders();

			fileHandler.initDirectory();
			moduleHandler.initModules();
			commandHandler.initCommands();

			LogUtils.getLogger().info("[MINCED] Init time: " + (System.currentTimeMillis() - initTime) + " ms.");

			initTime = System.currentTimeMillis();

			initializated = true;
		}
		if (initializated) {
			Minced.getInstance().getConfigHandler().load("autocfg");
		}
	}
	public static void verifyVersion() {
		try {
			String version = new BufferedReader(new InputStreamReader(new URL("https://raw.githubusercontent.com/tuskev1ch/minced-1.20.4/main/latestVersion.txt").openStream())).readLine();
			System.out.println("Current Version From GitHub - %s".formatted(version));
			if (!version.equals(ProfileHandler.getVersion())) {
				System.out.println("Client Outdated");
				isOutdated = true;
			}
		} catch (Exception ignored) {

		}
	}
	public static JsonObject saveData() {
		JsonObject json = new JsonObject();
		try {
			// сохраняем автора конфига
			json.addProperty("theme", getInstance().themeHandler.getTheme().name());
			json.addProperty("primaryTheme", getInstance().themeHandler.getPrimaryTheme().name());

			// сохраняем модули
			json.add("modules", getInstance().moduleHandler.save());

			// сохраняем друзей
			json.add("friends", getInstance().partnerHandler.save());

			// сохраняем макросы
			json.add("macros", getInstance().macrosHandler.save());

			// сохраняем драгабл
			getInstance().draggableHandler.save();

		} catch (Exception e) {
			LOGGER.error("[{}] Иди нахуй", "SAVING-DATA");
			e.printStackTrace();
		}
		return json;
	}

	public static void loadData(JsonObject object) {
		try {
			Optional<Theme> theme = Arrays.stream(Theme.values()).filter(themes -> themes.name().equalsIgnoreCase(object.get("theme").getAsString())).findFirst();
			theme.ifPresent(clientTheme -> getInstance().themeHandler.setTheme(clientTheme));
			// загружаем темы
			Optional<PrimaryTheme> primaryTheme = Arrays.stream(PrimaryTheme.values()).filter(themes -> themes.name().equalsIgnoreCase(object.get("primaryTheme").getAsString())).findFirst();
			primaryTheme.ifPresent(primary -> getInstance().themeHandler.setPrimaryTheme(primary));
			// загружаем модули

			getInstance().moduleHandler.load(object.getAsJsonObject("modules"));

			// загружаем друзей
			getInstance().partnerHandler.load(object.getAsJsonObject("friends"));

			// загружаем драгбл
			getInstance().draggableHandler.load();

			// загружаем макросы
			getInstance().macrosHandler.load(object.getAsJsonObject("macros"));

		} catch (Exception e) {
			LOGGER.error("[{}] Иди нахуй", "LOADING-DATA");
			e.printStackTrace();
		}
	}


	public static boolean isLithiumPresent() {
		return !FabricLoader.getInstance().getModContainer("lithium").isEmpty();
	}
	public static boolean isFuturePresent() {
		return !FabricLoader.getInstance().getModContainer("future").isEmpty();
	}


	public static Minced getInstance() {
		return INSTANCE;
	}
}