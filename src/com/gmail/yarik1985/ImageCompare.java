package com.gmail.yarik1985;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageCompare {

    public static void main(String args[]) throws IOException {
        BufferedImage img1 = null;
        BufferedImage img2 = null;
        try {
            File pic1 = new File("D:\\images\\image1.png");
            File pic2 = new File("D:\\images\\image2.png");
            img1 = ImageIO.read(pic1);
            img2 = ImageIO.read(pic2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int width1 = img1.getWidth(null);
        int width2 = img2.getWidth(null);
        int height1 = img1.getHeight(null);
        int height2 = img2.getHeight(null);
        if ((width1 != width2) || (height1 != height2)) {
            System.err.println(String.format("Error: Images dimensions mismatch! Images must have the same dimensions: (%d,%d) vs. (%d,%d)", width1, height1, width2, height2));
            System.exit(1);
        }

        BufferedImage temp = new BufferedImage(width1, height1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = temp.createGraphics();
        g.drawImage(img2, 0, 0, null);

        for (int row = 0; row < height1; row++) {
            for (int col = 0; col < width1; col++) {
                int result = img1.getRGB(col, row);
                int result1 = img2.getRGB(col, row);

                if (result != result1) {
                    g.setColor(Color.red);
                    g.setStroke(new BasicStroke(2.0F));
                    g.drawRect(col - 1, row - 12, 35, 35);


                    g.drawRect(col, row - 10, 0, 20);
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.65f));

                }
            }
        }

        g.dispose();

        File out = new File("D:\\output.png");
        ImageIO.write(temp, "PNG", out);

        JOptionPane.showMessageDialog(null, new ImageIcon(temp), "Result of image comparison", JOptionPane.INFORMATION_MESSAGE);


    }

}
