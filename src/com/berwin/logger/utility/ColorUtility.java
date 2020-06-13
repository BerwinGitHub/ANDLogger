package com.berwin.logger.utility;

import java.awt.*;

public class ColorUtility {

    public static Color hexToColor(String str) {
        str = str.replace("#", "");
        String str1 = str.substring(0, 2);
        String str2 = str.substring(2, 4);
        String str3 = str.substring(4, 6);
        String str4 = str.substring(6, 8);
        int alpha = Integer.parseInt(str1, 16);
        int red = Integer.parseInt(str2, 16);
        int green = Integer.parseInt(str3, 16);
        int blue = Integer.parseInt(str4, 16);
        Color color = new Color(red, green, blue, alpha);
        return color;
    }

    public static String colorToHex(Color color) {
        String a = Integer.toHexString(color.getAlpha());
        String r = Integer.toHexString(color.getRed());
        String g = Integer.toHexString(color.getGreen());
        String b = Integer.toHexString(color.getBlue());
        a = a.length() == 2 ? a : "0" + a;
        r = r.length() == 2 ? r : "0" + r;
        g = g.length() == 2 ? g : "0" + g;
        b = b.length() == 2 ? b : "0" + b;
        return String.format("#%s%s%s%s", a, r, g, b);
    }
}
