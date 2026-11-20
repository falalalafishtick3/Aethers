package dev.aether.config;

import com.google.gson.*;
import dev.aether.module.*;
import dev.aether.setting.Setting;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.*;

public final class ConfigManager {
    private static final Path DEFAULT = FabricLoader.getInstance().getConfigDir().resolve("aether.json");
    private static Path current = DEFAULT;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void save() {
        try {
            JsonObject root = new JsonObject();
            for (ClientModule m : ModuleManager.MODULES) {
                JsonObject o = new JsonObject(), s = new JsonObject();
                o.addProperty("enabled", m.enabled);
                o.addProperty("key", m.key);
                o.addProperty("favorite", m.favorite);
                for (Setting<?> st : m.settings) s.add(st.name, st.save());
                o.add("settings", s);
                root.add(m.name, o);
            }
            Files.writeString(current, GSON.toJson(root));
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void load() { load(current); }

    private static void load(Path file) {
        if (!Files.exists(file)) return;
        try {
            JsonObject root = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
            for (ClientModule m : ModuleManager.MODULES) {
                if (!root.has(m.name)) continue;
                JsonObject o = root.getAsJsonObject(m.name);
                if (o.has("enabled")) m.enabled = o.get("enabled").getAsBoolean();
                if (o.has("key")) m.key = o.get("key").getAsInt();
                if (o.has("favorite")) m.favorite = o.get("favorite").getAsBoolean();
                JsonObject s = o.getAsJsonObject("settings");
                if (s != null) for (Setting<?> st : m.settings) if (s.has(st.name)) st.load(s.get(st.name));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    /** Switch to a named profile (e.g. a server address). Missing profiles inherit the current settings. */
    public static void useProfile(String name) {
        save();
        boolean[] before = new boolean[ModuleManager.MODULES.size()];
        for (int i = 0; i < before.length; i++) before[i] = ModuleManager.MODULES.get(i).enabled;
        current = name == null ? DEFAULT : FabricLoader.getInstance().getConfigDir().resolve("aether-" + name.replaceAll("[^a-zA-Z0-9._-]", "_") + ".json");
        load(current);
        // run enable/disable hooks for modules whose state changed
        for (int i = 0; i < before.length; i++) {
            ClientModule m = ModuleManager.MODULES.get(i);
            if (m.enabled != before[i]) { if (m.enabled) m.onEnable(); else m.onDisable(); }
        }
    }
}
