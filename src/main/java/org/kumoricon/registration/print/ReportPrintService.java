package org.kumoricon.registration.print;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.kumoricon.registration.settings.SettingsService;
import org.springframework.stereotype.Service;

import javax.print.PrintException;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.Date;

@Service
public class ReportPrintService extends PrintService {
    private final SettingsService settingsService;
    public ReportPrintService(SettingsService settingsService, PrinterInfoService printerInfoService) {
        super(printerInfoService);
        this.settingsService = settingsService;
    }

    public void printReport(String[] text, String title, String printerName) throws IOException, PrintException {
        PDDocument document = textToPDF(text, title);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.save(out);
        document.close();
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        printDocument(in, printerName, true);
    }

    public PDDocument textToPDF(String[] text, String title) throws IOException {

        // Define constants and perform calculations
        double PAGE_HEIGHT_INCHES = 11;
        double PAGE_WIDTH_INCHES = 8.5;
        int PRINTER_DPI = 72;
        int FONT_SIZE = 10;
        int MARGIN_DPI = 30;
        PDFont FONT = PDType1Font.HELVETICA;
        AffineTransform affinetransform = new AffineTransform();
        FontRenderContext frc = new FontRenderContext(affinetransform,true,true);
        Font font = new Font(FONT.getName(), Font.PLAIN, FONT_SIZE);
        int WIDTH_LETTER_W_DPI = (int)(font.getStringBounds("W", frc).getWidth());
        int FONT_LEADING = (int)(font.getStringBounds("W", frc).getHeight());
        if (FONT_LEADING <= 0) { FONT_LEADING = 14; }
        if (WIDTH_LETTER_W_DPI <= 0) { WIDTH_LETTER_W_DPI = 12; }
        int PAGE_HEIGHT_DPI = (int)Math.round(PAGE_HEIGHT_INCHES * PRINTER_DPI);
        int PAGE_WIDTH_DPI = (int)Math.round(PAGE_WIDTH_INCHES * PRINTER_DPI);
        int MAX_LINES_PER_PAGE = (int)(PAGE_HEIGHT_DPI - 2 * MARGIN_DPI) / FONT_LEADING;
        int MAX_LINE_LENGTH = (int)Math.round((PAGE_WIDTH_DPI - 2 * MARGIN_DPI) / WIDTH_LETTER_W_DPI);
        int TITLE_X = PAGE_WIDTH_DPI/2 - (int)(font.getStringBounds(title, frc).getWidth())/2;
        int TITLE_Y = PAGE_HEIGHT_DPI - 3*FONT_LEADING;
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        String dateString = formatter.format(date);
        int DATE_X = PAGE_WIDTH_DPI - (int)(font.getStringBounds(dateString, frc).getWidth()) - MARGIN_DPI;
        int DATE_Y = PAGE_HEIGHT_DPI - 2*FONT_LEADING;

        // Wrap any lines greater than MAX_LINE_LENGTH characters in length
        ArrayList<String> stringArray = new ArrayList<>();
        for (int i = 0; i < text.length; i++) {
            int lineLength = text[i].length();
            int numLineCharsProcessed = 0;
            while (numLineCharsProcessed < lineLength) {
                int numCharsLeft = lineLength - numLineCharsProcessed;
                if (numCharsLeft > MAX_LINE_LENGTH) { numCharsLeft = MAX_LINE_LENGTH; }
                stringArray.add(text[i].substring(numLineCharsProcessed, numLineCharsProcessed + numCharsLeft));
                numLineCharsProcessed += numCharsLeft;
            }
        }

        // Render the lines of text to pages in a PDF document
        PDDocument document = new PDDocument();

        int numLines = stringArray.size();
        int numPages = (int)Math.ceil((float)numLines / (float)MAX_LINES_PER_PAGE);
        int numLinesProcessed = 0;
        for (int i = 1; i <= numPages; i++) {
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.setLeading(FONT_LEADING);

            // Add page number
            String pageNum = "Page " + i + " / " + numPages;
            int PGNUM_X = PAGE_WIDTH_DPI/2 - (int)(font.getStringBounds(pageNum, frc).getWidth())/2;
            int PGNUM_Y = 2*FONT_LEADING;
            contentStream.beginText();
            contentStream.newLineAtOffset(PGNUM_X, PGNUM_Y);
            contentStream.showText(pageNum);
            contentStream.endText();

            // Add date
            contentStream.beginText();
            contentStream.newLineAtOffset(DATE_X, DATE_Y);
            contentStream.showText(dateString);
            contentStream.endText();

            // Add title
            contentStream.beginText();
            contentStream.newLineAtOffset(TITLE_X, TITLE_Y);
            contentStream.showText(title);
            contentStream.endText();


            for (int j = 0; j < MAX_LINES_PER_PAGE; j++) {
                if (numLinesProcessed < numLines) {
                    String line = stringArray.get(numLinesProcessed);
                    if (line != null && line != "") {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(MARGIN_DPI, PAGE_HEIGHT_DPI - MARGIN_DPI - j*FONT_LEADING - FONT_LEADING);
                        contentStream.showText(line);
                        contentStream.endText();
                    }
                    numLinesProcessed++;
                }
            }

            contentStream.close();
        }

        return document;
    }
}