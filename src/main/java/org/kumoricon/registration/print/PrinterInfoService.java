package org.kumoricon.registration.print;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.util.ArrayList;
import java.util.List;

@Service
public class PrinterInfoService {
    private static final Logger log = LoggerFactory.getLogger(PrinterInfoService.class);

    public List<String> getPrinterNames() {
        List<String> printerNames = new ArrayList<>();
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);

        for (PrintService service : services) {
            printerNames.add(service.getName());
        }
        return printerNames;
    }

    /**
     * Returns a PrintService object for the printer with the given name, or the default printer if
     * no match is found.
     * @param name Printer name (case insensitive)
     * @return PrintService
     * @throws PrintException Exception if printer not found and default printer isn't set
     */
    public javax.print.PrintService findPrinter(String name) throws PrintException {
        name = name.toLowerCase().trim();
        javax.print.PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

        for (javax.print.PrintService printer : printServices) {
            String thisPrinterName = printer.getName().trim().toLowerCase();
            if (name.equals(thisPrinterName)) { return printer; }
        }

        log.error("Printer \"{}\" not found, no default printer found.", name);
        throw new PrintException("Error: Printer named '" + name + "' does not exist and no default printer set");
    }
}
