package org.kumoricon.registration.print;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.Sides;
import java.io.InputStream;

/**
 * Base class for implementing services that will send data to a printer installed on the server
 */
public abstract class PrintService {
    protected PrinterInfoService printerInfoService;

    public PrintService() {}

    public PrintService(PrinterInfoService printerInfoService) {
        this.printerInfoService = printerInfoService;
    }

    /**
     * Prints the given inputStream to the printer with the given name, or the default printer
     * if a printer with that name isn't found.
     *
     * @param inputStream Data stream (Usually PDF formatted)
     * @param printerName Destination printer name (case insensitive)
     * @param duplex Print the job double-sided
     */
    void printDocument(InputStream inputStream, String printerName, boolean duplex) throws PrintException {
        javax.print.PrintService printService;
        DocPrintJob job;

        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
        PrintRequestAttributeSet printRequestSet = new HashPrintRequestAttributeSet();
        if (duplex) {
            printRequestSet.add(Sides.DUPLEX);
        }

        Doc doc = new SimpleDoc(inputStream, flavor, null);
        if (printerInfoService == null) {
            throw new PrintException("PrintService.printDocument called when printerInfoService is null");
        }
        printService = printerInfoService.findPrinter(printerName);
        job = printService.createPrintJob();
        printRequestSet.add(MediaSizeName.INVOICE);
        printRequestSet.add(OrientationRequested.LANDSCAPE);
        job.print(doc, printRequestSet);
    }

}
