/*
 * Copyright (C) 2014 BigTesting.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bigtesting.interpolatd.tests;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.bigtesting.interpolatd.Interpolator;
import org.bigtesting.interpolatd.Substitutor;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Luis Antunes
 */
public class TestInterpolator {
    
    private Interpolator interpolator;
    
    private Map<String, Value> map;
    
    @Before
    @SuppressWarnings("unchecked")
    public void beforeEachTest() {
        
        interpolator = new Interpolator();
        
        interpolator.when("[a-zA-Z0-9_]").prefixedBy(":").handleWith(new Substitutor() {
            public String substitute(String captured, Object arg) {
                return ((Map<String, Value>)arg).get(captured).getForPrefix();
            }
        });
        
        interpolator.when("[0-9]").enclosedBy("*[").and("]").handleWith(new Substitutor() {
            public String substitute(String captured, Object arg) {
                return ((Map<String, Value>)arg).get(captured).getForPrefixedBracketEnclosed();
            }
        });
        
        interpolator.when().enclosedBy("{").and("}").handleWith(new Substitutor() {
            public String substitute(String captured, Object arg) {
                return ((Map<String, Value>)arg).get(captured).getForBraceEnclosed();
            }
        });
        
        interpolator.when().enclosedBy("[").and("]").handleWith(new Substitutor() {
            public String substitute(String captured, Object arg) {
                return ((Map<String, Value>)arg).get(captured).getForBracketEnclosed();
            }
        });
        
        interpolator.escapeWith("^");
        
        map = new HashMap<String, Value>();
    }
    
    @Test
    public void testStringReturnedUnmodifiedWhenNoSubstitutionsArePresent() {
        
        map.put("foo", new Value().forPrefix("bar"));
        
        assertEquals("Hello World!", 
                interpolator.interpolate("Hello World!", map));
    }
    
    @Test
    public void testSinglePrefixedSubstitued() {
        
        map.put("name", new Value().forPrefix("Tim"));
        
        assertEquals("Hello Tim", 
                interpolator.interpolate("Hello :name", map));
    }
    
    @Test
    public void testSinglePrefixedSubstituedInPresenceOfOtherStandalonePrefixes() {
        
        map.put("name", new Value().forPrefix("Tim"));
        
        assertEquals("Hello : Tim :", 
                interpolator.interpolate("Hello : :name :", map));
    }
    
    @Test
    public void testSinglePrefixedDoublyPrefixedIsSubstitued() {
        
        map.put("name", new Value().forPrefix("Tim"));
        
        assertEquals("Hello :Tim", 
                interpolator.interpolate("Hello ::name", map));
    }
    
    @Test
    public void testSinglePrefixedEnclosedByPrefixesIsSubstitued() {
        
        map.put("name", new Value().forPrefix("Tim"));
        
        assertEquals("Hello :Tim:", 
                interpolator.interpolate("Hello ::name:", map));
    }
    
    @Test
    public void testSinglePrefixedSubstituedInPresenceOfOtherPrefixedTerms() {
        
        map.put("name", new Value().forPrefix("Tim"));
        
        assertEquals("Hello :someTerm Tim", 
                interpolator.interpolate("Hello :someTerm :name", map));
    }
    
    @Test
    public void testSinglePrefixedSubstituedWithPrefixContainingValue() {
        
        map.put("name", new Value().forPrefix(":Tim"));
        
        assertEquals("Hello :Tim", 
                interpolator.interpolate("Hello :name", map));
    }
    
    @Test
    public void testSimilarPrefixedSubstitued() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/name/Tim/John");
//        when(req.getRoute()).thenReturn(new Route("/name/:name/:name1"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("name", new Value().forPrefix("Tim"));
        map.put("name1", new Value().forPrefix("John"));
        
        assertEquals("Hello Tim John", 
                interpolator.interpolate("Hello :name :name1", map));
    }
    
    @Test
    public void testSimilarPrefixedNotSubstitued() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/name/Tim");
//        when(req.getRoute()).thenReturn(new Route("/name/:name"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("name", new Value().forPrefix("Tim"));
        
        assertEquals("Hello :name1", 
                interpolator.interpolate("Hello :name1", map));
    }
    
    @Test
    public void testPrefixedSubstitutedWithNameOfAnotherPrefix() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/name/:id/1");
//        when(req.getRoute()).thenReturn(new Route("/name/:name/:id"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("name", new Value().forPrefix(":id"));
        map.put("id", new Value().forPrefix(":1"));
        
        assertEquals("Hello :id 1", 
                interpolator.interpolate("Hello :name :id", map));
    }
    
    @Test
    public void testMultiplePrefixedSubstitued() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/name/John/Doe");
//        when(req.getRoute()).thenReturn(new Route("/name/:firstName/:lastName"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("firstName", new Value().forPrefix("John"));
        map.put("lastName", new Value().forPrefix("Doe"));
        
        assertEquals("Hello John Doe", 
                interpolator.interpolate("Hello :firstName :lastName", map));
    }
    
    @Test
    public void testMultiplePrefixedSubstituedInPresenceOfOtherPrefixed() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/name/John/Doe");
//        when(req.getRoute()).thenReturn(new Route("/name/:firstName/:lastName"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("firstName", new Value().forPrefix("John"));
        map.put("lastName", new Value().forPrefix("Doe"));
        
        assertEquals("Hello : John : Doe :", 
                interpolator.interpolate("Hello : :firstName : :lastName :", map));
    }
    
    @Test
    public void testMultiplePrefixedSubstituedWithPrefixContainingValues() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/name/:John/:Doe");
//        when(req.getRoute()).thenReturn(new Route("/name/:firstName/:lastName"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("firstName", new Value().forPrefix(":John"));
        map.put("lastName", new Value().forPrefix(":Doe"));
        
        assertEquals("Hello :John :Doe", 
                interpolator.interpolate("Hello :firstName :lastName", map));
    }
    
    @Test
    public void testSingleBraceEnclosedSubstitued() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/");
//        when(req.getRoute()).thenReturn(new Route("/"));
//        Session session = new Session();
//        session.set("name", "Tim");
//        when(req.getSession()).thenReturn(session);
        
        map.put("name", new Value().forBraceEnclosed("Tim"));
        
        assertEquals("Hello Tim", 
                interpolator.interpolate("Hello {name}", map));
    }
    
    @Test
    public void testBraceEnclosedWithPrefixedNameNotSubstituted() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John");
//        when(req.getRoute()).thenReturn(new Route("/:name"));
//        Session session = new Session();
//        session.set("name", ":name");
//        when(req.getSession()).thenReturn(session);
        
        map.put("name", new Value().forPrefix("John")
                                   .forBraceEnclosed(":name"));
        
        assertEquals("Hello :name", 
                interpolator.interpolate("Hello {name}", map));
    }
    
    @Test
    public void testPrefixedWithBraceEnclosedNameNotSubstituted() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/{name}");
//        when(req.getRoute()).thenReturn(new Route("/:name"));
//        Session session = new Session();
//        session.set("name", "Tim");
//        when(req.getSession()).thenReturn(session);
        
        map.put("name", new Value().forPrefix("{name}")
                                   .forBraceEnclosed("Tim"));
        
        assertEquals("Hello {name}", 
                interpolator.interpolate("Hello :name", map));
    }
    
    @Test
    public void testSingleBraceEnclosedSubstituedInPresenceOfOtherBraces() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/");
//        when(req.getRoute()).thenReturn(new Route("/"));
//        Session session = new Session();
//        session.set("name", "Tim");
//        when(req.getSession()).thenReturn(session);
        
        map.put("name", new Value().forBraceEnclosed("Tim"));
        
        assertEquals("Hello { Tim } {", 
                interpolator.interpolate("Hello { {name} } {", map));
    }
    
    @Test
    public void testSingleBraceEnclosedSubstituedInPresenceOfOtherBraces_NoSpaces() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/");
//        when(req.getRoute()).thenReturn(new Route("/"));
//        Session session = new Session();
//        session.set("name", "Tim");
//        when(req.getSession()).thenReturn(session);
        
        map.put("name", new Value().forBraceEnclosed("Tim"));
        
        assertEquals("Hello {Tim} {", 
                interpolator.interpolate("Hello {{name}} {", map));
    }
    
    @Test
    public void testSingleBraceEnclosedSubstituedInPresenceOfOtherBraceEnclosedTerms() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/");
//        when(req.getRoute()).thenReturn(new Route("/"));
//        Session session = new Session();
//        session.set("name", "Tim");
//        when(req.getSession()).thenReturn(session);
        
        map.put("name", new Value().forBraceEnclosed("Tim"));
        
        assertEquals("Hello {there} Tim", 
                interpolator.interpolate("Hello {there} {name}", map));
    }
    
    @Test
    public void testSingleBraceEnclosedSubstituedWithBraceEnclosedValue() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/");
//        when(req.getRoute()).thenReturn(new Route("/"));
//        Session session = new Session();
//        session.set("name", "{Tim}");
//        when(req.getSession()).thenReturn(session);
        
        map.put("name", new Value().forBraceEnclosed("{Tim}"));
        
        assertEquals("Hello {Tim}", 
                interpolator.interpolate("Hello {name}", map));
    }
    
    @Test
    public void testMultipleBraceEnclosedSubstitued() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/");
//        when(req.getRoute()).thenReturn(new Route("/"));
//        Session session = new Session();
//        session.set("firstName", "John");
//        session.set("lastName", "Doe");
//        when(req.getSession()).thenReturn(session);
        
        map.put("firstName", new Value().forBraceEnclosed("John"));
        map.put("lastName", new Value().forBraceEnclosed("Doe"));
        
        assertEquals("Hello John Doe", 
                interpolator.interpolate("Hello {firstName} {lastName}", map));
    }
    
    @Test
    public void testMultipleBraceEnclosedSubstituedInPresenceOfOtherBraces() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/");
//        when(req.getRoute()).thenReturn(new Route("/"));
//        Session session = new Session();
//        session.set("firstName", "John");
//        session.set("lastName", "Doe");
//        when(req.getSession()).thenReturn(session);
        
        map.put("firstName", new Value().forBraceEnclosed("John"));
        map.put("lastName", new Value().forBraceEnclosed("Doe"));
        
        assertEquals("Hello { John } { Doe } {", 
                interpolator.interpolate("Hello { {firstName} } { {lastName} } {", map));
    }
    
    @Test
    public void testMultipleBraceEnclosedSubstituedInPresenceOfOtherBraces_NoSpaces() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/");
//        when(req.getRoute()).thenReturn(new Route("/"));
//        Session session = new Session();
//        session.set("firstName", "John");
//        session.set("lastName", "Doe");
//        when(req.getSession()).thenReturn(session);
        
        map.put("firstName", new Value().forBraceEnclosed("John"));
        map.put("lastName", new Value().forBraceEnclosed("Doe"));
        
        assertEquals("Hello {John} {Doe} {", 
                interpolator.interpolate("Hello {{firstName}} {{lastName}} {", map));
    }
    
    @Test
    public void testMultipleBraceEnclosedSubstituedInPresenceOfOtherBraceEnclosedTerms() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/");
//        when(req.getRoute()).thenReturn(new Route("/"));
//        Session session = new Session();
//        session.set("firstName", "John");
//        session.set("lastName", "Doe");
//        when(req.getSession()).thenReturn(session);
        
        map.put("firstName", new Value().forBraceEnclosed("John"));
        map.put("lastName", new Value().forBraceEnclosed("Doe"));
        
        assertEquals("Hello {there} John Doe", 
                interpolator.interpolate("Hello {there} {firstName} {lastName}", map));
    }
    
    @Test
    public void testMultipleBraceEnclosedSubstituedWithBraceEnclosedValue() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/");
//        when(req.getRoute()).thenReturn(new Route("/"));
//        Session session = new Session();
//        session.set("firstName", "{John}");
//        session.set("lastName", "{Doe}");
//        when(req.getSession()).thenReturn(session);
        
        map.put("firstName", new Value().forBraceEnclosed("{John}"));
        map.put("lastName", new Value().forBraceEnclosed("{Doe}"));
        
        assertEquals("Hello {John} {Doe}", 
                interpolator.interpolate("Hello {firstName} {lastName}", map));
    }
    
    @Test
    public void testPrefixedAndBraceEnclosed_PrefixedValuesComeFirst() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/greet/Mr.");
//        when(req.getRoute()).thenReturn(new Route("/greet/:salutation"));
//        Session session = new Session();
//        session.set("firstName", "John");
//        session.set("lastName", "Doe");
//        when(req.getSession()).thenReturn(session);
        
        map.put("salutation", new Value().forPrefix("Mr."));
        map.put("firstName", new Value().forBraceEnclosed("John"));
        map.put("lastName", new Value().forBraceEnclosed("Doe"));
        
        assertEquals("Hello Mr. John Doe", 
                interpolator.interpolate("Hello :salutation {firstName} {lastName}", map));
    }
    
    @Test
    public void testPrefixedAndBraceEnclosed_BraceEnclosedComeFirst() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/greet/John/Doe");
//        when(req.getRoute()).thenReturn(new Route("/greet/:firstName/:lastName"));
//        Session session = new Session();
//        session.set("salutation", "Mr.");
//        when(req.getSession()).thenReturn(session);
        
        map.put("salutation", new Value().forBraceEnclosed("Mr."));
        map.put("firstName", new Value().forPrefix("John"));
        map.put("lastName", new Value().forPrefix("Doe"));
        
        assertEquals("Hello Mr. John Doe", 
                interpolator.interpolate("Hello {salutation} :firstName :lastName", map));
    }
    
    @Test
    public void testColonPrefixedBraceEnclosed() {
        
        /*
         * In this test, the result is not "Hello John" because
         * :{name} is not a valid prefixed variable, as its name
         * contains non-alphanumeric characters.
         */
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/name/John");
//        when(req.getRoute()).thenReturn(new Route("/name/:{name}"));
//        Session session = new Session();
//        session.set("name", "Tim");
//        when(req.getSession()).thenReturn(session);
        
        map.put("{name}", new Value().forPrefix("John"));
        map.put("name", new Value().forBraceEnclosed("Tim"));
        
        assertEquals("Hello :Tim", 
                interpolator.interpolate("Hello :{name}", map));
    }
    
    @Test
    public void testBraceEnclosedPrefixedValueSubstitutesBraceEnclosed_OneOccurrence() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/name/John");
//        when(req.getRoute()).thenReturn(new Route("/name/:name"));
//        Session session = new Session();
//        session.set(":name", "Tim");
//        when(req.getSession()).thenReturn(session);
        
        map.put("name", new Value().forPrefix("John"));
        map.put(":name", new Value().forBraceEnclosed("Tim"));
        
        assertEquals("Hello Tim", 
                interpolator.interpolate("Hello {:name}", map));
    }
    
    @Test
    public void testBraceEnclosedPrefixedValueSubstitutesBraceEnclosed_TwoOccurrences() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/name/John/Doe");
//        when(req.getRoute()).thenReturn(new Route("/name/:first/:last"));
//        Session session = new Session();
//        session.set(":first", "Tom");
//        session.set(":last", "Jerry");
//        when(req.getSession()).thenReturn(session);
        
        map.put("first", new Value().forPrefix("John"));
        map.put("last", new Value().forPrefix("Doe"));
        map.put(":first", new Value().forBraceEnclosed("Tom"));
        map.put(":last", new Value().forBraceEnclosed("Jerry"));
        
        assertEquals("Hello Tom Jerry", 
                interpolator.interpolate("Hello {:first} {:last}", map));
    }
    
    @Test
    public void testSquareBracketUnknownValuesIgnored() throws Exception {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getBodyAsStream()).thenReturn(body("Hello World!"));
//        when(req.getUndecodedPath()).thenReturn("/");
//        when(req.getRoute()).thenReturn(new Route("/"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("request.body", new Value().forBracketEnclosed("Hello World!"));
        
        assertEquals("Message: [some.value]", 
                interpolator.interpolate("Message: [some.value]", map));
    }
    
    @Test
    public void testSquareBracketSubstituted() throws Exception {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getBodyAsStream()).thenReturn(body("Hello World!"));
//        when(req.getUndecodedPath()).thenReturn("/");
//        when(req.getRoute()).thenReturn(new Route("/"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("request.body", new Value().forBracketEnclosed("Hello World!"));
        
        assertEquals("Message: Hello World!", 
                interpolator.interpolate("Message: [request.body]", map));
    }
    
    @Test
    public void testSquareBracketSubstituedInPresenceOfOtherSquareBrackets() throws Exception {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getBodyAsStream()).thenReturn(body("Hello World!"));
//        when(req.getUndecodedPath()).thenReturn("/");
//        when(req.getRoute()).thenReturn(new Route("/"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("request.body", new Value().forBracketEnclosed("Hello World!"));
        
        assertEquals("Message: [ Hello World! ] [", 
                interpolator.interpolate("Message: [ [request.body] ] [", map));
    }
    
    @Test
    public void testSquareBracketSubstituedInPresenceOfOtherSquareBrackets_NoSpaces() throws Exception {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getBodyAsStream()).thenReturn(body("Hello World!"));
//        when(req.getUndecodedPath()).thenReturn("/");
//        when(req.getRoute()).thenReturn(new Route("/"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("request.body", new Value().forBracketEnclosed("Hello World!"));
        
        assertEquals("Message: [Hello World!] [", 
                interpolator.interpolate("Message: [[request.body]] [", map));
    }
    
    @Test
    public void testSquareBracketSubstituedInPresenceOfOtherSquareBracketEnclosedTerms() throws Exception {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getBodyAsStream()).thenReturn(body("Tim"));
//        when(req.getUndecodedPath()).thenReturn("/");
//        when(req.getRoute()).thenReturn(new Route("/"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("request.body", new Value().forBracketEnclosed("Tim"));
        
        assertEquals("Hello [there] Tim", 
                interpolator.interpolate("Hello [there] [request.body]", map));
    }
    
    @Test
    public void testSquareBracketSubstituedWithSquareBracketEnclosedValue() throws Exception {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getBodyAsStream()).thenReturn(body("[Tim]"));
//        when(req.getUndecodedPath()).thenReturn("/");
//        when(req.getRoute()).thenReturn(new Route("/"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("request.body", new Value().forBracketEnclosed("[Tim]"));
        
        assertEquals("Hello [Tim]", 
                interpolator.interpolate("Hello [request.body]", map));
    }
    
    @Test
    public void testPrefixedAndBraceEnclosedAndSquareBracketUsedTogether() throws Exception {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getBodyAsStream()).thenReturn(body("Doe"));
//        when(req.getUndecodedPath()).thenReturn("/name/John");
//        when(req.getRoute()).thenReturn(new Route("/name/:name"));
//        Session session = new Session();
//        session.set("salutation", "Mr.");
//        when(req.getSession()).thenReturn(session);
        
        map.put("salutation", new Value().forBraceEnclosed("Mr."));
        map.put("name", new Value().forPrefix("John"));
        map.put("request.body", new Value().forBracketEnclosed("Doe"));
        
        assertEquals("Hello Mr. John Doe", 
                interpolator.interpolate("Hello {salutation} :name [request.body]", map));
    }
    
    @Test
    public void testSinglePrefixedSquareBracketSubstitued() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/name/Tim");
//        when(req.getRoute()).thenReturn(new Route("/name/*"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("0", new Value().forPrefixedBracketEnclosed("Tim"));
        
        assertEquals("Hello Tim", 
                interpolator.interpolate("Hello *[0]", map));
    }
    
    @Test
    public void testSinglePrefixedSquareBracketSubstituedMultipleTimes() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/name/Tim");
//        when(req.getRoute()).thenReturn(new Route("/name/*"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("0", new Value().forPrefixedBracketEnclosed("Tim"));
        
        assertEquals("Hello Tim Tim", 
                interpolator.interpolate("Hello *[0] *[0]", map));
    }
    
    @Test
    public void testSinglePrefixedSquareBracketNotSubstituedWithIncorrectIndex() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/name/Tim");
//        when(req.getRoute()).thenReturn(new Route("/name/*"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("0", new Value().forPrefixedBracketEnclosed("Tim"));
        
        assertEquals("Hello *[1]", 
                interpolator.interpolate("Hello *[1]", map));
    }
    
    @Test
    public void testSinglePrefixedSquareBracketNotSubstituedIfFormattedIncorrectly() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/name/Tim");
//        when(req.getRoute()).thenReturn(new Route("/name/*"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("0", new Value().forPrefixedBracketEnclosed("Tim"));
        
        assertEquals("Hello *[ 0]", 
                interpolator.interpolate("Hello *[ 0]", map));
    }
    
    @Test
    public void testSinglePrefixedSquareBracketSubstituedWithSplatContainingValue() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/name/*[0]");
//        when(req.getRoute()).thenReturn(new Route("/name/*"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("0", new Value().forPrefixedBracketEnclosed("*[0]"));
        
        assertEquals("Hello *[0]", 
                interpolator.interpolate("Hello *[0]", map));
    }
    
    @Test
    public void testSinglePrefixedSquareBracketSubstituedWithEmptyValue() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/name/");
//        when(req.getRoute()).thenReturn(new Route("/name/*"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("0", new Value().forPrefixedBracketEnclosed(""));
        
        assertEquals("Hello ", 
                interpolator.interpolate("Hello *[0]", map));
    }
    
    @Test
    public void testPrefixedSquareBracketSubstituedWithDoubleDigitIndex() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/name/a/b/c/d/e/f/g/h/i/j/k");
//        when(req.getRoute()).thenReturn(new Route("/name/*/*/*/*/*/*/*/*/*/*/*"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("10", new Value().forPrefixedBracketEnclosed("k"));
        
        assertEquals("Hello k", 
                interpolator.interpolate("Hello *[10]", map));
    }
    
    @Test
    public void testTwoPrefixedSquareBracketsSubstitued() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/name/John/Doe");
//        when(req.getRoute()).thenReturn(new Route("/name/*/*"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("0", new Value().forPrefixedBracketEnclosed("John"));
        map.put("1", new Value().forPrefixedBracketEnclosed("Doe"));
        
        assertEquals("Hello John Doe", 
                interpolator.interpolate("Hello *[0] *[1]", map));
    }
    
    @Test
    public void testOnlySecondOfTwoPrefixedSquareBracketsSubstitued() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/name/John/Doe");
//        when(req.getRoute()).thenReturn(new Route("/name/*/*"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("0", new Value().forPrefixedBracketEnclosed("John"));
        map.put("1", new Value().forPrefixedBracketEnclosed("Doe"));
        
        assertEquals("Hello Doe", 
                interpolator.interpolate("Hello *[1]", map));
    }
    
    @Test
    public void testSubstitutionIncreasesOverallLengthOfString() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/hello/there");
//        when(req.getRoute()).thenReturn(new Route("/*/*"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("0", new Value().forPrefixedBracketEnclosed("hello"));
        map.put("1", new Value().forPrefixedBracketEnclosed("there"));
        
        assertEquals("hello there", 
                interpolator.interpolate("*[0] *[1]", map));
    }
    
    @Test
    public void testEscapeWithSinglePrefixedWhichExists() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John");
//        when(req.getRoute()).thenReturn(new Route("/:name"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("name", new Value().forPrefix("John"));
        
        assertEquals("Hello :name", 
                interpolator.interpolate("Hello ^:name", map));
    }
    
    @Test
    public void testEscapeWithSinglePrefixedWhichDoesNotExist() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John");
//        when(req.getRoute()).thenReturn(new Route("/:id"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("id", new Value().forPrefix("John"));
        
        assertEquals("Hello :name", 
                interpolator.interpolate("Hello ^:name", map));
    }
    
    @Test
    public void testEscapeWithMultiplePrefixed_OneEscaped() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John/Doe");
//        when(req.getRoute()).thenReturn(new Route("/:name1/:name2"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("name1", new Value().forPrefix("John"));
        map.put("name2", new Value().forPrefix("Doe"));
        
        assertEquals("Hello :name1 Doe", 
                interpolator.interpolate("Hello ^:name1 :name2", map));
    }
    
    @Test
    public void testEscapeWithMultiplePrefixed_MultipleEscaped() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John/Doe");
//        when(req.getRoute()).thenReturn(new Route("/:name1/:name2"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("name1", new Value().forPrefix("John"));
        map.put("name2", new Value().forPrefix("Doe"));
        
        assertEquals("Hello :name1 :name2", 
                interpolator.interpolate("Hello ^:name1 ^:name2", map));
    }
    
    @Test
    public void testEscapeWithMultiplePrefixed_MultipleEscapedConcatenated() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John/Doe");
//        when(req.getRoute()).thenReturn(new Route("/:name1/:name2"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("name1", new Value().forPrefix("John"));
        map.put("name2", new Value().forPrefix("Doe"));
        
        assertEquals("Hello :name1:name2", 
                interpolator.interpolate("Hello ^:name1^:name2", map));
    }
    
    @Test
    public void testDoubleEscapeWithPrefixed() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John");
//        when(req.getRoute()).thenReturn(new Route("/:name"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("name", new Value().forPrefix("John"));
        
        assertEquals("Hello ^John", 
                interpolator.interpolate("Hello ^^:name", map));
    }
    
    @Test
    public void testTripleEscapeWithPrefixed() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John");
//        when(req.getRoute()).thenReturn(new Route("/:name"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("name", new Value().forPrefix("John"));
        
        assertEquals("Hello ^:name", 
                interpolator.interpolate("Hello ^^^:name", map));
    }
    
    @Test
    public void testQuadrupleEscapeWithPrefixed() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John");
//        when(req.getRoute()).thenReturn(new Route("/:name"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("name", new Value().forPrefix("John"));
        
        assertEquals("Hello ^^John", 
                interpolator.interpolate("Hello ^^^^:name", map));
    }
    
    @Test
    public void testEscapeOnItsOwn() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John");
//        when(req.getRoute()).thenReturn(new Route("/:name"));
//        when(req.getSession()).thenReturn(null);
        
        assertEquals("Hello ^ there", 
                interpolator.interpolate("Hello ^ there", map));
    }
    
    @Test
    public void testDoubleEscapeOnItsOwn() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John");
//        when(req.getRoute()).thenReturn(new Route("/:name"));
//        when(req.getSession()).thenReturn(null);
        
        assertEquals("Hello ^^ there", 
                interpolator.interpolate("Hello ^^ there", map));
    }
    
    @Test
    public void testTripleEscapeOnItsOwn() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John");
//        when(req.getRoute()).thenReturn(new Route("/:name"));
//        when(req.getSession()).thenReturn(null);
        
        assertEquals("Hello ^^^ there", 
                interpolator.interpolate("Hello ^^^ there", map));
    }
    
    @Test
    public void testQuadrupleEscapeOnItsOwn() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John");
//        when(req.getRoute()).thenReturn(new Route("/:name"));
//        when(req.getSession()).thenReturn(null);
        
        assertEquals("Hello ^^^^ there", 
                interpolator.interpolate("Hello ^^^^ there", map));
    }
    
    @Test
    public void testEscapeOnItsOwnWithPrefixed() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John");
//        when(req.getRoute()).thenReturn(new Route("/:name"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("name", new Value().forPrefix("John"));
        
        assertEquals("Hello ^ John", 
                interpolator.interpolate("Hello ^ :name", map));
    }
    
    @Test
    public void testDoubleEscapeOnItsOwnWithPrefixed() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John");
//        when(req.getRoute()).thenReturn(new Route("/:name"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("name", new Value().forPrefix("John"));
        
        assertEquals("Hello ^^ John", 
                interpolator.interpolate("Hello ^^ :name", map));
    }
    
    @Test
    public void testTripleEscapeOnItsOwnWithPrefixed() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John");
//        when(req.getRoute()).thenReturn(new Route("/:name"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("name", new Value().forPrefix("John"));
        
        assertEquals("Hello ^^^ John", 
                interpolator.interpolate("Hello ^^^ :name", map));
    }
    
    @Test
    public void testQuadrupleEscapeOnItsOwnWithPrefixed() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John");
//        when(req.getRoute()).thenReturn(new Route("/:name"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("name", new Value().forPrefix("John"));
        
        assertEquals("Hello ^^^^ John", 
                interpolator.interpolate("Hello ^^^^ :name", map));
    }
    
    @Test
    public void testEscapeNextToNonInterpolatedValue() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John");
//        when(req.getRoute()).thenReturn(new Route("/:name"));
//        when(req.getSession()).thenReturn(null);
        
        assertEquals("Hello ^there", 
                interpolator.interpolate("Hello ^there", map));
    }
    
    @Test
    public void testDoubleEscapeNextToNonInterpolatedValue() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John");
//        when(req.getRoute()).thenReturn(new Route("/:name"));
//        when(req.getSession()).thenReturn(null);
        
        assertEquals("Hello ^^there", 
                interpolator.interpolate("Hello ^^there", map));
    }
    
    @Test
    public void testTripleEscapeNextToNonInterpolatedValue() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John");
//        when(req.getRoute()).thenReturn(new Route("/:name"));
//        when(req.getSession()).thenReturn(null);
        
        assertEquals("Hello ^^^there", 
                interpolator.interpolate("Hello ^^^there", map));
    }
    
    @Test
    public void testQuadrupleEscapeNextToNonInterpolatedValue() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John");
//        when(req.getRoute()).thenReturn(new Route("/:name"));
//        when(req.getSession()).thenReturn(null);
        
        assertEquals("Hello ^^^^there", 
                interpolator.interpolate("Hello ^^^^there", map));
    }
    
    @Test
    public void testEscapeAfterPrefixed() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John");
//        when(req.getRoute()).thenReturn(new Route("/:name"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("name", new Value().forPrefix("John"));
        
        assertEquals("Hello John^", 
                interpolator.interpolate("Hello :name^", map));
    }
    
    @Test
    public void testDoubleEscapeAfterPrefixed() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John");
//        when(req.getRoute()).thenReturn(new Route("/:name"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("name", new Value().forPrefix("John"));
        
        assertEquals("Hello John^^", 
                interpolator.interpolate("Hello :name^^", map));
    }
    
    @Test
    public void testTripleEscapeAfterPrefixed() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John");
//        when(req.getRoute()).thenReturn(new Route("/:name"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("name", new Value().forPrefix("John"));
        
        assertEquals("Hello John^^^", 
                interpolator.interpolate("Hello :name^^^", map));
    }
    
    @Test
    public void testQuadrupleEscapeAfterPrefixed() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John");
//        when(req.getRoute()).thenReturn(new Route("/:name"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("name", new Value().forPrefix("John"));
        
        assertEquals("Hello John^^^^", 
                interpolator.interpolate("Hello :name^^^^", map));
    }
    
    @Test
    public void testEscapeAfterNonInterpolatedValue() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John");
//        when(req.getRoute()).thenReturn(new Route("/:name"));
//        when(req.getSession()).thenReturn(null);
        
        assertEquals("Hello there^", 
                interpolator.interpolate("Hello there^", map));
    }
    
    @Test
    public void testDoubleEscapeAfterNonInterpolatedValue() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John");
//        when(req.getRoute()).thenReturn(new Route("/:name"));
//        when(req.getSession()).thenReturn(null);
        
        assertEquals("Hello there^^", 
                interpolator.interpolate("Hello there^^", map));
    }
    
    @Test
    public void testTripleEscapeAfterNonInterpolatedValue() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John");
//        when(req.getRoute()).thenReturn(new Route("/:name"));
//        when(req.getSession()).thenReturn(null);
        
        assertEquals("Hello there^^^", 
                interpolator.interpolate("Hello there^^^", map));
    }
    
    @Test
    public void testQuadrupleEscapeAfterNonInterpolatedValue() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John");
//        when(req.getRoute()).thenReturn(new Route("/:name"));
//        when(req.getSession()).thenReturn(null);
        
        assertEquals("Hello there^^^^", 
                interpolator.interpolate("Hello there^^^^", map));
    }
    
    @Test
    public void testEscapeIgnoredInsideBraceEnclosed() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/");
//        when(req.getRoute()).thenReturn(new Route("/"));
//        Session session = new Session();
//        session.set("^name", "Tim");
//        when(req.getSession()).thenReturn(session);
        
        map.put("^name", new Value().forBraceEnclosed("Tim"));
        
        assertEquals("Hello Tim", 
                interpolator.interpolate("Hello {^name}", map));
    }
    
    @Test
    public void testEscapeWithSingleBraceEnclosedWhichExists() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/");
//        when(req.getRoute()).thenReturn(new Route("/"));
//        Session session = new Session();
//        session.set("name", "Tim");
//        when(req.getSession()).thenReturn(session);
        
        map.put("name", new Value().forBraceEnclosed("Tim"));
        
        assertEquals("Hello {name}", 
                interpolator.interpolate("Hello ^{name}", map));
    }
    
    @Test
    public void testEscapeWithSingleBraceEnclosedWhichDoesNotExist() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/");
//        when(req.getRoute()).thenReturn(new Route("/"));
//        Session session = new Session();
//        session.set("id", "Tim");
//        when(req.getSession()).thenReturn(session);
        
        map.put("id", new Value().forBraceEnclosed("Tim"));
        
        assertEquals("Hello {name}", 
                interpolator.interpolate("Hello ^{name}", map));
    }
    
    @Test
    public void testEscapeWithMultipleBraceEnclosed_OneEscaped() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/");
//        when(req.getRoute()).thenReturn(new Route("/"));
//        Session session = new Session();
//        session.set("firstName", "John");
//        session.set("lastName", "Doe");
//        when(req.getSession()).thenReturn(session);
        
        map.put("firstName", new Value().forBraceEnclosed("John"));
        map.put("lastName", new Value().forBraceEnclosed("Doe"));
        
        assertEquals("Hello {firstName} Doe", 
                interpolator.interpolate("Hello ^{firstName} {lastName}", map));
    }
    
    @Test
    public void testEscapeWithMultipleBraceEnclosed_MultipleEscaped() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/");
//        when(req.getRoute()).thenReturn(new Route("/"));
//        Session session = new Session();
//        session.set("firstName", "John");
//        session.set("lastName", "Doe");
//        when(req.getSession()).thenReturn(session);
        
        map.put("firstName", new Value().forBraceEnclosed("John"));
        map.put("lastName", new Value().forBraceEnclosed("Doe"));
        
        assertEquals("Hello {firstName} {lastName}", 
                interpolator.interpolate("Hello ^{firstName} ^{lastName}", map));
    }
    
    @Test
    public void testEscapeWithSingleSquareBracketWhichExists() throws Exception {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getRequestParameter("name")).thenReturn("Tim");
//        when(req.getUndecodedPath()).thenReturn("/");
//        when(req.getRoute()).thenReturn(new Route("/"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("request?name", new Value().forBracketEnclosed("Tim"));
        
        assertEquals("Hello [request?name]", 
                interpolator.interpolate("Hello ^[request?name]", map));
    }
    
    @Test
    public void testEscapeWithSingleSquareBracketWhichDoesNotExist() throws Exception {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getRequestParameter("id")).thenReturn("Tim");
//        when(req.getUndecodedPath()).thenReturn("/");
//        when(req.getRoute()).thenReturn(new Route("/"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("request?id", new Value().forBracketEnclosed("Tim"));
        
        assertEquals("Hello [request?name]", 
                interpolator.interpolate("Hello ^[request?name]", map));
    }
    
    @Test
    public void testEscapeWithMultipleSquareBrackets_OneEscaped() throws Exception {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getRequestParameter("name1")).thenReturn("John");
//        when(req.getRequestParameter("name2")).thenReturn("Doe");
//        when(req.getUndecodedPath()).thenReturn("/");
//        when(req.getRoute()).thenReturn(new Route("/"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("request?name1", new Value().forBracketEnclosed("John"));
        map.put("request?name2", new Value().forBracketEnclosed("Doe"));
        
        assertEquals("Hello [request?name1] Doe", 
                interpolator.interpolate("Hello ^[request?name1] [request?name2]", map));
    }
    
    @Test
    public void testEscapeWithMultipleSquareBrackets_MultipleEscaped() throws Exception {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getRequestParameter("name1")).thenReturn("John");
//        when(req.getRequestParameter("name2")).thenReturn("Doe");
//        when(req.getUndecodedPath()).thenReturn("/");
//        when(req.getRoute()).thenReturn(new Route("/"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("request?name1", new Value().forBracketEnclosed("John"));
        map.put("request?name2", new Value().forBracketEnclosed("Doe"));
        
        assertEquals("Hello [request?name1] [request?name2]", 
                interpolator.interpolate("Hello ^[request?name1] ^[request?name2]", map));
    }
    
    @Test
    public void testEscapeWithSinglePrefixedSquareBracketWhichExists() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/name/Tim");
//        when(req.getRoute()).thenReturn(new Route("/name/*"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("0", new Value().forPrefixedBracketEnclosed("Tim"));
        
        assertEquals("Hello *[0]", 
                interpolator.interpolate("Hello ^*[0]", map));
    }
    
    @Test
    public void testEscapeWithSinglePrefixedSquareBracketWhichDoesNotExist() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/name/Tim");
//        when(req.getRoute()).thenReturn(new Route("/name/:name"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("name", new Value().forPrefix("Tim"));
        
        assertEquals("Hello *[0]", 
                interpolator.interpolate("Hello ^*[0]", map));
    }
    
    @Test
    public void testEscapeWithMultiplePrefixedSquareBrackets_OneEscaped() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/name/John/Doe");
//        when(req.getRoute()).thenReturn(new Route("/name/*/*"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("0", new Value().forPrefixedBracketEnclosed("John"));
        map.put("1", new Value().forPrefixedBracketEnclosed("Doe"));
        
        assertEquals("Hello *[0] Doe", 
                interpolator.interpolate("Hello ^*[0] *[1]", map));
    }
    
    @Test
    public void testEscapeWithMultiplePrefixedSquareBrackets_MultipleEscaped() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/name/John/Doe");
//        when(req.getRoute()).thenReturn(new Route("/name/*/*"));
//        when(req.getSession()).thenReturn(null);
        
        map.put("0", new Value().forPrefixedBracketEnclosed("John"));
        map.put("1", new Value().forPrefixedBracketEnclosed("Doe"));
        
        assertEquals("Hello *[0] *[1]", 
                interpolator.interpolate("Hello ^*[0] ^*[1]", map));
    }
    
    @Test
    public void testEscapeBraceEnclosedWithPrefixedName() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/John");
//        when(req.getRoute()).thenReturn(new Route("/:name"));
//        Session session = new Session();
//        session.set("name", ":name");
//        when(req.getSession()).thenReturn(session);
        
        map.put("name", new Value().forPrefix("John")
                                   .forBraceEnclosed(":name"));
        
        assertEquals("Hello {name}", 
                interpolator.interpolate("Hello ^{name}", map));
    }
    
    @Test
    public void testEscapePrefixedWithBraceEnclosedName() {
        
//        HttpRequest req = mock(HttpRequest.class);
//        when(req.getUndecodedPath()).thenReturn("/{name}");
//        when(req.getRoute()).thenReturn(new Route("/:name"));
//        Session session = new Session();
//        session.set("name", "Tim");
//        when(req.getSession()).thenReturn(session);
        
        map.put("name", new Value().forPrefix("{name}")
                                   .forBraceEnclosed("Tim"));
        
        assertEquals("Hello :name", 
                interpolator.interpolate("Hello ^:name", map));
    }
    
    /*-----------------------------------*/
    
    private static class Value {
        
        public String forPrefix;
        public String forBraceEnclosed;
        public String forBracketEnclosed;
        public String forPrefixedBracketEnclosed;
        
        public Value forPrefix(String val) {
            forPrefix = val;
            return this;
        }
        
        public Value forBraceEnclosed(String val) {
            forBraceEnclosed = val;
            return this;
        }
        
        public Value forBracketEnclosed(String val) {
            forBracketEnclosed = val;
            return this;
        }
        
        public Value forPrefixedBracketEnclosed(String val) {
            forPrefixedBracketEnclosed = val;
            return this;
        }
        
        public String getForPrefix() {
            return forPrefix;
        }

        public String getForBraceEnclosed() {
            return forBraceEnclosed;
        }

        public String getForBracketEnclosed() {
            return forBracketEnclosed;
        }

        public String getForPrefixedBracketEnclosed() {
            return forPrefixedBracketEnclosed;
        }
    }
}
