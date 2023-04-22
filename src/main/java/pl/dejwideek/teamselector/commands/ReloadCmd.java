package pl.dejwideek.teamselector.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import pl.dejwideek.teamselector.TeamSelectorPlugin;
import pl.dejwideek.teamselector.color.ColorAPI;

import java.io.IOException;

@SuppressWarnings("ALL")
public class ReloadCmd extends BaseCommand {

    private static TeamSelectorPlugin plugin;

    public ReloadCmd(TeamSelectorPlugin plugin) {
        this.plugin = plugin;
    }

    @CommandAlias("betterteamselectorreload|teamselectorreload|btsreload|tsreload")
    @Description("Reload config file")
    public void reload(CommandSender commandSender) {
        if(commandSender instanceof Player) {
            Player p = (Player) commandSender;
            YamlDocument config = plugin.config;
            ColorAPI colorApi = new ColorAPI();

            String permission = config.getString("permissions.reload");
            String reloadedMsg = config.getString("messages.reloaded");
            String noPermsMsg = config.getString("messages.no-permission");

            if(p.hasPermission(permission)) {
                try {
                    plugin.config.reload();
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }

                p.sendMessage(colorApi.process(reloadedMsg));
                plugin.getLogger().info("Reloaded configuration file!");
            }
            else {
                p.sendMessage(colorApi.process(noPermsMsg
                        .replaceAll("%permission%", permission)));
            }
        }
        if(commandSender instanceof ConsoleCommandSender) {
            try {
                plugin.config.reload();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }

            plugin.getLogger().info("Reloaded configuration file!");
        }
        return;
    }
}
