package org.chrku.grid;

import java.awt.*;
import java.awt.image.WritableRaster;

public class DrawUtils {
    private DrawUtils() {
    }

    public static void fillRect(WritableRaster raster, int x, int y, int width, int height, Color color) {
        int[] comp = new int[3];

        comp[0] = color.getRed();
        comp[1] = color.getGreen();
        comp[2] = color.getBlue();

        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                raster.setPixel(x + j, y + i, comp);
            }
        }
    }
}
