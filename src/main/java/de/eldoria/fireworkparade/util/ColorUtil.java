package de.eldoria.fireworkparade.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Color;

@UtilityClass
public class ColorUtil {
    public static Color parseColor(char color) {
        switch (color) {
            case '0':
                return Color.fromRGB(0x000000);
            case '1':
                return Color.fromRGB(0x0000AA);
            case '2':
                return Color.fromRGB(0x00AA00);
            case '3':
                return Color.fromRGB(0x00AAAA);
            case '4':
                return Color.fromRGB(0xAA0000);
            case '5':
                return Color.fromRGB(0xAA00AA);
            case '6':
                return Color.fromRGB(0xFFAA00);
            case '7':
                return Color.fromRGB(0xAAAAAA);
            case '8':
                return Color.fromRGB(0x555555);
            case '9':
                return Color.fromRGB(0x5555FF);
            case 'a':
                return Color.fromRGB(0x55FF55);
            case 'b':
                return Color.fromRGB(0x55FFFF);
            case 'c':
                return Color.fromRGB(0xFF5555);
            case 'd':
                return Color.fromRGB(0xFF55FF);
            case 'e':
                return Color.fromRGB(0xFFFF55);
            case 'f':
                return Color.fromRGB(0xFFFFFF);
        }
        return null;
    }

    public static Color parseColor(String color) {
        switch (color.toLowerCase()) {
            case "black":
                return Color.fromRGB(0x000000);
            case "dark_blue":
                return Color.fromRGB(0x0000AA);
            case "dark_green":
                return Color.fromRGB(0x00AA00);
            case "dark_aqua":
                return Color.fromRGB(0x00AAAA);
            case "dark_red":
                return Color.fromRGB(0xAA0000);
            case "dark_purple":
                return Color.fromRGB(0xAA00AA);
            case "gold":
                return Color.fromRGB(0xFFAA00);
            case "gray":
                return Color.fromRGB(0xAAAAAA);
            case "dark_gray":
                return Color.fromRGB(0x555555);
            case "blue":
                return Color.fromRGB(0x5555FF);
            case "green":
                return Color.fromRGB(0x55FF55);
            case "aqua":
                return Color.fromRGB(0x55FFFF);
            case "red":
                return Color.fromRGB(0xFF5555);
            case "light_purple":
                return Color.fromRGB(0xFF55FF);
            case "yellow":
                return Color.fromRGB(0xFFFF55);
            case "white":
                return Color.fromRGB(0xFFFFFF);
        }
        return null;
    }

    public static String colorToString(Color color) {
        switch (color.asRGB()) {
            case 0x000000:
                return "0";
            case 0x0000AA:
                return "1";
            case 0x00AA00:
                return "2";
            case 0x00AAAA:
                return "3";
            case 0xAA0000:
                return "4";
            case 0xAA00AA:
                return "5";
            case 0xFFAA00:
                return "6";
            case 0xAAAAAA:
                return "7";
            case 0x555555:
                return "8";
            case 0x5555FF:
                return "9";
            case 0x55FF55:
                return "a";
            case 0x55FFFF:
                return "b";
            case 0xFF5555:
                return "c";
            case 0xFF55FF:
                return "d";
            case 0xFFFF55:
                return "e";
            case 0xFFFFFF:
                return "f";
        }
        return null;
    }

}
