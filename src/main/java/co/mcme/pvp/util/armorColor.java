package co.mcme.pvp.util;

import org.bukkit.Color;

public class armorColor {

    public static int cred;
    public static int cgreen;
    public static int cblue;
    public static final Color RED = fromString("#ff0000");
    public static final Color BLUE = fromString("#003cff");
    public static final Color GREEN = fromString("#00ff36");
    public static final Color YELLOW = fromString("#f6ff00");
    public static final Color BLACK = fromString("#000000");
    public static final Color WHITE = fromString("#ffffff");
    public static final Color AQUA = fromString("#00ac98");
    public static final Color PURPLE = fromString("#534481");
    public static final Color LIME = fromString("#55FF55");

    public static Color fromString(String string) {
        java.awt.Color color = java.awt.Color.decode(string);
        cred = color.getRed();
        cgreen = color.getGreen();
        cblue = color.getBlue();
        return Color.fromRGB(cred, cgreen, cblue);

        //.fromInt(color.getRed(), color.getGreen(), color.getBlue());
    }

    /*private ArmorColor(int red, int green, int blue) {
     this.cred = red;
     this.cgreen = green;
     this.cblue = blue;
     }*/
    public int getBlue() {
        return this.cblue;
    }

    public int getRed() {
        return this.cred;
    }

    public int getGreen() {
        return this.cgreen;
    }

    public enum ColEnum {

        red(armorColor.RED),
        blue(armorColor.BLUE),
        green(armorColor.GREEN),
        yellow(armorColor.YELLOW),
        black(armorColor.BLACK),
        aqua(armorColor.AQUA),
        purple(armorColor.PURPLE);
        Color col;

        private ColEnum(Color colo) {
            this.col = colo;
        }

        public Color getColor() {
            return this.col;
        }

        public String getName() {
            return col.toString();
        }
    }
}
