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
        // Message that is sent when player enters command
        public String playerMessageStyle = "Player ${playername} (UUID: ${uuid}) used command: ${command}";

        // Whether commands executed by command blocks should be logged
        public boolean logCommandBlockCommands = true;
        // Message that is sent when command from command block is executed
        public String commandBlockMessageStyle = "Command block in: ${dimension} at X: ${x} Y: ${y} Z: ${z} executed command: ${command}";
    }

    public MainConfig main = new MainConfig();
    public ArrayList<String> blacklistedCommands = new ArrayList<>(Arrays.asList(
            "msg",
            // From SimpleAuth - ignore || delete those if you don't have the mod installed
            "login",
            "register",
            "changepw",
            "unregister",
            "auth"
    ));

    // Logger and gson initalizing
    private static final Logger LOGGER = (Logger) LogManager.getLogger();

    // GSON initializing with nice printing options :P
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    // Loading config
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

    // Saving config to path "file"
    private void save(File file) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            gson.toJson(this, fileWriter);
        } catch (IOException e) {
            LOGGER.error("[CommandSpy] An error occured:", e);
        }
    }
}
