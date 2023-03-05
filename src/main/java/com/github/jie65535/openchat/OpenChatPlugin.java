/*
 * gc-openchat
 * Copyright (C) 2022  jie65535
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.jie65535.openchat;

import com.github.jie65535.openchat.commands.ChatPlayerCommands;
import com.github.jie65535.openchat.commands.ChatServerCommands;
import com.github.jie65535.openchat.utils.SensitiveWordFilter;
import emu.grasscutter.plugin.Plugin;
import emu.grasscutter.server.event.EventHandler;
import emu.grasscutter.server.event.HandlerPriority;
import emu.grasscutter.server.event.player.PlayerJoinEvent;
import emu.grasscutter.utils.JsonUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

public final class OpenChatPlugin extends Plugin {
    private static OpenChatPlugin instance;
    public static OpenChatPlugin getInstance() {
        return instance;
    }


    @Override
    public void onLoad() {
        instance = this;
        loadConfig();
        loadData();
        loadSensitiveWordList();
        getLogger().info("[OpenChat] Loaded.");
    }

    @Override
    public void onEnable() {
        // Register event listeners.
        new EventHandler<>(PlayerJoinEvent.class)
                .priority(HandlerPriority.NORMAL)
                .listener(EventListeners::onJoin)
                .register(this);

        // Register commands.
        getHandle().registerCommand(new ChatServerCommands());
        getHandle().registerCommand(new ChatPlayerCommands());

        // Set my chat system.
        getServer().setChatSystem(new OpenChatSystem(this));

        // Log a plugin status message.
        getLogger().info("[OpenChat] Enabled, see https://github.com/jie65535/gc-openchat-plugin");
    }

    @Override
    public void onDisable() {
        saveData();
        saveConfig();
        getLogger().info("[OpenChat] Disabled.");
    }


    // region config

    private OpenChatConfig config;
    public OpenChatConfig getConfig() {
        return config;
    }
    public void loadConfig() {
        var configFile = new File(getDataFolder(), "config.json");
        if (!configFile.exists()) {
            config = new OpenChatConfig();
            saveConfig();
        } else {
            try {
                config = JsonUtils.loadToClass(configFile.toPath(), OpenChatConfig.class);
            } catch (Exception exception) {
                config = new OpenChatConfig();
                getLogger().error("[OpenChat] There was an error while trying to load the configuration from config.json. Please make sure that there are no syntax errors. If you want to start with a default configuration, delete your existing config.json.", exception);
            }
        }
    }
    public void saveConfig() {
        var configFile = new File(getDataFolder(), "config.json");
        try (var file = new FileWriter(configFile, StandardCharsets.UTF_8)) {
            file.write(JsonUtils.encode(config));
        } catch (IOException e) {
            getLogger().error("[OpenChat] Unable to write to config file.");
        } catch (Exception e) {
            getLogger().error("[OpenChat] Unable to save config file.");
        }
    }

    // endregion

    // region data

    private OpenChatData data;
    public OpenChatData getData() {
        return data;
    }
    private void loadData() {
        var dataFile = new File(getDataFolder(), "data.json");
        if (!dataFile.exists()) {
            data = new OpenChatData();
            saveData();
        } else {
            try {
                data = JsonUtils.loadToClass(dataFile.toPath(), OpenChatData.class);
            } catch (Exception exception) {
                data = new OpenChatData();
                getLogger().error("[OpenChat] There was an error while trying to load the data from data.json. Please make sure that there are no syntax errors. If you want to start with a default data, delete your existing data.json.", exception);
            }
        }
    }
    public void saveData() {
        try (var file = new FileWriter(new File(getDataFolder(), "data.json"), StandardCharsets.UTF_8)) {
            file.write(JsonUtils.encode(data));
        } catch (IOException e) {
            getLogger().error("[OpenChat] Unable to write to data file.");
        } catch (Exception e) {
            getLogger().error("[OpenChat] Unable to save data file.");
        }
    }

    // endregion

    // region SensitiveWordFilter

    private final SensitiveWordFilter sensitiveWordFilter = new SensitiveWordFilter();

    public SensitiveWordFilter getSensitiveWordFilter() {
        return sensitiveWordFilter;
    }
    private static final String SENSITIVE_WORD_LIST_FILE_NAME = "SensitiveWordList.txt";
    public void loadSensitiveWordList() {
        try {
            var sensitiveWordListFile = new File(getDataFolder(), SENSITIVE_WORD_LIST_FILE_NAME);
            if (!sensitiveWordListFile.exists()) {
                var in = OpenChatPlugin.class.getClassLoader().getResourceAsStream(SENSITIVE_WORD_LIST_FILE_NAME);
                Files.copy(Objects.requireNonNull(in), sensitiveWordListFile.toPath());
                in.close();
            }
            var wordList = Files.readAllLines(sensitiveWordListFile.toPath());
            for (var word : wordList) {
                sensitiveWordFilter.addWord(word);
            }
            getLogger().info("[OpenChat] {} sensitive words loaded", wordList.size());
        } catch (Exception ex) {
            getLogger().error("[OpenChat] Failed to load sensitive word list!", ex);
        }
    }

    // endregion
}
