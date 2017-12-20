package com.gmail.yarik1985;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class ImagesComparator {

    public static void main(String[] args) throws IOException {
        LinkedList<Rect> rectangles = null;

        BufferedImage img1 = ImageIO.read(new File("C:\\temp\\image1.png"));
        BufferedImage img2 = ImageIO.read(new File("C:\\temp\\image2.png"));

        int width = img1.getWidth();
        int height = img1.getHeight();
        int width2 = img2.getWidth();
        int height2 = img2.getHeight();
        int xOffset = 0;
        int yOffset = 0;
        int length = img1.getWidth() * img1.getHeight();

        int[][] image1 = new int[height][width];
        int[][] image2 = new int[height][width];

        int[] img1Pix = new int[length];
        int[] img2Px = new int[length];

        if ((width != width2) || (height != height2)) {
            System.err.println(String.format("Error: Images dimensions mismatch! Images must have the same dimensions: (%d,%d) vs. (%d,%d)", width, height, width2, height2));
            System.exit(1);
        }

        img1.getRGB(0, 0, width, height, img1Pix, 0, width);
        img2.getRGB(0, 0, width2, height2, img2Px, 0, width2);

        for (int row = 0; row < height; row++) {
            System.arraycopy(img1Pix, (row * width), image1[row], 0, width);
            System.arraycopy(img2Px, (row * width), image2[row], 0, width);
        }

        ImagesComparator imgComp = new ImagesComparator();
        rectangles = imgComp.diffImg(image1, image2, xOffset, yOffset, width, height);

        int index = 0;
        for (Rect rect : rectangles) {

            for (int i = rect.y; i < rect.y + rect.w; i++) {
                img2Px[(rect.x * width) + i] = 0xFFFF0000;
                img2Px[((rect.x + rect.h - 1) * width) + i] = 0xFFFF0000;
            }

            for (int j = rect.x; j < rect.x + rect.h; j++) {
                img2Px[(j * width) + rect.y] = 0xFFFF0000;
                img2Px[(j * width) + (rect.y + rect.w - 1)] = 0xFFFF0000;
            }

        }

        img2.setRGB(0, 0, width, height, img2Px, 0, width);
        ImageIO.write(img2, "PNG", new File("C:\\temp\\result.png"));
    }

    public LinkedList<Rect> diffImg(int[][] image1, int[][] image2, int xOffset, int yOffset, int width, int height) {
        // Code starts here
        int xRover = 0;
        int yRover = 0;
        int index = 0;
        int limit = 0;
        int rover = 0;

        boolean isRectChanged = false;
        boolean shouldSkip = false;

        LinkedList<Rect> rectangles = new LinkedList<Rect>();
        Rect rect = null;

        int verticalLimit = xOffset + height;
        int horizontalLimit = yOffset + width;

        for (xRover = xOffset; xRover < verticalLimit; xRover += 1) {
            for (yRover = yOffset; yRover < horizontalLimit; yRover += 1) {

                if (image1[xRover][yRover] != image2[xRover][yRover]) {

                    for (Rect itrRect : rectangles) {
                        if (((xRover < itrRect.x + itrRect.h) && (xRover >= itrRect.x)) && ((yRover < itrRect.y + itrRect.w) && (yRover >= itrRect.y))) {
                            shouldSkip = true;
                            yRover = itrRect.y + itrRect.w - 1;
                            break;
                        }
                    }

                    if (shouldSkip) {
                        shouldSkip = false;
                        continue;
                    }
                    rect = new Rect();

                    rect.x = ((xRover - 1) < xOffset) ? xOffset : (xRover - 1);
                    rect.y = ((yRover - 1) < yOffset) ? yOffset : (yRover - 1);
                    rect.w = 2;
                    rect.h = 2;

                    isRectChanged = true;

                    while (isRectChanged) {
                        isRectChanged = false;
                        index = 0;

                        index = rect.x;
                        limit = rect.x + rect.h;
                        while (index < limit && rect.y != yOffset) {
                            if (image1[index][rect.y] != image2[index][rect.y]) {
                                isRectChanged = true;
                                rect.y = rect.y - 1;
                                rect.w = rect.w + 1;
                                index = rect.x;
                                continue;
                            }

                            index = index + 1;
                            ;
                        }

                        index = rect.y;
                        limit = rect.y + rect.w;
                        while ((index < limit) && (rect.x + rect.h != verticalLimit)) {
                            rover = rect.x + rect.h - 1;
                            if (image1[rover][index] != image2[rover][index]) {
                                isRectChanged = true;
                                rect.h = rect.h + 1;
                                index = rect.y;
                                continue;
                            }

                            index = index + 1;
                        }

                        index = rect.x;
                        limit = rect.x + rect.h;
                        while ((index < limit) && (rect.y + rect.w != horizontalLimit)) {
                            rover = rect.y + rect.w - 1;
                            if (image1[index][rover] != image2[index][rover]) {
                                isRectChanged = true;
                                rect.w = rect.w + 1;
                                index = rect.x;
                                continue;
                            }

                            index = index + 1;
                        }
                    }

                    int idx = 0;
                    while (idx < rectangles.size()) {
                        Rect r = rectangles.get(idx);
                        if (((rect.x <= r.x) && (rect.x + rect.h >= r.x + r.h)) && ((rect.y <= r.y) && (rect.y + rect.w >= r.y + r.w))) {
                            rectangles.remove(r);
                        } else {
                            idx += 1;
                        }
                    }

                    rectangles.addFirst(rect);

                    yRover = rect.y + rect.w - 1;
                    rect = null;

                }
            }
        }
        return rectangles;
    }

    public class Rect {
        public int x;
        public int y;
        public int w;
        public int h;

        public boolean equals(Object obj) {
            Rect rect = (Rect) obj;
            if (rect.x == this.x && rect.y == this.y && rect.w == this.w && rect.h == this.h) {
                return true;
            }
            return false;
        }
    }

}
