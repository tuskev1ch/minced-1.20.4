package free.minced.systems;


import net.minecraft.client.MinecraftClient;
import free.minced.Minced;
import free.minced.primary.IHolder;

import java.io.File;

public class FileHandler implements IHolder {


    public static File DIRECTORY = null;

    // initializes the main client directory
    public void initDirectory() {
        File dir = MinecraftClient.getInstance() == null ? new File("WTF") : MinecraftClient.getInstance().runDirectory;
        DIRECTORY = new File(dir, Minced.NAME);
        if (!DIRECTORY.exists()) {
            DIRECTORY.mkdirs();
        }
    }

}

