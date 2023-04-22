package pl.dejwideek.teamselector.color;

import com.google.common.collect.ImmutableMap;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Map;

@SuppressWarnings("ALL")
public class ColorAPI {

    private static final int VERSION = getVersion();
    private static final boolean SUPPORTS_RGB = VERSION >= 16;

    private static final ColorPattern PATTERN = new ColorUtil();

    private static final Map<Color, ChatColor> COLORS = ImmutableMap.<Color, ChatColor>builder()
            .put(new Color(0), ChatColor.getByChar('0'))
            .put(new Color(170), ChatColor.getByChar('1'))
            .put(new Color(43520), ChatColor.getByChar('2'))
            .put(new Color(43690), ChatColor.getByChar('3'))
            .put(new Color(11141120), ChatColor.getByChar('4'))
            .put(new Color(11141290), ChatColor.getByChar('5'))
            .put(new Color(16755200), ChatColor.getByChar('6'))
            .put(new Color(11184810), ChatColor.getByChar('7'))
            .put(new Color(5592405), ChatColor.getByChar('8'))
            .put(new Color(5592575), ChatColor.getByChar('9'))
            .put(new Color(5635925), ChatColor.getByChar('a'))
            .put(new Color(5636095), ChatColor.getByChar('b'))
            .put(new Color(16733525), ChatColor.getByChar('c'))
            .put(new Color(16733695), ChatColor.getByChar('d'))
            .put(new Color(16777045), ChatColor.getByChar('e'))
            .put(new Color(16777215), ChatColor.getByChar('f')).build();

    @Nonnull
    public static String color(@Nonnull String string, @Nonnull Color color) {
        return (SUPPORTS_RGB ? ChatColor.of(color) : getClosestColor(color)) + string;
    }

    @Nonnull
    public static String process(@Nonnull String string) {
        string = PATTERN.process(string);
        string = ChatColor.translateAlternateColorCodes('&', string);
        return string;
    }

    @Nonnull
    public static ChatColor getColor(@Nonnull String string) {
        return SUPPORTS_RGB ? ChatColor.of(new Color(Integer.parseInt(string, 16)))
                : getClosestColor(new Color(Integer.parseInt(string, 16)));
    }

    private static int getVersion() {
        String version = Bukkit.getVersion();
        Validate.notEmpty(version, "Cannot get major Minecraft version from null or empty string");

        // getVersion()
        int index = version.lastIndexOf("MC:");
        if (index != -1) {
            version = version.substring(index + 4, version.length() - 1);
        } else if (version.endsWith("SNAPSHOT")) {
            // getBukkitVersion()
            index = version.indexOf('-');
            version = version.substring(0, index);
        }

        // 1.13.2, 1.14.4, etc...
        int lastDot = version.lastIndexOf('.');
        if (version.indexOf('.') != lastDot) version = version.substring(0, lastDot);

        return Integer.parseInt(version.substring(2));
    }

    @Nonnull
    private static ChatColor getClosestColor(Color color) {
        Color nearestColor = null;
        double nearestDistance = Integer.MAX_VALUE;

        for (Color constantColor : COLORS.keySet()) {
            double distance = Math.pow(color.getRed() - constantColor.getRed(), 2) + Math.pow(color.getGreen() - constantColor.getGreen(), 2) + Math.pow(color.getBlue() - constantColor.getBlue(), 2);
            if (nearestDistance > distance) {
                nearestColor = constantColor;
                nearestDistance = distance;
            }
        }
        return COLORS.get(nearestColor);
    }
}
