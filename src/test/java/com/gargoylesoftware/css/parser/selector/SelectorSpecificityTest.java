/*
 * Copyright (c) 2019 Ronald Brill.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gargoylesoftware.css.parser.selector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;

import com.gargoylesoftware.css.parser.CSSOMParser;
import com.gargoylesoftware.css.parser.InputSource;

/**
 * Tests for {@link SelectorSpecificity}.
 *
 * @author Marc Guillemot
 * @author Ronald Brill
 */
public class SelectorSpecificityTest {

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void selectorSpecifity() throws Exception {
        final SelectorSpecificity specificy0 = selectorSpecifity("*", "0,0,0,0");
        final SelectorSpecificity specificy1 = selectorSpecifity("li", "0,0,0,1");
        final SelectorSpecificity specificy2a = selectorSpecifity("li:first-line", "0,0,0,2");
        final SelectorSpecificity specificy2b = selectorSpecifity("ul li", "0,0,0,2");
        final SelectorSpecificity specificy2c = selectorSpecifity("body > p", "0,0,0,2");
        final SelectorSpecificity specificy3 = selectorSpecifity("ul ol+li", "0,0,0,3");
        final SelectorSpecificity specificy11 = selectorSpecifity("h1 + *[rel=up]", "0,0,1,1");
        final SelectorSpecificity specificy13 = selectorSpecifity("ul ol li.red", "0,0,1,3");
        final SelectorSpecificity specificy21 = selectorSpecifity("li.red.level", "0,0,2,1");
        final SelectorSpecificity specificy100 = selectorSpecifity("#x34y", "0,1,0,0");

        selectorSpecifity("test#x34y", "0,1,0,1");

        assertEquals(0, specificy0.compareTo(specificy0));
        assertTrue(specificy0.compareTo(specificy1) < 0);
        assertTrue(specificy0.compareTo(specificy2a) < 0);
        assertTrue(specificy0.compareTo(specificy13) < 0);

        assertEquals(0, specificy1.compareTo(specificy1));
        assertTrue(specificy1.compareTo(specificy0) > 0);
        assertTrue(specificy1.compareTo(specificy2a) < 0);
        assertTrue(specificy1.compareTo(specificy13) < 0);

        assertEquals(0, specificy2a.compareTo(specificy2b));
        assertEquals(0, specificy2a.compareTo(specificy2c));
        assertTrue(specificy2a.compareTo(specificy0) > 0);
        assertTrue(specificy2a.compareTo(specificy3) < 0);
        assertTrue(specificy2a.compareTo(specificy11) < 0);
        assertTrue(specificy2a.compareTo(specificy13) < 0);
        assertTrue(specificy2a.compareTo(specificy100) < 0);

        assertEquals(0, specificy11.compareTo(specificy11));
        assertTrue(specificy11.compareTo(specificy0) > 0);
        assertTrue(specificy11.compareTo(specificy13) < 0);
        assertTrue(specificy11.compareTo(specificy21) < 0);
        assertTrue(specificy11.compareTo(specificy100) < 0);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void selectorSpecifitySiblingCombinator() throws Exception {
        selectorSpecifity(".cls ~ p", "0,0,1,1");
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void selectorSpecifityLangCondition() throws Exception {
        selectorSpecifity(":lang(en)", "0,0,1,0");
        selectorSpecifity("li:lang(en)", "0,0,1,1");
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void selectorSpecifityPseudo() throws Exception {
        selectorSpecifity("#id div:hover > div", "0,1,1,2");
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void selectorSpecifityCondition() throws Exception {
        selectorSpecifity("a[target=_blank]", "0,0,1,1");
        selectorSpecifity("a[id=xy]", "0,0,1,1");

        selectorSpecifity("a[target*='ank']", "0,0,1,1");
        selectorSpecifity("a[id*='xy']", "0,0,1,1");

        selectorSpecifity("a[target$='ank']", "0,0,1,1");
        selectorSpecifity("a[id$='xy']", "0,0,1,1");

        selectorSpecifity("a[target^='ank']", "0,0,1,1");
        selectorSpecifity("a[id^='xy']", "0,0,1,1");

        selectorSpecifity("a[target~='ank']", "0,0,1,1");
        selectorSpecifity("a[id~='xy']", "0,0,1,1");

        selectorSpecifity("a[target|='ank']", "0,0,1,1");
        selectorSpecifity("a[id|='xy']", "0,0,1,1");
    }

    private SelectorSpecificity selectorSpecifity(final String selector, final String expectedSpecificity)
        throws Exception {
        final Reader r = new StringReader(selector);
        final InputSource is = new InputSource(r);
        final SelectorList sl = new CSSOMParser().parseSelectors(is);

        final SelectorSpecificity specificity = new SelectorSpecificity(sl.get(0));
        assertEquals(expectedSpecificity, specificity.toString());
        return specificity;
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void hashCodeTest() throws Exception {
        final SelectorSpecificity specificy = selectorSpecifity("*", "0,0,0,0");
        assertTrue(specificy.hashCode() != 0);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void equalsTest() throws Exception {
        final SelectorSpecificity specificy0 = selectorSpecifity("*", "0,0,0,0");
        assertFalse(specificy0.equals(null));
        assertFalse(specificy0.equals(""));
        assertTrue(specificy0.equals(specificy0));

        final SelectorSpecificity spec0 = selectorSpecifity("*", "0,0,0,0");
        assertTrue(specificy0.equals(spec0));

        final SelectorSpecificity specificy100 = selectorSpecifity("#x34y", "0,1,0,0");
        assertFalse(specificy0.equals(specificy100));

        final SelectorSpecificity specificy11 = selectorSpecifity("h1 + *[rel=up]", "0,0,1,1");
        assertFalse(specificy0.equals(specificy11));

        final SelectorSpecificity specificy1 = selectorSpecifity("li", "0,0,0,1");
        assertFalse(specificy0.equals(specificy1));
    }
}
