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
import org.kumoricon.registration.model.tillsession.TillSessionDetailDTO;
import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.model.user.UserService;
import org.kumoricon.registration.settings.SettingsService;
import org.springframework.stereotype.Service;

import javax.print.PrintException;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.Date;
import java.util.List;

@Service
public class ReportPrintService extends PrintService {
    private final SettingsService settingsService;
    private final UserService userService;

    public ReportPrintService(SettingsService settingsService, PrinterInfoService printerInfoService, UserService userService) {
        super(printerInfoService);
        this.settingsService = settingsService;
        this.userService = userService;
    }

    private String[] populateTillReport(int currentUserId, int tillSessionId, TillSessionDetailDTO s) {
        String tillName = s.getTillName();
        User currentUser = userService.findById(currentUserId);
        String startTime = s.getStartTime().toString();
        String endTime = s.getEndTime().toString();
        List<TillSessionDetailDTO.TillSessionPaymentTotalDTO> totals = s.getPaymentTotals();
        List<TillSessionDetailDTO.TillSessionOrderDTO> orders = s.getOrderDTOs();
        ArrayList<String> stringArray = new ArrayList<>();
        stringArray.add("Name: " + currentUser.getFirstName() + " " + currentUser.getLastName());
        stringArray.add("Username: " + currentUser.getUsername());
        stringArray.add("Tillname: " + tillName);
        stringArray.add("Session ID: " + tillSessionId);
        stringArray.add("Start Time: " + startTime);
        stringArray.add("End Time: " + endTime);
        stringArray.add(" ");
        stringArray.add("Totals: ");
        for (TillSessionDetailDTO.TillSessionPaymentTotalDTO total : totals) {
            String totalString = total.getTotal().toString() + ": " + total.getType();
            stringArray.add(totalString);
        }
        stringArray.add(" ");
        stringArray.add("Orders:");
        for (TillSessionDetailDTO.TillSessionOrderDTO order : orders) {
            String orderString = order.getOrderId().toString() + ": " + order.getPayments();
            stringArray.add(orderString);
        }
        String[] data = new String[stringArray.size()];
        stringArray.toArray(data);
        return data;
    }

    public void printTillReport(int currentUserId, int tillSessionId, String printerName, TillSessionDetailDTO s) throws IOException, PrintException {
        String[] data = populateTillReport(currentUserId, tillSessionId, s);
        printReport(data, "Till Report", printerName);
    }

    public void saveTillReport(int currentUserId, int tillSessionId, String path, TillSessionDetailDTO s) throws IOException, PrintException {
        String[] data = populateTillReport(currentUserId, tillSessionId, s);
        saveReport(data, "Till Report", "/Volumes/Data/Kumoreg/test.pdf");
    }

    public void saveReport(String[] text, String title, String path) throws IOException, PrintException {
        PDDocument document = textToPDF(text, title);
        document.save(path);
        document.close();
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
        int FONT_LEADING = (int)(font.getStringBounds("W", frc).getHeight());
        if (FONT_LEADING <= 0) { FONT_LEADING = 14; }
        int PAGE_HEIGHT_DPI = (int)Math.round(PAGE_HEIGHT_INCHES * PRINTER_DPI);
        int PAGE_WIDTH_DPI = (int)Math.round(PAGE_WIDTH_INCHES * PRINTER_DPI);
        int MAX_LINES_PER_PAGE = (PAGE_HEIGHT_DPI - 2 * MARGIN_DPI) / FONT_LEADING;
        MAX_LINES_PER_PAGE -= 3;
        int TITLE_X = PAGE_WIDTH_DPI/2 - (int)(font.getStringBounds(title, frc).getWidth())/2;
        int TITLE_Y = PAGE_HEIGHT_DPI - 3*FONT_LEADING;
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        String dateString = formatter.format(date);
        int DATE_X = PAGE_WIDTH_DPI - (int)(font.getStringBounds(dateString, frc).getWidth()) - MARGIN_DPI;
        int DATE_Y = PAGE_HEIGHT_DPI - 2*FONT_LEADING;

        // Wrap lines that don't fit between margins
        int USABLE_SPACE_DPI = PAGE_WIDTH_DPI - 2*MARGIN_DPI;
        int CHAR_COUNT_THRESHOLD = (int)(USABLE_SPACE_DPI / font.getStringBounds("W", frc).getWidth());
        ArrayList<String> stringArray = new ArrayList<>();
        for (String s : text) {
            String line = s;
            int numChars = s.length();
            if (numChars < CHAR_COUNT_THRESHOLD) {
                stringArray.add(line);
            } else {
                int index = CHAR_COUNT_THRESHOLD;
                while (index < numChars) {
                    if (index + 1 == numChars) {
                        stringArray.add(line.substring(0, index + 1));
                    } else if ((int) (font.getStringBounds(line.substring(0, index), frc).getWidth()) > USABLE_SPACE_DPI) {
                        stringArray.add(line.substring(0, index));
                        line = line.substring(index);
                        index = -1;
                        numChars = line.length();
                    }
                    index++;
                }
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
            int PGNUM_Y = 3*FONT_LEADING;
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

            // Add content
            for (int j = 0; j < MAX_LINES_PER_PAGE; j++) {
                if (numLinesProcessed < numLines) {
                    String line = stringArray.get(numLinesProcessed);
                    if (line != null && !line.equals("")) {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(MARGIN_DPI,
                                PAGE_HEIGHT_DPI - MARGIN_DPI - j*FONT_LEADING - 3*FONT_LEADING);
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