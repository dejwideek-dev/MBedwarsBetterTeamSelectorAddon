package pl.dejwideek.teamselector.events;

import com.cryptomorin.xseries.XSound;
import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.dejwideek.teamselector.TeamSelectorPlugin;
import pl.dejwideek.teamselector.color.ColorAPI;

@SuppressWarnings("ALL")
public class InvClickEvent implements Listener {

    private static TeamSelectorPlugin plugin;

    public InvClickEvent(TeamSelectorPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        YamlDocument config = plugin.config;
        ColorAPI colorAPI = new ColorAPI();

        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getClickedInventory();

        String title = config.getString("menu.title");
        String teamFullMsg = config.getString(
                "messages.team-is-full");
        String alreadyInTeamMsg = config.getString(
                "messages.already-in-team");
        String joinedMsg = config.getString(
                "messages.joined-to-team");
        boolean isTeamFullSoundEnabled = config.getBoolean(
                "menu.sound.team-is-full.enabled");
        String teamFullSound = config.getString(
                "menu.sound.team-is-full.sound").toUpperCase();
        boolean isAlreadyInTeamSoundEnabled = config.getBoolean(
                "menu.sound.already-in-team.enabled");
        String alreadyInTeamSound = config.getString(
                "menu.sound.already-in-team.sound").toUpperCase();
        boolean isJoinedSoundEnabled = config.getBoolean(
                "menu.sound.joined-to-team.enabled");
        String joinedSound = config.getString(
                "menu.sound.joined-to-team.sound").toUpperCase();

        try {
            if(p.getOpenInventory().getTitle()
                    .equals(colorAPI.process(title))) {
                Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(p);

                if(!arena.equals(null)) {
                    ItemStack item = e.getCurrentItem();
                    for(Team team : arena.getEnabledTeams()) {
                        if(item.getItemMeta().getDisplayName()
                                .contains(colorAPI.process(config.getString(
                                        "menu.team-display-names." + team.name())))) {
                            int playersPerTeam = arena.getPlayersPerTeam();
                            int playersInTeam = arena
                                    .getPlayersInTeam(team).size();

                            try {
                                Team playerTeam = arena.getPlayerTeam(p);
                                if(!playerTeam.equals(team)) {
                                    if(playersInTeam == playersPerTeam) {
                                        p.sendMessage(colorAPI.process(teamFullMsg));
                                        if(isTeamFullSoundEnabled)
                                            p.playSound(p.getLocation(), XSound.valueOf(
                                                            teamFullSound).parseSound(),
                                                    50.0f, 1.0f);
                                    }
                                    else {
                                        arena.setPlayerTeam(p, team);
                                        p.sendMessage(colorAPI.process(joinedMsg
                                                .replaceAll("%team%",
                                                        config.getString("menu.team-display-names."
                                                                + team.name()))));
                                        if(isJoinedSoundEnabled)
                                            p.playSound(p.getLocation(), XSound.valueOf(
                                                            joinedSound).parseSound(),
                                                    50.0f, 1.0f);
                                        BedwarsAPI.getGameAPI().getLobbyItemHandler(
                                                        "better-team-selector")
                                                .handleUse(p, arena, null);
                                    }
                                }
                                else {
                                    p.sendMessage(colorAPI.process(alreadyInTeamMsg));
                                    if(isAlreadyInTeamSoundEnabled)
                                        p.playSound(p.getLocation(), XSound.valueOf(
                                                        alreadyInTeamSound).parseSound(),
                                                50.0f, 1.0f);
                                }
                            } catch (Exception ex) {
                                if(playersInTeam == playersPerTeam) {
                                    p.sendMessage(colorAPI.process(teamFullMsg));
                                    if(isTeamFullSoundEnabled)
                                        p.playSound(p.getLocation(), XSound.valueOf(
                                                        teamFullSound).parseSound(),
                                                50.0f, 1.0f);
                                }
                                else {
                                    arena.setPlayerTeam(p, team);
                                    p.sendMessage(colorAPI.process(joinedMsg
                                            .replaceAll("%team%",
                                                    config.getString("menu.team-display-names."
                                                            + team.name()))));
                                    if(isJoinedSoundEnabled)
                                        p.playSound(p.getLocation(), XSound.valueOf(
                                                        joinedSound).parseSound(),
                                                50.0f, 1.0f);
                                    BedwarsAPI.getGameAPI().getLobbyItemHandler(
                                                    "better-team-selector")
                                            .handleUse(p, arena, null);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {return;}
    }
}
