package org.kumoricon.registration.utility;

import org.springframework.stereotype.Service;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.util.ArrayList;
import java.util.List;

@Service
public class PrinterService {

    public PrinterService() {

    }

    public List<String> getPrinterNames() {
        List<String> printerNames = new ArrayList<>();
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);

        for (PrintService service : services) {
            printerNames.add(service.getName());
        }
        return printerNames;
    }
}
