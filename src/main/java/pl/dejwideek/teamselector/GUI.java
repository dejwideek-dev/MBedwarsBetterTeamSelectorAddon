package pl.dejwideek.teamselector;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import pl.dejwideek.teamselector.color.ColorAPI;

import java.util.ArrayList;
import java.util.function.Consumer;

@SuppressWarnings("ALL")
public class GUI {

    private static TeamSelectorPlugin plugin;

    public GUI(TeamSelectorPlugin plugin) {
        this.plugin = plugin;
    }

    public void gui(Player player) {
        YamlDocument config = plugin.config;
        ColorAPI colorAPI = new ColorAPI();

        String title = config.getString("menu.title");
        boolean isItemEnchant = config
                .getBoolean("menu.current-team-item-enchant");
        boolean isFillEmptySlots = config.getBoolean("menu.fill-empty-slots.enabled");
        boolean isPlayerListEnabled = config.getBoolean(
                "menu.lore.player-list-line.enabled");
        boolean isPerArenaTemplatesEnabled =
                config.getBoolean("team-selector.per-arena-templates-enabled");
        boolean isTemplatesEnabled = config.getBoolean("team-selector.templates-enabled");
        Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);
        
        if (arena == null)
            return;
    
        int arenaPlayersPerTeam = arena.getPlayersPerTeam();
        int arenaTeams = arena.getEnabledTeams().size();
        String arenaName = arena.getName();

        final Consumer<Section> readTemplateSection = section -> {
            int size = section.getInt("size");
            Inventory gui = Bukkit.createInventory(
                    null, size, colorAPI.process(title));

            for (Team team : arena.getEnabledTeams()) {
                String teamName = team.name()
                        .replaceAll("LIGHT_GREEN", "LIME"); // this.
                int itemSlot = section.getInt("slots." + teamName);
                String cfgMaterial = config.getString("menu.material").toUpperCase();
                byte cfgData = config.getByte("menu.material-data");
                String itemMaterial = null;
                byte itemData = 0;
                String itemDisplayName = config.getString(
                        "menu.display-name");
                ArrayList<String> loreList = new ArrayList<>();

                if (cfgMaterial.equals("WOOL")
                        || cfgMaterial.equals("STAINED_GLASS")
                        || cfgMaterial.equals("STAINED_GLASS_PANE")
                        || cfgMaterial.equals("TERRACOTTA")
                        || cfgMaterial.equals("GLAZED_TERRACOTTA")
                        || cfgMaterial.equals("CONCRETE")
                        || cfgMaterial.equals("CANDLE")
                        || cfgMaterial.equals("CARPET")
                        || cfgMaterial.equals("SHULKER_BOX")
                        || cfgMaterial.equals("BED")) {
                    if (!cfgMaterial.equals("GLAZED_TERRACOTTA")
                            || !cfgMaterial.equals("CANDLE")
                            || !cfgMaterial.equals("SHULKER_BOX")) {
                        itemData = team.getDyeColor().getWoolData();
                        itemMaterial = teamName + "_" + cfgMaterial;
                    } else itemMaterial = teamName + "_" + cfgMaterial;
                } else {
                    itemMaterial = cfgMaterial;
                    itemData = cfgData;
                }

                ItemStack item = new ItemStack(XMaterial.valueOf(
                        itemMaterial).parseMaterial(), 1, itemData);
                if (itemMaterial.startsWith("LEATHER_")
                        && !itemMaterial.endsWith("HORSE_ARMOR")) {
                    LeatherArmorMeta meta =
                            (LeatherArmorMeta) item.getItemMeta();

                    meta.setColor(team.getDyeColor().getColor());
                    meta.setDisplayName(colorAPI.process(itemDisplayName
                                    .replaceAll("%team%",
                                            config.getString("menu.team-display-names."
                                                    + teamName))
                                    .replaceAll("%players%",
                                            String.valueOf(arena
                                                    .getPlayersInTeam(team).size())))
                            .replaceAll("%max%",
                                    String.valueOf(arenaPlayersPerTeam)));

                    for (String s : config.getStringList("menu.lore.header")) {
                        loreList.add(colorAPI.process(s.replaceAll("%players%",
                                        String.valueOf(arena
                                                .getPlayersInTeam(team).size())))
                                .replaceAll("%max%",
                                        String.valueOf(arenaPlayersPerTeam)));
                    }
                    if (isPlayerListEnabled) {
                        if (arena.getPlayersInTeam(team).size() != 0) {
                            for (Player p : arena.getPlayersInTeam(team)) {
                                String s = config.getString(
                                        "menu.lore.player-list-line.line");
                                loreList.add(colorAPI.process(
                                        s.replaceAll("%player%", p.getName())));
                            }
                        } else {
                            String s = config.getString(
                                    "menu.lore.player-list-line.no-players");
                            loreList.add(colorAPI.process(s));
                        }
                    }
                    for (String s : config.getStringList("menu.lore.footer")) {
                        String placeholder = null;
                        try {
                            if (!arena.getPlayerTeam(player).equals(team)) {
                                if (arena.getPlayersInTeam(team).size()
                                        == arenaPlayersPerTeam) {
                                    placeholder = config.getString(
                                            "menu.lore.status.team-is-full");
                                } else {
                                    placeholder = config.getString(
                                            "menu.lore.status.click-to-join");
                                }
                            } else {
                                placeholder = config.getString(
                                        "menu.lore.status.already-in-team");
                            }
                        } catch (Exception e) {
                            placeholder = config.getString(
                                    "menu.lore.status.click-to-join");
                        }

                        loreList.add(colorAPI.process(s
                                        .replaceAll("%status%", placeholder)
                                        .replaceAll("%players%",
                                                String.valueOf(arena
                                                        .getPlayersInTeam(team).size())))
                                .replaceAll("%max%",
                                        String.valueOf(arenaPlayersPerTeam)));
                    }
                    meta.setLore(loreList);

                    try {
                        if (arena.getPlayerTeam(player).equals(team)) {
                            if (isItemEnchant) {
                                meta.addEnchant(
                                        XEnchantment.DAMAGE_ALL.getEnchant(),
                                        1, true);
                            }
                        }
                    } catch (Exception e) {
                    }

                    meta.addItemFlags(
                            ItemFlag.HIDE_ATTRIBUTES,
                            ItemFlag.HIDE_ENCHANTS,
                            ItemFlag.HIDE_DESTROYS,
                            ItemFlag.HIDE_POTION_EFFECTS,
                            ItemFlag.HIDE_UNBREAKABLE,
                            ItemFlag.HIDE_PLACED_ON);

                    item.setItemMeta(meta);
                } else {
                    ItemMeta meta = item.getItemMeta();

                    meta.setDisplayName(colorAPI.process(itemDisplayName
                                    .replaceAll("%team%",
                                            config.getString("menu.team-display-names."
                                                    + teamName))
                                    .replaceAll("%players%",
                                            String.valueOf(arena
                                                    .getPlayersInTeam(team).size())))
                            .replaceAll("%max%",
                                    String.valueOf(arenaPlayersPerTeam)));

                    for (String s : config.getStringList("menu.lore.header")) {
                        loreList.add(colorAPI.process(s.replaceAll("%players%",
                                        String.valueOf(arena
                                                .getPlayersInTeam(team).size())))
                                .replaceAll("%max%",
                                        String.valueOf(arenaPlayersPerTeam)));
                    }
                    if (isPlayerListEnabled) {
                        if (arena.getPlayersInTeam(team).size() != 0) {
                            for (Player p : arena.getPlayersInTeam(team)) {
                                String s = config.getString(
                                        "menu.lore.player-list-line.line");
                                loreList.add(colorAPI.process(
                                        s.replaceAll("%player%", p.getName())));
                            }
                        } else {
                            String s = config.getString(
                                    "menu.lore.player-list-line.no-players");
                            loreList.add(colorAPI.process(s));
                        }
                    }
                    for (String s : config.getStringList("menu.lore.footer")) {
                        String placeholder = null;
                        try {
                            if (!arena.getPlayerTeam(player).equals(team)) {
                                if (arena.getPlayersInTeam(team).size()
                                        == arenaPlayersPerTeam) {
                                    placeholder = config.getString(
                                            "menu.lore.status.team-is-full");
                                } else {
                                    placeholder = config.getString(
                                            "menu.lore.status.click-to-join");
                                }
                            } else {
                                placeholder = config.getString(
                                        "menu.lore.status.already-in-team");
                            }
                        } catch (Exception e) {
                            placeholder = config.getString(
                                    "menu.lore.status.click-to-join");
                        }

                        loreList.add(colorAPI.process(s
                                        .replaceAll("%status%", placeholder)
                                        .replaceAll("%players%",
                                                String.valueOf(arena
                                                        .getPlayersInTeam(team).size())))
                                .replaceAll("%max%",
                                        String.valueOf(arenaPlayersPerTeam)));
                    }
                    meta.setLore(loreList);

                    try {
                        if (arena.getPlayerTeam(player).equals(team)) {
                            if (isItemEnchant) {
                                meta.addEnchant(
                                        XEnchantment.DAMAGE_ALL.getEnchant(),
                                        1, true);
                            }
                        }
                    } catch (Exception e) {
                    }
                    meta.addItemFlags(
                            ItemFlag.HIDE_ATTRIBUTES,
                            ItemFlag.HIDE_ENCHANTS,
                            ItemFlag.HIDE_DESTROYS,
                            ItemFlag.HIDE_POTION_EFFECTS,
                            ItemFlag.HIDE_UNBREAKABLE,
                            ItemFlag.HIDE_PLACED_ON);

                    item.setItemMeta(meta);
                }

                gui.setItem(itemSlot, item);
            }

            if(isFillEmptySlots) {
                String emptyItemMaterial = config.getString(
                                "menu.fill-empty-slots.material")
                        .toUpperCase();
                byte emptyItemData = config.getByte(
                        "menu.fill-empty-slots.material-data");

                ItemStack emptyItem = new ItemStack(XMaterial.valueOf(
                        emptyItemMaterial).parseMaterial(), 1, emptyItemData);
                ItemMeta emptyMeta = emptyItem.getItemMeta();
                emptyMeta.setDisplayName(" ");
                emptyMeta.addItemFlags(
                        ItemFlag.HIDE_ATTRIBUTES,
                        ItemFlag.HIDE_ENCHANTS,
                        ItemFlag.HIDE_DESTROYS,
                        ItemFlag.HIDE_POTION_EFFECTS,
                        ItemFlag.HIDE_UNBREAKABLE,
                        ItemFlag.HIDE_PLACED_ON);
                emptyItem.setItemMeta(emptyMeta);

                for(int i = 0; i < size; i++) {
                    if(gui.getItem(i) == null
                            || gui.getItem(i)
                            .getType().equals(XMaterial.AIR))
                        gui.setItem(i, emptyItem);
                }
            }

            player.openInventory(gui);
        };
        boolean found = false;

        if(isPerArenaTemplatesEnabled) {
            Section templatesSection = config.getSection("team-selector.per-arena-templates");

            for (Object rawKey : templatesSection.getKeys()) {
                Section templateSection = templatesSection.getSection((String) rawKey);

                if (templateSection == null)
                    continue;

                readTemplateSection.accept(templateSection);
                found = true;

                break;
            }
        }
        if(!found) {
            if (isTemplatesEnabled) {
                Section templatesSection = config.getSection("team-selector.templates");

                for (Object rawKey : templatesSection.getKeys()) {
                    Section templateSection = templatesSection.getSection((String) rawKey);

                    if (templateSection == null)
                        continue;

                    int teams = templateSection.getInt("teams");
                    int playersPerTeam = templateSection.getInt("players-per-team");

                    if (arena.getEnabledTeams().size() != teams || arena.getPlayersPerTeam() != playersPerTeam)
                        continue;

                    readTemplateSection.accept(templateSection);
                    found = true;

                    break;
                }
            }
        }
        if (!found)
            readTemplateSection.accept(config.getSection("team-selector.default-template"));
    }
}
