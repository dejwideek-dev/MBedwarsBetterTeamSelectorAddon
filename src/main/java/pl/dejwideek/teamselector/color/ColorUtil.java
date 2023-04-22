package pl.dejwideek.teamselector.color;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("ALL")
public class ColorUtil implements ColorPattern {

    Pattern pattern = Pattern.compile("&#([0-9A-Fa-f]{6})|#\\{([0-9A-Fa-f]{6})}");

    public String process(String string) {
        Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            String color = matcher.group(1);
            if (color == null) color = matcher.group(2);

            string = string.replace(matcher.group(), ColorAPI.getColor(color) + "");
        }
        return string;
    }
}
