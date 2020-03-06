package org.samo_lego.commandspy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class SpyConfig {
    public static class MainConfig {
        // Whether player commands should be logged
        public boolean logPlayerCommands = true;
        // Whether UUID should be appended to player's name
        public boolean appendUUID = false;

        // Whether commands executed by command blocks should be logged
        public boolean logCommandBlockCommands = true;
    }
    public static ArrayList<String> blacklistedCommands = new ArrayList<String>(Arrays.asList(
            "login",
            "register",
            "changepw",
            "unregister",
            "auth"
    ));

    public MainConfig main = new MainConfig();

    // Logger and gson initalizing
    private static final Logger LOGGER = (Logger) LogManager.getLogger();
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static SpyConfig loadConfig(File file) {
        SpyConfig config;

        if (file.exists()) {
            try (FileReader fileReader = new FileReader(file)){
                config = gson.fromJson(fileReader, SpyConfig.class);
            } catch (IOException e) {
                throw new RuntimeException("[CommandSpy] An error occured:", e);
            }
        }
        else {
            config = new SpyConfig();
        }
        config.save(file);

        return config;
    }

    private void save(File file) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            gson.toJson(this, fileWriter);
        } catch (IOException e) {
            LOGGER.error("[CommandSpy] An error occured:", e);
        }
    }
}
