package pl.dejwideek.teamselector;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import dev.dejvokep.boostedyaml.spigot.SpigotSerializer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("ALL")
public class TeamSelectorPlugin extends JavaPlugin {

    public YamlDocument config;

    public void onEnable() {
        if(!mbwCheck()) return;
        if(!registerAddon()) return;

        loadConfig();
        new TeamSelectorAddon(this).registerCommands();
        new TeamSelectorAddon(this).registerEvents();
        new TeamSelectorAddon(this).registerItem();
    }

    private void loadConfig() {
        try {
            config = YamlDocument.create(
                    new File(new TeamSelectorAddon(this).getDataFolder(), "config.yml"),
                    getResource("config.yml"),
                    GeneralSettings.builder().setSerializer(
                            SpigotSerializer.getInstance()).build(),
                    LoaderSettings.DEFAULT,
                    DumperSettings.DEFAULT,
                    UpdaterSettings.DEFAULT);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private boolean mbwCheck() {
        if(Bukkit.getPluginManager().getPlugin("MBedwars") != null) {
            final int supportedAPIVersion = 24;

            try {
                Class apiClass = Class.forName("de.marcely.bedwars.api.BedwarsAPI");
                int apiVersion = (int) apiClass.getMethod("getAPIVersion").invoke(null);

                if (apiVersion < supportedAPIVersion)
                    throw new IllegalStateException();
            } catch (Exception e) {
                this.getLogger().warning("Your MBedwars version is not supported. Supported version: 5.3 or higher!");
                Bukkit.getPluginManager().disablePlugin(this);
                return false;
            }
        }
        else return false;
        return true;
    }

    private boolean registerAddon() {
        TeamSelectorAddon addon = new TeamSelectorAddon(this);

        if(!addon.register()) {
            this.getLogger().warning("It seems like this addon has already been loaded. Please delete duplicates and try again.");
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }
        return true;
    }
}
