package org.kumoricon.registration.print.formatter.badgeimage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Class for creating badge images in PNG format. Contains functions to do the drawing
 * and text placement, plus the image itself.
 */
public class BadgeImage {
    //private static final Logger log = LoggerFactory.getLogger(BadgeImage.class);
    private final BufferedImage image;
    private final Graphics2D g2;
    private final int dpi;

    /**
     * Create new BadgeImage instance
     * @param width Width in pixels
     * @param height Height in pixels
     * @param dpi DPI
     */
    BadgeImage(int width, int height, int dpi) {
        this.dpi = dpi;

        BufferedImage background;

        try {
            background = ImageIO.read(new File("badgebackground.png"));
        } catch (IOException e) {
            background = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
        image = background;
        g2 = image.createGraphics();
    }

    void drawImage(Image image, Rectangle area) {
        if (image == null) return;
        g2.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2.drawImage(image, area.x, area.y, area.width, area.height, null);
    }

    void drawStretchedImage(Image image, Rectangle boundingArea) {
        if (image == null) return;
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);

        Dimension imageDimension = new Dimension(imageWidth, imageHeight);
        Dimension boundaryDimension = new Dimension(boundingArea.width, boundingArea.height);
        Dimension scaledDimension = getScaledDimension(imageDimension, boundaryDimension);

        Rectangle imageArea = new Rectangle(
            boundingArea.x,
            boundingArea.y,
            scaledDimension.width,
            scaledDimension.height
        );

        int heightDifference = boundingArea.height - imageArea.height;
        int widthDifference = boundingArea.width - imageArea.width;

        Rectangle centeredImage = new Rectangle(
            boundingArea.x + (widthDifference/2),
            boundingArea.y + (heightDifference/2),
            imageArea.width,
            imageArea.height
        );

        drawImage(image, centeredImage);
    }

    public static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {
        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;
    
        // first check if we need to scale width
        if (original_width > bound_width) {
            //scale width to fit
            new_width = bound_width;
            //scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }
    
        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            // The '* 125 / 100' part is an embarrassing hack to make scaled images 25% wider
            // since I couldn't figure out what was shrinking them by 25% with the amount of time I had
            new_width = (new_height * original_width * 125 / 100) / original_height;
        }

        return new Dimension(new_width, new_height);
    }

    /**
     * Draw a string centered in the given rectangle, stretched to fill it minus 10 pixels of padding
     * @param text Text to draw
     * @param rect Rectangle the text will fill
     * @param font Text font
     * @param color Text color
     */
    void drawStretchedCenteredString(String text, Rectangle rect, Font font, Color color) {
        drawStretchedCenteredString(text, rect, font, color, getInverseColor(color), 1, 0.0f);
    }

    void drawStretchedCenteredString(String text, Rectangle rect, Font font, Color color, int outlineWidth) {
        drawStretchedCenteredString(text, rect, font, color, getInverseColor(color), outlineWidth, 0.0f);
    }

    void drawStretchedCenteredString(String text, Rectangle rect, Font font, Color color, Color outlineColor, int outlineWidth) {
        drawStretchedCenteredString(text, rect, font, color, outlineColor, outlineWidth, 0.0f);
    }

    void drawStretchedCenteredString(String text, Rectangle rect, Font font, Color color, Color outlineColor, int outlineWidth, float maxFontSize) {
        Rectangle paddedRect = getPaddedRect(rect);
        final Font sizedFont = scaleFont(text, paddedRect, font, maxFontSize);
        drawCenteredString(text, rect, sizedFont, color, outlineColor, outlineWidth);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    void drawRotatedStretchedCenteredString(String text, Rectangle rect, Font font, Color color, float maxFontSize) {
        Rectangle rotatedBounds = new Rectangle(rect.x, rect.y, rect.height, rect.width);
        final Font sizedFont = scaleFont(text, rotatedBounds, font, maxFontSize);
        drawCenteredString(text, rect, sizedFont, color);
    }

    void drawStretchedLeftAlignedString(String text, Rectangle rect, Font font, Color color, float maxFontSize) {
        Rectangle paddedRect = getPaddedRect(rect);
        final Font sizedFont = scaleFont(text, paddedRect, font, maxFontSize);
        drawLeftAlignedString(text, paddedRect, sizedFont, color);
    }

    /**
     * Draw a string that is stretched to fill the given rectangle, minus a 10 pixel padding
     * @param text Text to draw
     * @param rect Rectangle the text will fill
     * @param font Text font
     * @param color Color color
     * @param outlineWidth int Outline width
     */
    void drawStretchedLeftAlignedString(String text, Rectangle rect, Font font, Color color, int outlineWidth, float maxFontSize) {
        Rectangle paddedRect = getPaddedRect(rect);
        final Font sizedFont = scaleFont(text, paddedRect, font, maxFontSize);
        drawLeftAlignedString(text, paddedRect, sizedFont, color, outlineWidth);
    }

    /**
     * Draw a string that is stretched to fill the given rectangle, minus a 10 pixel padding
     * @param text Text to draw
     * @param rect Rectangle the text will fill
     * @param font Text font
     * @param color Text color
     * @param outlineWidth int Outline width
     */
    void drawStretchedRightAlignedString(String text, Rectangle rect, Font font, Color color, int outlineWidth, float maxFontSize) {
        Rectangle paddedRect = getPaddedRect(rect);
        final Font sizedFont = scaleFont(text, paddedRect, font, maxFontSize);

        drawRightAlignedString(text, paddedRect, sizedFont, color, outlineWidth);
    }


    void drawCenteredString(String text, Rectangle rect, Font font, Color color) {
        drawCenteredString(text, rect, font, color, getInverseColor(color), 0);
    }

    void drawCenteredString(String text, Rectangle rect, Font font, Color color, Color outlineColor) {
        drawCenteredString(text, rect, font, color, outlineColor, 0);
    }

    void drawCenteredString(String text, Rectangle rect, Font font, Color color, int outlineWidth) {
        drawCenteredString(text, rect, font, color, getInverseColor(color), outlineWidth);
    }

    void drawCenteredString(String text, Rectangle rect, Font font, Color color, Color outlineColor, int outlineWidth) {
        FontMetrics metrics = g2.getFontMetrics(font);
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g2.setFont(font);

        drawTextOutline(text, color, outlineColor, x, y, outlineWidth);

        g2.setColor(color);
        g2.drawString(text, x, y);
    }


    void drawTextOutline(String text, Color color, int x, int y) {
        drawTextOutline(text, color, getInverseColor(color), x, y, 1);
    }

    void drawTextOutline(String text, Color color, int x, int y, int width) {
        drawTextOutline(text, color, getInverseColor(color), x, y, width);
    }

    void drawTextOutline(String text, Color color, Color outlineColor, int x, int y) {
        drawTextOutline(text, color, outlineColor, x, y, 1);
    }

    void drawTextOutline(String text, Color color, Color outlineColor, int x, int y, int width) {
        if (width <= 0) return;
        g2.setColor(outlineColor);
        g2.drawString(text, x-width, y);
        g2.drawString(text, x+width, y);
        g2.drawString(text, x, y-width);
        g2.drawString(text, x, y+width);
        g2.drawString(text, x-width, y-width);
        g2.drawString(text, x-width, y+width);
        g2.drawString(text, x+width, y-width);
        g2.drawString(text, x+width, y+width);
    }

    void drawLeftAlignedString(String text, Rectangle rect, Font font, Color color) {
        drawLeftAlignedString(text, rect, font, color, 0);
    }

    void drawLeftAlignedString(String text, Rectangle rect, Font font, Color color, int outlineWidth) {
        FontMetrics metrics = g2.getFontMetrics(font);
        int x = rect.x + 10;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();

        g2.setFont(font);
        drawTextOutline(text, color, x, y, outlineWidth);
        g2.setColor(color);
        g2.drawString(text, x, y);
    }

    void drawRightAlignedString(String text, Rectangle rect, Font font, Color color, int outlineWidth) {
        drawRightAlignedString(text, rect, font, color, getInverseColor(color), outlineWidth);
    }

    void drawRightAlignedString(String text, Rectangle rect, Font font, Color color, Color outlineColor, int outlineWidth) {
        FontMetrics metrics = g2.getFontMetrics(font);
        int x = rect.x + (rect.width - metrics.stringWidth(text));
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();

        g2.setFont(font);
        drawTextOutline(text, color, outlineColor, x, y, outlineWidth);
        g2.setColor(color);
        g2.drawString(text, x, y);
    }

    Font scaleFontByHeight(float fontSize, Rectangle rect, Font font) {
        int height = g2.getFontMetrics(font).getHeight();
        float fontSizeByHeight = ((float)rect.height / (float)height) * fontSize;
        return font.deriveFont(fontSizeByHeight);
    }

    Font scaleFont(String text, Rectangle rect, Font font, float maxFontSize) {
        float fontSizeByWidth = 100.0f;
        float fontSizeByHeight = 100.0f;
        Font tFont = font.deriveFont(fontSizeByWidth);
        int width = g2.getFontMetrics(tFont).stringWidth(text);
        fontSizeByWidth = (((float)rect.width / (float)width ) * fontSizeByWidth)-2;

        int height = g2.getFontMetrics(tFont).getHeight();
        fontSizeByHeight = ((float)rect.height / (float)height) * fontSizeByHeight;

        // Enforce maximum font size, if specified
        if (maxFontSize > 0.0f && fontSizeByHeight > maxFontSize) {
            fontSizeByHeight = maxFontSize;
        }
        if (maxFontSize > 0.0f && fontSizeByWidth > maxFontSize) {
            fontSizeByWidth = maxFontSize;
        }

        if (fontSizeByHeight < fontSizeByWidth) {
            return font.deriveFont(fontSizeByHeight);
        } else {
            return font.deriveFont(fontSizeByWidth);
        }
    }

    BufferedImage getImage() {
        return image;
    }

    void fillRect(final Rectangle rectangle, final Color color) {
        g2.setColor(color);
        g2.setBackground(color);
        g2.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    private Rectangle getPaddedRect(Rectangle rect) {
        if (rect.getHeight() > 40 && rect.getWidth() > 40) {
            return new Rectangle(rect.x+10, rect.y+10, rect.width-20, rect.height-20);
        } else {
            int paddingWidth = Math.round((float)rect.getWidth() * .1f);
            int paddingHeight = Math.round((float)rect.getHeight() * .1f);
            if (paddingWidth < 2) paddingWidth = 2;
            if (paddingHeight < 2) paddingHeight = 2;
            return new Rectangle(rect.x + paddingWidth,
                    rect.y + paddingHeight,
                    rect.width-(paddingWidth*2),
                    rect.height-(paddingHeight*2));
        }
    }

    /**
     * Returns the best text color for the given background color. Ideally chooses white text on dark
     * backgrounds and black text on light backgrounds. Uses the formula from
     * http://en.wikipedia.org/wiki/HSV_color_space%23Lightness
     * Based on https://stackoverflow.com/questions/1855884/determine-font-color-based-on-background-color
     * @param background Color of background
     * @return Color (Black or White)
     */
    static Color getInverseColor(Color background) {
        if (background == null) return Color.BLACK;
        // Counting the perceptive luminance - human eye favors green color...
        double a = 1 - (0.299 * background.getRed() + 0.587 * background.getGreen() + 0.114 * background.getBlue()) / 255;

        if (a < 0.5) {
            return Color.BLACK;
        } else {
            return Color.WHITE;
        }
    }

    void drawRotatedCenteredStrings(String[] text, Rectangle boundingBox, Font font, Color fgColor, boolean rotateRight, float maxFontSize) {
        if (text.length == 0) return;
        // Since lines will be drawn rotated, line height is based on WIDTH of the bounding box
        int lineHeight = (int) (boundingBox.getWidth() / text.length);

        // Font scaling code assumes horizontal text, build a bounding box that's "rotated"
        @SuppressWarnings("SuspiciousNameCombination")
        Rectangle rotatedBounds = new Rectangle(boundingBox.x, boundingBox.y, boundingBox.height, lineHeight);
        Font sizedFont = scaleFont(text[0], rotatedBounds, font, maxFontSize);

        // Find the smallest font needed for each line and use it for all lines
        for (int i = 1; i < text.length; i++) {
            Font tmpFont = scaleFont(text[i], rotatedBounds, font, maxFontSize);
            if (tmpFont.getSize() < sizedFont.getSize()) sizedFont = tmpFont;
        }

        for (int i = 0; i < text.length; i++) {
            @SuppressWarnings("SuspiciousNameCombination")
            Rectangle lineBoundingBox = new Rectangle(boundingBox.x + (i * lineHeight), boundingBox.y, lineHeight, boundingBox.height);
            AffineTransform orig = g2.getTransform();
            if (rotateRight) {
                g2.rotate(Math.PI/2, lineBoundingBox.getX() + (lineBoundingBox.getWidth()/2), lineBoundingBox.getY() + (lineBoundingBox.getHeight()/2));
                drawCenteredString(text[text.length-1-i], lineBoundingBox, sizedFont, fgColor);
            } else {
                g2.rotate(-Math.PI/2, lineBoundingBox.getX() + lineBoundingBox.getWidth()/1.5, lineBoundingBox.getY() + (lineBoundingBox.getHeight()/2));
                drawCenteredString(text[i], lineBoundingBox, sizedFont, fgColor);
            }
            g2.setTransform(orig);
        }
    }

    void drawCenteredStrings(String[] text, Rectangle boundingBox, Font font, Color fgColor, int outlineWidth, float maxFontSize, boolean yAxisCentering) {
        drawCenteredStrings(text, boundingBox, font, fgColor, getInverseColor(fgColor), outlineWidth, maxFontSize, yAxisCentering);
    }

    void drawCenteredStrings(String[] text, Rectangle boundingBox, Font font, Color fgColor, Color outlineColor, int outlineWidth, float maxFontSize, boolean yAxisCentering) {
        // Find initial line height
        int lineHeight = (int) (boundingBox.getHeight() / text.length);

        Rectangle lineBounds = new Rectangle(boundingBox.x, boundingBox.y, boundingBox.width, lineHeight);
        Font sizedFont = scaleFont(text[0], lineBounds, font, maxFontSize);

        // Find the smallest font needed for each line and use it for all lines
        for (int i = 1; i < text.length; i++) {
            Font tmpFont = scaleFont(text[i], lineBounds, font, maxFontSize);
            if (tmpFont.getSize() < sizedFont.getSize()) sizedFont = tmpFont;
        }
        lineHeight = sizedFont.getSize() + 15;   // Make line height close to actual line size with padding

        int yOffset = boundingBox.y; /* Upper edge of the bounding box */
        if (yAxisCentering) {
            yOffset += /* Half the height of box */ boundingBox.height / 2 - /* Half the height of lines */ (text.length * (lineHeight - 15)) / 2;
        }

        for (int i = 0; i < text.length; i++) {
            Rectangle lineBoundingBox = new Rectangle(boundingBox.x, yOffset + (i*lineHeight), boundingBox.width, lineHeight);
            drawCenteredString(text[i], lineBoundingBox, sizedFont, fgColor, outlineColor, outlineWidth);
        }
    }

    void drawStretchedCenteredStrings(String[] text, Rectangle boundingBox, Font font, Color fgColor, int outlineWidth, float maxFontSize) {
        // Find initial line height
        int lineHeight = (int) (boundingBox.getHeight() / text.length);

        Rectangle lineBounds = new Rectangle(boundingBox.x, boundingBox.y, boundingBox.width, lineHeight);
        Font sizedFont = scaleFont(text[0], lineBounds, font, maxFontSize);


        // Find the smallest font needed for each line and use it for all lines
        for (int i = 1; i < text.length; i++) {
            Font tmpFont = scaleFont(text[i], lineBounds, font, maxFontSize);
            if (tmpFont.getSize() < sizedFont.getSize()) sizedFont = tmpFont;
        }
        lineHeight = sizedFont.getSize() + 15;   // Make line height close to actual line size with padding

        for (int i = 0; i < text.length; i++) {
            Rectangle lineBoundingBox = new Rectangle(boundingBox.x, boundingBox.y + (i*lineHeight), boundingBox.width, lineHeight);
            drawStretchedCenteredString(text[i], lineBoundingBox, sizedFont, fgColor, getInverseColor(fgColor), outlineWidth, maxFontSize);
        }
    }

    void drawStretchedRightRotatedString(String text, Rectangle boundingBox, Font font, Color fgColor, float maxFontSize) {
        AffineTransform orig = g2.getTransform();
        g2.rotate(-Math.PI/2, boundingBox.getX() + (boundingBox.getWidth()/2), boundingBox.getY() + (boundingBox.getHeight()/2));
        drawRotatedStretchedCenteredString(text, boundingBox, font, fgColor, maxFontSize);
        g2.setTransform(orig);
    }

    void drawVerticalCenteredString(String ageStripeText, Rectangle ageBackground, Font font, Color fgColor, int outlineWidth) {
        drawVerticalCenteredString(ageStripeText, ageBackground, font, fgColor, outlineWidth, 0.0f);
    }

    void drawVerticalCenteredString(String ageStripeText, Rectangle ageBackground, Font font, Color fgColor, int outlineWidth, float maxFontSize) {
        String text = ageStripeText.toUpperCase();

        int letterBoundingBoxHeight = ageBackground.height / ageStripeText.length();

        // The +5 and +10 below are to make the letters just a touch bigger so they
        // aren't so far apart.
        for (int i = 0; i < ageStripeText.length(); i++) {
            Rectangle letterBoundingBox = new Rectangle(ageBackground.x,
                    ageBackground.y + (letterBoundingBoxHeight * i)+5,
                    ageBackground.width,
                    letterBoundingBoxHeight+10);
            drawStretchedCenteredString(text.substring(i, i+1), letterBoundingBox, font, fgColor, getInverseColor(fgColor), outlineWidth, maxFontSize);
        }
    }

    /**
     * Used for badge type text when bar is on horizontal bottom
     *
     * Previous usage:
     * if (titleText.length() < 7) {
     *     titleText = BadgeImage.buildTitleString(titleText);
     * }
     */
    static String buildTitleString(String input) {
        if (input == null) return "";
        char[] characters = input.toUpperCase().toCharArray();
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            output.append(characters[i]);
            if (i < characters.length-1) output.append(" ");
        }
        return output.toString();
    }

    public byte[] writePNGToByteArray() {
        try (
            ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }
}
