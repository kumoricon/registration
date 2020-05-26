package org.kumoricon.registration.controlleradvice;


import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CookieControllerAdviceTest {
    CookieControllerAdvice cca;

    @BeforeEach
    void setUp() {
        this.cca = new CookieControllerAdvice();
    }

    @Test
    void selectedPrinterHappyPath() {
        HttpServletRequest request = buildMockRequest();
        String printerName = cca.selectedPrinter(request);
        assertEquals("testPrinter", printerName);
    }

    @Test
    void selectedPrinterReturnsNullForBadCookieValue() {
        HttpServletRequest request = buildMockRequest();
        when(request.getCookies()).thenReturn(new Cookie[] {
                new Cookie(CookieControllerAdvice.PRINTER_COOKIE_NAME, "printer1")
        });
        String printerName = cca.selectedPrinter(request);
        assertNull(printerName);
    }

    @Test
    void selectedTillHappyPath() {
        HttpServletRequest request = buildMockRequest();
        String tillName = cca.selectedTill(request);
        assertEquals("54", tillName);
    }

    @Test
    void selectedTillReturnsNullForMissingCookie() {
        HttpServletRequest request = buildMockRequest();
        when(request.getCookies()).thenReturn(new Cookie[]{});
        String tillName = cca.selectedTill(request);
        assertNull(tillName);
    }


    private HttpServletRequest buildMockRequest() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[] {
                new Cookie(CookieControllerAdvice.PRINTER_COOKIE_NAME, "testPrinter|0|0"),
                new Cookie(CookieControllerAdvice.TILL_COOKIE_NAME, "54")
        });
        return request;
    }

}