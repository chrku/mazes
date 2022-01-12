package org.chrku.grid;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class LabeledGrid extends Grid {
    private final Color baseColor;
    private List<List<Double>> labels;

    public LabeledGrid(int numRows, int numCols, Color baseColor) {
        super(numRows, numCols);

        this.baseColor = baseColor;
        this.labels = new ArrayList<>();

        for (int i = 0; i < rows(); ++i) {
            labels.add(new ArrayList<>());
            for (int j = 0; j < columns(); ++j) {
                labels.get(i).add(0.0);
            }
        }
    }

    private static Color getInterpolatedColor(double value,
                                              double minVal,
                                              double maxVal,
                                              float hue,
                                              float brightness) {
        double saturation = (value - minVal) / (maxVal - minVal);
        return Color.getHSBColor(hue, (float) saturation, brightness);
    }

    public void setLabels(List<List<Double>> labels) {
        this.labels = labels;
    }

    private double getMinVal() {
        double minVal = Double.MAX_VALUE;

        for (List<Double> row : labels) {
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

        for (List<Double> row : labels) {
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

        int totalCellSize = cellSize + lineWidth;

        for (var it = cellIterator(); it.hasNext(); ) {
            Cell current = it.next();

            int x = current.getColumn();
            int y = current.getRow();
            double value = labels.get(y).get(x);
            Color curColor = getInterpolatedColor(value, minVal, maxVal, hsbColor[0], hsbColor[2]);

            DrawUtils.fillRect(raster, x * totalCellSize,
                    y * totalCellSize, totalCellSize, totalCellSize, curColor);
        }
    }

    @Override
    public void writeImage(Path path, int cellSize, int lineWidth) throws IOException {
        BufferedImage outputImage = getImage(cellSize, lineWidth);
        colorGrid(outputImage, cellSize, lineWidth, baseColor);
        drawBorders(outputImage.getRaster(), cellSize, lineWidth);
        ImageIO.write(outputImage, "png", path.toFile());
    }

    private List<List<String>> getTextLabels() {
        ArrayList<List<String>> lists = new ArrayList<>();
        for (List<Double> list : labels) {
            ArrayList<String> row = new ArrayList<>();
            lists.add(row);
            for (double d : list) {
                row.add(Integer.toString((int) d));
            }
        }

        return lists;
    }

    @Override
    public String toString() {
        var labels =  getTextLabels();
        int maxWidthString = labels.stream()
                .flatMap(Collection::stream)
                .max(Comparator.comparingInt(String::length))
                .orElse("")
                .length();

        int verticalPad = (maxWidthString / 2) * 2 + 1;

        String baseElement = "-".repeat(maxWidthString + 2);
        String baseBlank = " ".repeat(maxWidthString + 2);

        StringBuilder builder = new StringBuilder();

        builder.append("+");
        builder.append((baseElement + "+").repeat(Math.max(0, columns())));
        builder.append("\n");

        rowIterator().forEachRemaining((List<Cell> l) -> {
            for (int i = 0; i < verticalPad; ++i) {
                StringBuilder top = new StringBuilder();
                top.append("|");

                for (Cell c : l) {
                    if (i == verticalPad / 2) {
                        top.append(" ");
                        top.append(String.format("%" + maxWidthString + "s",
                                labels.get(c.getRow()).get(c.getColumn())));
                        top.append(" ");
                    } else {
                        top.append(baseBlank);
                    }
                    if (!c.isLinked(c.getEast())) {
                        top.append("|");
                    } else {
                        top.append(" ");
                    }
                }

                builder.append(top);
                builder.append("\n");
            }

            StringBuilder bottom = new StringBuilder();
            bottom.append("+");
            for (Cell c : l) {
                if (!c.isLinked(c.getSouth())) {
                    bottom.append(baseElement);
                } else {
                    bottom.append(baseBlank);
                }
                bottom.append("+");
            }

            builder.append(bottom);
            builder.append("\n");
        });

        return builder.toString();
    }
}
