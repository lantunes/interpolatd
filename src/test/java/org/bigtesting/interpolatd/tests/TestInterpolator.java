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
    
    private Interpolator<ValueMap> interpolator;
    
    private ValueMap map;
    
    @Before
    public void beforeEachTest() {
        
        interpolator = new Interpolator<ValueMap>();
        
        interpolator.when("[a-zA-Z0-9_]+").prefixedBy(":").handleWith(new Substitutor<ValueMap>() {
            public String substitute(String captured, ValueMap map) {
                return map.getForPrefixed(captured);
            }
        });
        
        interpolator.when("[0-9]+").enclosedBy("*[").and("]").handleWith(new Substitutor<ValueMap>() {
            public String substitute(String captured, ValueMap map) {
                return map.getForPrefixedBracketEnclosed(captured);
            }
        });
        
        interpolator.when().enclosedBy("{").and("}").handleWith(new Substitutor<ValueMap>() {
            public String substitute(String captured, ValueMap map) {
                return map.getForBraceEnclosed(captured);
            }
        });
        
        interpolator.when().enclosedBy("[").and("]").handleWith(new Substitutor<ValueMap>() {
            public String substitute(String captured, ValueMap map) {
                return map.getForBracketEnclosed(captured);
            }
        });
        
        interpolator.escapeWith("^");
        
        map = new ValueMap();
    }
    
    @Test
    public void testStringReturnedUnmodifiedWhenNoSubstitutionsArePresent() {
        
        map.put("foo", new Value().forPrefixed("bar"));
        
        assertEquals("Hello World!", 
                interpolator.interpolate("Hello World!", map));
    }
    
    @Test
    public void testSinglePrefixedSubstitued() {
        
        map.put("name", new Value().forPrefixed("Tim"));
        
        assertEquals("Hello Tim", 
                interpolator.interpolate("Hello :name", map));
    }
    
    @Test
    public void testSinglePrefixedSubstituedInPresenceOfOtherStandalonePrefixes() {
        
        map.put("name", new Value().forPrefixed("Tim"));
        
        assertEquals("Hello : Tim :", 
                interpolator.interpolate("Hello : :name :", map));
    }
    
    @Test
    public void testSinglePrefixedDoublyPrefixedIsSubstitued() {
        
        map.put("name", new Value().forPrefixed("Tim"));
        
        assertEquals("Hello :Tim", 
                interpolator.interpolate("Hello ::name", map));
    }
    
    @Test
    public void testSinglePrefixedEnclosedByPrefixesIsSubstitued() {
        
        map.put("name", new Value().forPrefixed("Tim"));
        
        assertEquals("Hello :Tim:", 
                interpolator.interpolate("Hello ::name:", map));
    }
    
    @Test
    public void testSinglePrefixedSubstituedInPresenceOfOtherPrefixedTerms() {
        
        map.put("name", new Value().forPrefixed("Tim"));
        
        assertEquals("Hello :someTerm Tim", 
                interpolator.interpolate("Hello :someTerm :name", map));
    }
    
    @Test
    public void testSinglePrefixedSubstituedWithPrefixContainingValue() {
        
        map.put("name", new Value().forPrefixed(":Tim"));
        
        assertEquals("Hello :Tim", 
                interpolator.interpolate("Hello :name", map));
    }
    
    @Test
    public void testSimilarPrefixedSubstitued() {
        
        map.put("name", new Value().forPrefixed("Tim"));
        map.put("name1", new Value().forPrefixed("John"));
        
        assertEquals("Hello Tim John", 
                interpolator.interpolate("Hello :name :name1", map));
    }
    
    @Test
    public void testSimilarPrefixedNotSubstitued() {
        
        map.put("name", new Value().forPrefixed("Tim"));
        
        assertEquals("Hello :name1", 
                interpolator.interpolate("Hello :name1", map));
    }
    
    @Test
    public void testPrefixedSubstitutedWithNameOfAnotherPrefix() {
        
        map.put("name", new Value().forPrefixed(":id"));
        map.put("id", new Value().forPrefixed("1"));
        
        assertEquals("Hello :id 1", 
                interpolator.interpolate("Hello :name :id", map));
    }
    
    @Test
    public void testMultiplePrefixedSubstitued() {
        
        map.put("firstName", new Value().forPrefixed("John"));
        map.put("lastName", new Value().forPrefixed("Doe"));
        
        assertEquals("Hello John Doe", 
                interpolator.interpolate("Hello :firstName :lastName", map));
    }
    
    @Test
    public void testMultiplePrefixedSubstituedInPresenceOfOtherPrefixed() {
        
        map.put("firstName", new Value().forPrefixed("John"));
        map.put("lastName", new Value().forPrefixed("Doe"));
        
        assertEquals("Hello : John : Doe :", 
                interpolator.interpolate("Hello : :firstName : :lastName :", map));
    }
    
    @Test
    public void testMultiplePrefixedSubstituedWithPrefixContainingValues() {
        
        map.put("firstName", new Value().forPrefixed(":John"));
        map.put("lastName", new Value().forPrefixed(":Doe"));
        
        assertEquals("Hello :John :Doe", 
                interpolator.interpolate("Hello :firstName :lastName", map));
    }
    
    @Test
    public void testSingleBraceEnclosedSubstitued() {
        
        map.put("name", new Value().forBraceEnclosed("Tim"));
        
        assertEquals("Hello Tim", 
                interpolator.interpolate("Hello {name}", map));
    }
    
    @Test
    public void testBraceEnclosedWithPrefixedNameNotSubstituted() {
        
        map.put("name", new Value().forPrefixed("John")
                                   .forBraceEnclosed(":name"));
        
        assertEquals("Hello :name", 
                interpolator.interpolate("Hello {name}", map));
    }
    
    @Test
    public void testPrefixedWithBraceEnclosedNameNotSubstituted() {
        
        map.put("name", new Value().forPrefixed("{name}")
                                   .forBraceEnclosed("Tim"));
        
        assertEquals("Hello {name}", 
                interpolator.interpolate("Hello :name", map));
    }
    
    @Test
    public void testSingleBraceEnclosedSubstituedInPresenceOfOtherBraces() {
        
        map.put("name", new Value().forBraceEnclosed("Tim"));
        
        assertEquals("Hello { Tim } {", 
                interpolator.interpolate("Hello { {name} } {", map));
    }
    
    @Test
    public void testSingleBraceEnclosedSubstituedInPresenceOfOtherBraces_NoSpaces() {
        
        map.put("name", new Value().forBraceEnclosed("Tim"));
        
        assertEquals("Hello {Tim} {", 
                interpolator.interpolate("Hello {{name}} {", map));
    }
    
    @Test
    public void testSingleBraceEnclosedSubstituedInPresenceOfOtherBraceEnclosedTerms() {
        
        map.put("name", new Value().forBraceEnclosed("Tim"));
        
        assertEquals("Hello {there} Tim", 
                interpolator.interpolate("Hello {there} {name}", map));
    }
    
    @Test
    public void testSingleBraceEnclosedSubstituedWithBraceEnclosedValue() {
        
        map.put("name", new Value().forBraceEnclosed("{Tim}"));
        
        assertEquals("Hello {Tim}", 
                interpolator.interpolate("Hello {name}", map));
    }
    
    @Test
    public void testMultipleBraceEnclosedSubstitued() {
        
        map.put("firstName", new Value().forBraceEnclosed("John"));
        map.put("lastName", new Value().forBraceEnclosed("Doe"));
        
        assertEquals("Hello John Doe", 
                interpolator.interpolate("Hello {firstName} {lastName}", map));
    }
    
    @Test
    public void testMultipleBraceEnclosedSubstituedInPresenceOfOtherBraces() {
        
        map.put("firstName", new Value().forBraceEnclosed("John"));
        map.put("lastName", new Value().forBraceEnclosed("Doe"));
        
        assertEquals("Hello { John } { Doe } {", 
                interpolator.interpolate("Hello { {firstName} } { {lastName} } {", map));
    }
    
    @Test
    public void testMultipleBraceEnclosedSubstituedInPresenceOfOtherBraces_NoSpaces() {
        
        map.put("firstName", new Value().forBraceEnclosed("John"));
        map.put("lastName", new Value().forBraceEnclosed("Doe"));
        
        assertEquals("Hello {John} {Doe} {", 
                interpolator.interpolate("Hello {{firstName}} {{lastName}} {", map));
    }
    
    @Test
    public void testMultipleBraceEnclosedSubstituedInPresenceOfOtherBraceEnclosedTerms() {
        
        map.put("firstName", new Value().forBraceEnclosed("John"));
        map.put("lastName", new Value().forBraceEnclosed("Doe"));
        
        assertEquals("Hello {there} John Doe", 
                interpolator.interpolate("Hello {there} {firstName} {lastName}", map));
    }
    
    @Test
    public void testMultipleBraceEnclosedSubstituedWithBraceEnclosedValue() {
        
        map.put("firstName", new Value().forBraceEnclosed("{John}"));
        map.put("lastName", new Value().forBraceEnclosed("{Doe}"));
        
        assertEquals("Hello {John} {Doe}", 
                interpolator.interpolate("Hello {firstName} {lastName}", map));
    }
    
    @Test
    public void testPrefixedAndBraceEnclosed_PrefixedValuesComeFirst() {
        
        map.put("salutation", new Value().forPrefixed("Mr."));
        map.put("firstName", new Value().forBraceEnclosed("John"));
        map.put("lastName", new Value().forBraceEnclosed("Doe"));
        
        assertEquals("Hello Mr. John Doe", 
                interpolator.interpolate("Hello :salutation {firstName} {lastName}", map));
    }
    
    @Test
    public void testPrefixedAndBraceEnclosed_BraceEnclosedComeFirst() {
        
        map.put("salutation", new Value().forBraceEnclosed("Mr."));
        map.put("firstName", new Value().forPrefixed("John"));
        map.put("lastName", new Value().forPrefixed("Doe"));
        
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
        map.put("{name}", new Value().forPrefixed("John"));
        map.put("name", new Value().forBraceEnclosed("Tim"));
        
        assertEquals("Hello :Tim", 
                interpolator.interpolate("Hello :{name}", map));
    }
    
    @Test
    public void testBraceEnclosedPrefixedValueSubstitutesBraceEnclosed_OneOccurrence() {
        
        map.put("name", new Value().forPrefixed("John"));
        map.put(":name", new Value().forBraceEnclosed("Tim"));
        
        assertEquals("Hello Tim", 
                interpolator.interpolate("Hello {:name}", map));
    }
    
    @Test
    public void testBraceEnclosedPrefixedValueSubstitutesBraceEnclosed_TwoOccurrences() {
        
        map.put("first", new Value().forPrefixed("John"));
        map.put("last", new Value().forPrefixed("Doe"));
        map.put(":first", new Value().forBraceEnclosed("Tom"));
        map.put(":last", new Value().forBraceEnclosed("Jerry"));
        
        assertEquals("Hello Tom Jerry", 
                interpolator.interpolate("Hello {:first} {:last}", map));
    }
    
    @Test
    public void testSquareBracketUnknownValuesIgnored() throws Exception {
        
        map.put("request.body", new Value().forBracketEnclosed("Hello World!"));
        
        assertEquals("Message: [some.value]", 
                interpolator.interpolate("Message: [some.value]", map));
    }
    
    @Test
    public void testSquareBracketSubstituted() throws Exception {
        
        map.put("request.body", new Value().forBracketEnclosed("Hello World!"));
        
        assertEquals("Message: Hello World!", 
                interpolator.interpolate("Message: [request.body]", map));
    }
    
    @Test
    public void testSquareBracketSubstituedInPresenceOfOtherSquareBrackets() throws Exception {
        
        map.put("request.body", new Value().forBracketEnclosed("Hello World!"));
        
        assertEquals("Message: [ Hello World! ] [", 
                interpolator.interpolate("Message: [ [request.body] ] [", map));
    }
    
    @Test
    public void testSquareBracketSubstituedInPresenceOfOtherSquareBrackets_NoSpaces() throws Exception {
        
        map.put("request.body", new Value().forBracketEnclosed("Hello World!"));
        
        assertEquals("Message: [Hello World!] [", 
                interpolator.interpolate("Message: [[request.body]] [", map));
    }
    
    @Test
    public void testSquareBracketSubstituedInPresenceOfOtherSquareBracketEnclosedTerms() throws Exception {
        
        map.put("request.body", new Value().forBracketEnclosed("Tim"));
        
        assertEquals("Hello [there] Tim", 
                interpolator.interpolate("Hello [there] [request.body]", map));
    }
    
    @Test
    public void testSquareBracketSubstituedWithSquareBracketEnclosedValue() throws Exception {
        
        map.put("request.body", new Value().forBracketEnclosed("[Tim]"));
        
        assertEquals("Hello [Tim]", 
                interpolator.interpolate("Hello [request.body]", map));
    }
    
    @Test
    public void testPrefixedAndBraceEnclosedAndSquareBracketUsedTogether() throws Exception {
        
        map.put("salutation", new Value().forBraceEnclosed("Mr."));
        map.put("name", new Value().forPrefixed("John"));
        map.put("request.body", new Value().forBracketEnclosed("Doe"));
        
        assertEquals("Hello Mr. John Doe", 
                interpolator.interpolate("Hello {salutation} :name [request.body]", map));
    }
    
    @Test
    public void testSinglePrefixedSquareBracketSubstitued() {
        
        map.put("0", new Value().forPrefixedBracketEnclosed("Tim"));
        
        assertEquals("Hello Tim", 
                interpolator.interpolate("Hello *[0]", map));
    }
    
    @Test
    public void testSinglePrefixedSquareBracketSubstituedMultipleTimes() {
        
        map.put("0", new Value().forPrefixedBracketEnclosed("Tim"));
        
        assertEquals("Hello Tim Tim", 
                interpolator.interpolate("Hello *[0] *[0]", map));
    }
    
    @Test
    public void testSinglePrefixedSquareBracketNotSubstituedWithIncorrectIndex() {
        
        map.put("0", new Value().forPrefixedBracketEnclosed("Tim"));
        
        assertEquals("Hello *[1]", 
                interpolator.interpolate("Hello *[1]", map));
    }
    
    @Test
    public void testSinglePrefixedSquareBracketNotSubstituedIfFormattedIncorrectly() {
        
        map.put("0", new Value().forPrefixedBracketEnclosed("Tim"));
        
        assertEquals("Hello *[ 0]", 
                interpolator.interpolate("Hello *[ 0]", map));
    }
    
    @Test
    public void testSinglePrefixedSquareBracketSubstituedWithSplatContainingValue() {
        
        map.put("0", new Value().forPrefixedBracketEnclosed("*[0]"));
        
        assertEquals("Hello *[0]", 
                interpolator.interpolate("Hello *[0]", map));
    }
    
    @Test
    public void testSinglePrefixedSquareBracketSubstituedWithEmptyValue() {
        
        map.put("0", new Value().forPrefixedBracketEnclosed(""));
        
        assertEquals("Hello ", 
                interpolator.interpolate("Hello *[0]", map));
    }
    
    @Test
    public void testPrefixedSquareBracketSubstituedWithDoubleDigitIndex() {
        
        map.put("10", new Value().forPrefixedBracketEnclosed("k"));
        
        assertEquals("Hello k", 
                interpolator.interpolate("Hello *[10]", map));
    }
    
    @Test
    public void testTwoPrefixedSquareBracketsSubstitued() {
        
        map.put("0", new Value().forPrefixedBracketEnclosed("John"));
        map.put("1", new Value().forPrefixedBracketEnclosed("Doe"));
        
        assertEquals("Hello John Doe", 
                interpolator.interpolate("Hello *[0] *[1]", map));
    }
    
    @Test
    public void testOnlySecondOfTwoPrefixedSquareBracketsSubstitued() {
        
        map.put("0", new Value().forPrefixedBracketEnclosed("John"));
        map.put("1", new Value().forPrefixedBracketEnclosed("Doe"));
        
        assertEquals("Hello Doe", 
                interpolator.interpolate("Hello *[1]", map));
    }
    
    @Test
    public void testSubstitutionIncreasesOverallLengthOfString() {
        
        map.put("0", new Value().forPrefixedBracketEnclosed("hello"));
        map.put("1", new Value().forPrefixedBracketEnclosed("there"));
        
        assertEquals("hello there", 
                interpolator.interpolate("*[0] *[1]", map));
    }
    
    @Test
    public void testSubstitutionDecreasesOverallLengthOfString() {
        
        map.put("0", new Value().forPrefixedBracketEnclosed("a"));
        map.put("1", new Value().forPrefixedBracketEnclosed("b"));
        
        assertEquals("a b", 
                interpolator.interpolate("*[0] *[1]", map));
    }
    
    @Test
    public void testEscapeWithSinglePrefixedWhichExists() {
        
        map.put("name", new Value().forPrefixed("John"));
        
        assertEquals("Hello :name", 
                interpolator.interpolate("Hello ^:name", map));
    }
    
    @Test
    public void testEscapeWithSinglePrefixedWhichDoesNotExist() {
        
        assertEquals("Hello :name", 
                interpolator.interpolate("Hello ^:name", map));
    }
    
    @Test
    public void testEscapeWithMultiplePrefixed_OneEscaped() {
        
        map.put("name1", new Value().forPrefixed("John"));
        map.put("name2", new Value().forPrefixed("Doe"));
        
        assertEquals("Hello :name1 Doe", 
                interpolator.interpolate("Hello ^:name1 :name2", map));
    }
    
    @Test
    public void testEscapeWithMultiplePrefixed_MultipleEscaped() {
        
        map.put("name1", new Value().forPrefixed("John"));
        map.put("name2", new Value().forPrefixed("Doe"));
        
        assertEquals("Hello :name1 :name2", 
                interpolator.interpolate("Hello ^:name1 ^:name2", map));
    }
    
    @Test
    public void testEscapeWithMultiplePrefixed_MultipleEscapedConcatenated() {
        
        map.put("name1", new Value().forPrefixed("John"));
        map.put("name2", new Value().forPrefixed("Doe"));
        
        assertEquals("Hello :name1:name2", 
                interpolator.interpolate("Hello ^:name1^:name2", map));
    }
    
    @Test
    public void testDoubleEscapeWithPrefixed() {
        
        map.put("name", new Value().forPrefixed("John"));
        
        assertEquals("Hello ^John", 
                interpolator.interpolate("Hello ^^:name", map));
    }
    
    @Test
    public void testTripleEscapeWithPrefixed() {
        
        map.put("name", new Value().forPrefixed("John"));
        
        assertEquals("Hello ^:name", 
                interpolator.interpolate("Hello ^^^:name", map));
    }
    
    @Test
    public void testQuadrupleEscapeWithPrefixed() {
        
        map.put("name", new Value().forPrefixed("John"));
        
        assertEquals("Hello ^^John", 
                interpolator.interpolate("Hello ^^^^:name", map));
    }
    
    @Test
    public void testEscapeOnItsOwn() {
        
        assertEquals("Hello ^ there", 
                interpolator.interpolate("Hello ^ there", map));
    }
    
    @Test
    public void testDoubleEscapeOnItsOwn() {
        
        assertEquals("Hello ^^ there", 
                interpolator.interpolate("Hello ^^ there", map));
    }
    
    @Test
    public void testTripleEscapeOnItsOwn() {
        
        assertEquals("Hello ^^^ there", 
                interpolator.interpolate("Hello ^^^ there", map));
    }
    
    @Test
    public void testQuadrupleEscapeOnItsOwn() {
        
        assertEquals("Hello ^^^^ there", 
                interpolator.interpolate("Hello ^^^^ there", map));
    }
    
    @Test
    public void testEscapeOnItsOwnWithPrefixed() {
        
        map.put("name", new Value().forPrefixed("John"));
        
        assertEquals("Hello ^ John", 
                interpolator.interpolate("Hello ^ :name", map));
    }
    
    @Test
    public void testDoubleEscapeOnItsOwnWithPrefixed() {
        
        map.put("name", new Value().forPrefixed("John"));
        
        assertEquals("Hello ^^ John", 
                interpolator.interpolate("Hello ^^ :name", map));
    }
    
    @Test
    public void testTripleEscapeOnItsOwnWithPrefixed() {
        
        map.put("name", new Value().forPrefixed("John"));
        
        assertEquals("Hello ^^^ John", 
                interpolator.interpolate("Hello ^^^ :name", map));
    }
    
    @Test
    public void testQuadrupleEscapeOnItsOwnWithPrefixed() {
        
        map.put("name", new Value().forPrefixed("John"));
        
        assertEquals("Hello ^^^^ John", 
                interpolator.interpolate("Hello ^^^^ :name", map));
    }
    
    @Test
    public void testEscapeNextToNonInterpolatedValue() {
        
        assertEquals("Hello ^there", 
                interpolator.interpolate("Hello ^there", map));
    }
    
    @Test
    public void testDoubleEscapeNextToNonInterpolatedValue() {
        
        assertEquals("Hello ^^there", 
                interpolator.interpolate("Hello ^^there", map));
    }
    
    @Test
    public void testTripleEscapeNextToNonInterpolatedValue() {
        
        assertEquals("Hello ^^^there", 
                interpolator.interpolate("Hello ^^^there", map));
    }
    
    @Test
    public void testQuadrupleEscapeNextToNonInterpolatedValue() {
        
        assertEquals("Hello ^^^^there", 
                interpolator.interpolate("Hello ^^^^there", map));
    }
    
    @Test
    public void testEscapeAfterPrefixed() {
        
        map.put("name", new Value().forPrefixed("John"));
        
        assertEquals("Hello John^", 
                interpolator.interpolate("Hello :name^", map));
    }
    
    @Test
    public void testDoubleEscapeAfterPrefixed() {
        
        map.put("name", new Value().forPrefixed("John"));
        
        assertEquals("Hello John^^", 
                interpolator.interpolate("Hello :name^^", map));
    }
    
    @Test
    public void testTripleEscapeAfterPrefixed() {
        
        map.put("name", new Value().forPrefixed("John"));
        
        assertEquals("Hello John^^^", 
                interpolator.interpolate("Hello :name^^^", map));
    }
    
    @Test
    public void testQuadrupleEscapeAfterPrefixed() {
        
        map.put("name", new Value().forPrefixed("John"));
        
        assertEquals("Hello John^^^^", 
                interpolator.interpolate("Hello :name^^^^", map));
    }
    
    @Test
    public void testEscapeAfterNonInterpolatedValue() {
        
        assertEquals("Hello there^", 
                interpolator.interpolate("Hello there^", map));
    }
    
    @Test
    public void testDoubleEscapeAfterNonInterpolatedValue() {
        
        assertEquals("Hello there^^", 
                interpolator.interpolate("Hello there^^", map));
    }
    
    @Test
    public void testTripleEscapeAfterNonInterpolatedValue() {
        
        assertEquals("Hello there^^^", 
                interpolator.interpolate("Hello there^^^", map));
    }
    
    @Test
    public void testQuadrupleEscapeAfterNonInterpolatedValue() {
        
        assertEquals("Hello there^^^^", 
                interpolator.interpolate("Hello there^^^^", map));
    }
    
    @Test
    public void testEscapeIgnoredInsideBraceEnclosed() {
        
        map.put("^name", new Value().forBraceEnclosed("Tim"));
        
        assertEquals("Hello Tim", 
                interpolator.interpolate("Hello {^name}", map));
    }
    
    @Test
    public void testEscapeWithSingleBraceEnclosedWhichExists() {
        
        map.put("name", new Value().forBraceEnclosed("Tim"));
        
        assertEquals("Hello {name}", 
                interpolator.interpolate("Hello ^{name}", map));
    }
    
    @Test
    public void testEscapeWithSingleBraceEnclosedWhichDoesNotExist() {
        
        assertEquals("Hello {name}", 
                interpolator.interpolate("Hello ^{name}", map));
    }
    
    @Test
    public void testEscapeWithMultipleBraceEnclosed_OneEscaped() {
        
        map.put("firstName", new Value().forBraceEnclosed("John"));
        map.put("lastName", new Value().forBraceEnclosed("Doe"));
        
        assertEquals("Hello {firstName} Doe", 
                interpolator.interpolate("Hello ^{firstName} {lastName}", map));
    }
    
    @Test
    public void testEscapeWithMultipleBraceEnclosed_MultipleEscaped() {
        
        map.put("firstName", new Value().forBraceEnclosed("John"));
        map.put("lastName", new Value().forBraceEnclosed("Doe"));
        
        assertEquals("Hello {firstName} {lastName}", 
                interpolator.interpolate("Hello ^{firstName} ^{lastName}", map));
    }
    
    @Test
    public void testEscapeWithSingleSquareBracketWhichExists() throws Exception {
        
        map.put("request?name", new Value().forBracketEnclosed("Tim"));
        
        assertEquals("Hello [request?name]", 
                interpolator.interpolate("Hello ^[request?name]", map));
    }
    
    @Test
    public void testEscapeWithSingleSquareBracketWhichDoesNotExist() throws Exception {
        
        assertEquals("Hello [request?name]", 
                interpolator.interpolate("Hello ^[request?name]", map));
    }
    
    @Test
    public void testEscapeWithMultipleSquareBrackets_OneEscaped() throws Exception {
        
        map.put("request?name1", new Value().forBracketEnclosed("John"));
        map.put("request?name2", new Value().forBracketEnclosed("Doe"));
        
        assertEquals("Hello [request?name1] Doe", 
                interpolator.interpolate("Hello ^[request?name1] [request?name2]", map));
    }
    
    @Test
    public void testEscapeWithMultipleSquareBrackets_MultipleEscaped() throws Exception {
        
        map.put("request?name1", new Value().forBracketEnclosed("John"));
        map.put("request?name2", new Value().forBracketEnclosed("Doe"));
        
        assertEquals("Hello [request?name1] [request?name2]", 
                interpolator.interpolate("Hello ^[request?name1] ^[request?name2]", map));
    }
    
    @Test
    public void testEscapeWithSinglePrefixedSquareBracketWhichExists() {
        
        map.put("0", new Value().forPrefixedBracketEnclosed("Tim"));
        
        assertEquals("Hello *[0]", 
                interpolator.interpolate("Hello ^*[0]", map));
    }
    
    @Test
    public void testEscapeWithSinglePrefixedSquareBracketWhichDoesNotExist() {
        
        assertEquals("Hello *[0]", 
                interpolator.interpolate("Hello ^*[0]", map));
    }
    
    @Test
    public void testEscapeWithMultiplePrefixedSquareBrackets_OneEscaped() {
        
        map.put("0", new Value().forPrefixedBracketEnclosed("John"));
        map.put("1", new Value().forPrefixedBracketEnclosed("Doe"));
        
        assertEquals("Hello *[0] Doe", 
                interpolator.interpolate("Hello ^*[0] *[1]", map));
    }
    
    @Test
    public void testEscapeWithMultiplePrefixedSquareBrackets_MultipleEscaped() {
        
        map.put("0", new Value().forPrefixedBracketEnclosed("John"));
        map.put("1", new Value().forPrefixedBracketEnclosed("Doe"));
        
        assertEquals("Hello *[0] *[1]", 
                interpolator.interpolate("Hello ^*[0] ^*[1]", map));
    }
    
    @Test
    public void testEscapeBraceEnclosedWithPrefixedName() {
        
        map.put("name", new Value().forPrefixed("John")
                                   .forBraceEnclosed(":name"));
        
        assertEquals("Hello {name}", 
                interpolator.interpolate("Hello ^{name}", map));
    }
    
    @Test
    public void testEscapePrefixedWithBraceEnclosedName() {
        
        map.put("name", new Value().forPrefixed("{name}")
                                   .forBraceEnclosed("Tim"));
        
        assertEquals("Hello :name", 
                interpolator.interpolate("Hello ^:name", map));
    }
    
    @Test
    public void testPrefixedBracketEnclosedSubstitutedForAny() {
        
        /*
         * Even though we have a match for "[]",
         * and a value for any kind of match, the 
         * result should not be "Hello *John"
         */
        map.put("0", new Value().forAny("John"));
        
        assertEquals("Hello John", 
                interpolator.interpolate("Hello *[0]", map));
    }
    
    @Test
    public void testEscapeWithSinglePrefixedSquareBracketWhichExistsForAny() {
        
        /*
         * Even though we have a match for "[]",
         * and a value for any kind of match, the 
         * result should not be "Hello *Tim"
         */
        map.put("0", new Value().forAny("Tim"));
        
        assertEquals("Hello *[0]", 
                interpolator.interpolate("Hello ^*[0]", map));
    }
    
    /*-----------------------------------*/
    
    private static class ValueMap {
        
        private Map<String, Value> map = new HashMap<String, Value>();
        
        public Value put(String key, Value value) {
            return map.put(key, value);
        }
        
        public String getForPrefixed(String captured) {
            
            Value val = map.get(captured);
            if (hasForAny(val)) return val.getForAny();
            return val != null ? val.getForPrefixed() : null;
        }
        
        public String getForBraceEnclosed(String captured) {
            
            Value val = map.get(captured);
            if (hasForAny(val)) return val.getForAny();
            return val != null ? val.getForBraceEnclosed() : null;
        }
        
        public String getForBracketEnclosed(String captured) {
            
            Value val = map.get(captured);
            if (hasForAny(val)) return val.getForAny();
            return val != null ? val.getForBracketEnclosed() : null;
        }
        
        public String getForPrefixedBracketEnclosed(String captured) {
            
            Value val = map.get(captured);
            if (hasForAny(val)) return val.getForAny();
            return val != null ? val.getForPrefixedBracketEnclosed() : null;
        }
        
        private boolean hasForAny(Value val) {
            return val != null && val.getForAny() != null;
        }
    }
    
    private static class Value {
        
        private String forAny;
        private String forPrefixed;
        private String forBraceEnclosed;
        private String forBracketEnclosed;
        private String forPrefixedBracketEnclosed;
        
        public Value forAny(String val) {
            forAny = val;
            return this;
        }
        
        public Value forPrefixed(String val) {
            forPrefixed = val;
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
        
        public String getForAny() {
            return forAny;
        }
        
        public String getForPrefixed() {
            return forPrefixed;
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
