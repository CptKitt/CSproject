package GUI.assets.fonts;

import javafx.scene.text.Font;

/** Helper class to locate Font resources. */
public class Fonts {
    public static Font boldPixel(int size) {
        Font font = Font.loadFont(Fonts.class.getResource("pixelmix_bold.ttf").toExternalForm(), size);
        if (font == null) {
            System.out.println("Unable to load custom fonts. Defaulting to system font.");
            return Font.font(size);
        }
        return font;
    }
    
    public static Font pixel(int size) {
        Font font = Font.loadFont(Fonts.class.getResource("pixelmix.ttf").toExternalForm(), size);
        if (font == null) {
            System.out.println("Unable to load custom fonts. Defaulting to system font.");
            return Font.font(size);
        }
        return font;
    }
}
