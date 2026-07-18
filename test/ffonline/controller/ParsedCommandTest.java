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

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Anna Jaqueline (TheFallenWarrior)
 */
public class ParsedCommandTest {
    ParsedCommand empty;
    
    public ParsedCommandTest(){
    }
    
    @Before
    public void setUp(){
        empty = new ParsedCommand("", 0);
    }

    /**
     * Test of reparse method, of class ParsedCommand.
     */
    @Test
    public void testReparse(){
        System.out.println("Testing ParsedCommand::reparse");
        ParsedCommand p = new ParsedCommand("a \"1 2\" 3", 0);
        
        var p0 = p.reparse(0);
        var p1 = p.reparse(1);
        var p2 = p.reparse(2);
        var p3 = p.reparse(3);

        assertEquals(p.getVerb(), p0.getVerb());
        assertEquals(p.getVerb(), p1.getVerb());
        assertEquals(p.getVerb(), p2.getVerb());
        assertEquals(p.getVerb(), p3.getVerb());
        
        assertEquals(p.getArgs(), p0.getArgs());
        assertEquals(List.of("1 2"), p1.getArgs());
        assertEquals(List.of("1 2", "3"), p2.getArgs());
        assertEquals(List.of("1 2", "3"), p3.getArgs());
        
        assertEquals(p.getRest(), p0.getRest());
        assertEquals("3", p1.getRest());
        assertEquals("", p2.getRest());
        assertEquals("", p3.getRest());
        
        assertEquals(p.toString(), p0.toString());
        
        // reparse() must return a new ParsedCommand instead of modifying in-place
        assertNotSame(p, p0);
        assertNotSame(p, p1);
        assertNotSame(p, p2);
        assertNotSame(p, p3);
        
        assertEquals("a", p.getVerb());
        assertEquals(List.of(), p.getArgs());
        assertEquals("\"1 2\" 3", p.getRest());
    }

    /**
     * Test of toString method, of class ParsedCommand.
     */
    @Test
    public void testToString(){
        System.out.println("Testing ParsedCommand::toString");
        
        // NOTE: Current implementation of toString normalizes unquoted whitespaces,
        //  so the returned string may be different to the one passed to the
        //  constructor.
        assertEquals("", empty.toString().strip());
        assertEquals("a \"1 2\" 3", new ParsedCommand("a \"1 2\" 3", 0).toString());
        assertEquals("a \"1 2\" 3", new ParsedCommand("a \"1 2\" 3", 1).toString());
        assertEquals("a \"1 2\" 3", new ParsedCommand("a \"1 2\" 3", 2).toString());
        assertEquals("a \"1 2\" 3", new ParsedCommand("a \"1 2\" 3", 3).toString());
    }

    /**
     * Test of getVerb method, of class ParsedCommand.
     */
    @Test
    public void testGetVerb(){
        System.out.println("Testing ParsedCommand::getVerb");
        
        assertEquals("", empty.getVerb());
        assertEquals("a", new ParsedCommand("a", 0).getVerb());
        assertEquals("a", new ParsedCommand("a 1 2 3", 0).getVerb());
        assertEquals("a 1 2", new ParsedCommand("\"a 1 2\" 3", 0).getVerb());
    }

    /**
     * Test of getArgs method, of class ParsedCommand.
     */
    @Test
    public void testGetArgs(){
        System.out.println("Testing ParsedCommand::getArgs");
        
        assertEquals(List.of(), empty.getArgs());
        assertEquals(List.of("1", "2", "3"), new ParsedCommand("a 1 2 3", 3).getArgs());
        
        assertEquals(List.of(), new ParsedCommand("a \"1 2\" 3", 0).getArgs());
        assertEquals(List.of("1 2"), new ParsedCommand("a \"1 2\" 3", 1).getArgs());
        assertEquals(List.of("1 2", "3"), new ParsedCommand("a \"1 2\" 3", 2).getArgs());
        assertEquals(List.of("1 2", "3"), new ParsedCommand("a \"1 2\" 3", 3).getArgs());
        
        // Test if returned List is umodifiable
        assertThrows(UnsupportedOperationException.class, new ParsedCommand("a 1 2 3", 3).getArgs()::clear);
    }

    /**
     * Test of getRest method, of class ParsedCommand.
     */
    @Test
    public void testGetRest(){
        System.out.println("Testing ParsedCommand::getRest");
        
        assertEquals("", empty.getRest());
        assertEquals("1 2 3", new ParsedCommand("a 1 2 3", 0).getRest());
        assertEquals("2", new ParsedCommand("a 1 2", 1).getRest());
        assertEquals("", new ParsedCommand("a 1 2", 2).getRest());
        assertEquals("", new ParsedCommand("a 1 2", 3).getRest());
    }
    
}
