/*
 * The MIT License
 *
 * Copyright 2026 TheFallenWarrior.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ffonline.controller;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Anna Jaqueline (TheFallenWarrior)
 */
public class PresentationTest {
    
    public PresentationTest(){
    }

    /**
     * Test of formatNumber6Digits method, of class Presentation.
     */
    @Test
    public void testFormatNumber6Digits(){
        System.out.println("Testing Presentation::formatNumber6Digits");
        
        // Valid values within range
        assertEquals("     0", Presentation.formatNumber6Digits(0));
        assertEquals("     1", Presentation.formatNumber6Digits(1));
        assertEquals("    10", Presentation.formatNumber6Digits(10));
        assertEquals("    99", Presentation.formatNumber6Digits(99));
        assertEquals("   100", Presentation.formatNumber6Digits(100));
        assertEquals("   255", Presentation.formatNumber6Digits(255));
        assertEquals("   256", Presentation.formatNumber6Digits(256));
        assertEquals("  1000", Presentation.formatNumber6Digits(1000));
        assertEquals("  9999", Presentation.formatNumber6Digits(9999));
        assertEquals(" 10000", Presentation.formatNumber6Digits(10000));
        assertEquals(" 65535", Presentation.formatNumber6Digits(65535));
        assertEquals(" 65536", Presentation.formatNumber6Digits(65536));
        assertEquals(" 99999", Presentation.formatNumber6Digits(99999));
        assertEquals("100000", Presentation.formatNumber6Digits(100000));
        assertEquals("999998", Presentation.formatNumber6Digits(999998));
        assertEquals("999999", Presentation.formatNumber6Digits(999999));

        // Overflow values from invalid input, these are observed values from FF1
        assertEquals("9999A0", Presentation.formatNumber6Digits(1000000));
        assertEquals("9999A1", Presentation.formatNumber6Digits(1000001));
        assertEquals("9999P5", Presentation.formatNumber6Digits(1000155));
        assertEquals("999900", Presentation.formatNumber6Digits(1000156));
        assertEquals("999935", Presentation.formatNumber6Digits(1000191));
        assertEquals("999983", Presentation.formatNumber6Digits(1055535));
        assertEquals("990000", Presentation.formatNumber6Digits(1055536));
        assertEquals("990001", Presentation.formatNumber6Digits(1055537));
        assertEquals("996438", Presentation.formatNumber6Digits(0x123456));
        assertEquals("999936", Presentation.formatNumber6Digits(0x496200));
        assertEquals("999934", Presentation.formatNumber6Digits(0xfffffe));
        assertEquals("999935", Presentation.formatNumber6Digits(0xffffff));
    }
}
