package org.chrku.grid;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class LabeledGrid {
    private final Grid grid;
    private final List<List<Double>> elements;

    LabeledGrid(Grid grid, List<List<Double>> labels) {
        this.grid = grid;
        this.elements = labels;
    }

    private static Color getInterpolatedColor(double value,
                                              double minVal,
                                              double maxVal,
                                              float hue,
                                              float saturation) {
        double brightness = (value - minVal) / (maxVal - minVal);
        return Color.getHSBColor(hue, saturation, (float) brightness);
    }

    private double getMinVal() {
        double minVal = Double.MAX_VALUE;

        for (List<Double> row : elements) {
            for (double elem : row) {
                if (elem < minVal) {
                    minVal = elem;
                }
            }
        }

        return minVal;
    }

    private double getMaxVal() {
        double maxVal = Double.MIN_VALUE;

        for (List<Double> row : elements) {
            for (double elem : row) {
                if (elem > maxVal) {
                    maxVal = elem;
                }
            }
        }

        return maxVal;
    }

    private void colorGrid(BufferedImage image, int cellSize, int lineWidth, Color baseColor) {
        double minVal = getMinVal();
        double maxVal = getMaxVal();
        float[] hsbColor = Color.RGBtoHSB(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), null);
        WritableRaster raster = image.getRaster();

        for (var it = grid.cellIterator(); it.hasNext(); ) {
            Cell current = it.next();

            int x = current.getColumn();
            int y = current.getRow();
            double value = elements.get(y).get(x);
            Color curColor = getInterpolatedColor(value, minVal, maxVal, hsbColor[0], hsbColor[1]);

            DrawUtils.fillRect(raster, x * (cellSize + lineWidth) + lineWidth,
                    y * (cellSize + lineWidth) + lineWidth, cellSize, cellSize, curColor);
        }
    }

    public void writeImage(Path path, int cellSize, int lineWidth, Color baseColor) throws IOException {
        BufferedImage outputImage = grid.getImage(cellSize, lineWidth);
        colorGrid(outputImage, cellSize, lineWidth, baseColor);
        ImageIO.write(outputImage, "png", path.toFile());
    }
}
