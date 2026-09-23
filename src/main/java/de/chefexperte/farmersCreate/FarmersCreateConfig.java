package de.chefexperte.farmersCreate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FarmersCreateConfig {

    public static final String REQUIRE_CONTAINER = "requireContainer";
    public static final String DAMAGE_TOOLS = "damageTools";
    public static final String FARMERS_DELIGHT_ROLLS = "farmersDelightRolls";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static volatile FarmersCreateConfig instance;

    public final boolean requireContainer;
    public final boolean damageTools;
    public final boolean farmersDelightRolls;

    private FarmersCreateConfig(boolean requireContainer, boolean damageTools, boolean farmersDelightRolls) {
        this.requireContainer = requireContainer;
        this.damageTools = damageTools;
        this.farmersDelightRolls = farmersDelightRolls;
    }

    public static FarmersCreateConfig get() {
        FarmersCreateConfig config = instance;
        if (config == null) {
            synchronized (FarmersCreateConfig.class) {
                config = instance;
                if (config == null) {
                    config = load(FabricLoader.getInstance().getConfigDir().resolve(FarmersCreate.MOD_ID + ".json"));
                    instance = config;
                }
            }
        }
        return config;
    }

    private static FarmersCreateConfig load(Path path) {
        JsonObject json = new JsonObject();
        boolean writable = true;
        if (Files.isRegularFile(path)) {
            try {
                JsonElement parsed = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8));
                if (parsed.isJsonObject()) {
                    json = parsed.getAsJsonObject();
                } else {
                    FarmersCreate.LOGGER.warn("{} is not a JSON object, using the defaults", path);
                    writable = false;
                }
            } catch (IOException | RuntimeException e) {
                FarmersCreate.LOGGER.warn("Could not read {}, using the defaults: {}", path, e.toString());
                writable = false;
            }
        }
        boolean complete = json.has(REQUIRE_CONTAINER) && json.has(DAMAGE_TOOLS) && json.has(FARMERS_DELIGHT_ROLLS);
        FarmersCreateConfig config = new FarmersCreateConfig(
                read(json, REQUIRE_CONTAINER),
                read(json, DAMAGE_TOOLS),
                read(json, FARMERS_DELIGHT_ROLLS));
        if (writable && !complete) {
            write(path, config);
        }
        return config;
    }

    private static boolean read(JsonObject json, String key) {
        JsonElement value = json.get(key);
        if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isBoolean()) {
            return true;
        }
        return value.getAsBoolean();
    }

    private static void write(Path path, FarmersCreateConfig config) {
        JsonObject json = new JsonObject();
        json.addProperty(REQUIRE_CONTAINER, config.requireContainer);
        json.addProperty(DAMAGE_TOOLS, config.damageTools);
        json.addProperty(FARMERS_DELIGHT_ROLLS, config.farmersDelightRolls);
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(json) + System.lineSeparator(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            FarmersCreate.LOGGER.warn("Could not write {}: {}", path, e.toString());
        }
    }
}
