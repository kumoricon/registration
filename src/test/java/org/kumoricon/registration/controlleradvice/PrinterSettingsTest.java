package org.kumoricon.registration.controlleradvice;

import org.junit.Test;
import org.springframework.security.core.parameters.P;

import static org.junit.Assert.*;

public class PrinterSettingsTest {

    @Test
    public void asCookieValue() {
        PrinterSettings p = new PrinterSettings("test", 0, 0);
        assertEquals("test|0|0", p.asCookieValue());

        PrinterSettings r = new PrinterSettings("test", -10, 100);
        assertEquals("test|-10|100", r.asCookieValue());
    }

    @Test
    public void asCookieValueNulls() {
        PrinterSettings p = new PrinterSettings(null, null, null);
        assertNull(p.getPrinterName());
        assertEquals(0, (int) p.getxOffset());
        assertEquals(0, (int) p.getyOffset());
        assertEquals("|0|0", p.asCookieValue());
    }

    @Test
    public void fromCookieValue() {
        PrinterSettings p = PrinterSettings.fromCookieValue("test|0|0");
        assertEquals("test", p.getPrinterName());
        assertEquals((Integer) 0, p.getxOffset());
        assertEquals((Integer) 0, p.getyOffset());
    }

    @Test
    public void fromCookieValueNegativeOffset() {
        PrinterSettings p = PrinterSettings.fromCookieValue("test|-10|-10");
        assertEquals("test", p.getPrinterName());
        assertEquals(-10, (int) p.getxOffset());
        assertEquals(-10, (int) p.getyOffset());
    }

    @Test
    public void fromCookieValuePositiveOffset() {
        PrinterSettings p = PrinterSettings.fromCookieValue("test|512|512");
        assertEquals("test", p.getPrinterName());
        assertEquals((Integer) 512, p.getxOffset());
        assertEquals((Integer) 512, p.getyOffset());
    }

    @Test
    public void fromCookieValueEmptyPrinterName() {
        PrinterSettings p = PrinterSettings.fromCookieValue("|0|0");
        assertNull(p.getPrinterName());
        assertEquals((Integer) 0, p.getxOffset());
        assertEquals((Integer) 0, p.getyOffset());
    }

}