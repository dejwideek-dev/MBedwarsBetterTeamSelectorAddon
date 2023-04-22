package pl.dejwideek.teamselector;

import co.aikar.commands.PaperCommandManager;
import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.BedwarsAddon;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.game.lobby.LobbyItem;
import de.marcely.bedwars.api.game.lobby.LobbyItemHandler;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import pl.dejwideek.teamselector.commands.ReloadCmd;
import pl.dejwideek.teamselector.events.InvClickEvent;

@SuppressWarnings("ALL")
public class TeamSelectorAddon extends BedwarsAddon {

    private static TeamSelectorPlugin plugin;

    public TeamSelectorAddon(TeamSelectorPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    public void registerCommands() {
        PaperCommandManager manager =
                new PaperCommandManager(plugin);

        manager.registerCommand(new ReloadCmd(plugin));
    }

    public void registerEvents() {
        PluginManager manager = Bukkit.getPluginManager();

        manager.registerEvents(new InvClickEvent(plugin), plugin);
    }

    public void registerItem() {
        BedwarsAPI.onReady(() -> {
            BedwarsAPI.getGameAPI().registerLobbyItemHandler(
                    new LobbyItemHandler("better-team-selector", plugin) {
                @Override
                public void handleUse(Player player, Arena arena, LobbyItem lobbyItem) {
                    new GUI(plugin).gui(player);
                }

                @Override
                public boolean isVisible(Player player, Arena arena, LobbyItem lobbyItem) {
                    YamlDocument config = plugin.config;
                    boolean isHiddenIfOnePlayerPerTeam =
                            config.getBoolean(
                                    "team-selector.hide-item-if-one-player-per-team");

                    if(isHiddenIfOnePlayerPerTeam) {
                        if(arena.getPlayersPerTeam() == 1) return false;
                        else return true;
                    }
                    else return true;
                }
            });
        });
    }
}
