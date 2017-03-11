/**
 * 
 * Scolomfr Recette
 * 
 * Copyright (C) 2017  MENESR (DNE), J.Dornbusch
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package fr.scolomfr.recette.model.tests.impl.spellchecking;

import static fr.scolomfr.recette.model.tests.impl.ResultTestHelper.assertContainsMessage;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import fr.scolomfr.recette.config.MvcConfiguration;
import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.execution.result.Result;
import fr.scolomfr.recette.model.tests.organization.TestParameters;
import junit.framework.Assert;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MvcConfiguration.class })
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
public class SkosSpellCheckingTest {

	@Autowired
	private SkosSpellChecking skosSpellChecking;

	@Test
	public void testSkosWithoutError() {
		skosSpellChecking.reset();
		Map<String, String> executionParameters = new HashMap<>();
		executionParameters.put(TestParameters.Values.SKOSTYPE, "skos");
		executionParameters.put(TestParameters.Values.VERSION, "0.0.0");
		executionParameters.put(TestParameters.Values.VOCABULARY, "a15_valid");
		skosSpellChecking.setExecutionParameters(executionParameters);
		skosSpellChecking.run();
		Result result = skosSpellChecking.getExecutionResult();
		Assert.assertEquals("There should be exactly zero error.", 0, result.getErrorCount());

	}

	@Test
	public void testSkosWithError() {
		skosSpellChecking.reset();
		Map<String, String> executionParameters = new HashMap<>();
		executionParameters.put(TestParameters.Values.SKOSTYPE, "skos");
		executionParameters.put(TestParameters.Values.VERSION, "0.0.0");
		executionParameters.put(TestParameters.Values.VOCABULARY, "a15_invalid");
		skosSpellChecking.setExecutionParameters(executionParameters);
		skosSpellChecking.run();
		Result result = skosSpellChecking.getExecutionResult();
		Assert.assertEquals("There should be exactly one error.", 1, result.getErrorCount());
		String uri = "http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-010-num-0082";
		String word = "exammen";
		String place = "scopeNote";
		assertContainsMessage(result, Message.Type.ERROR, new String[] {}, new String[] { uri, word, place });
	}

	@Test
	public void testSkosWithAbbreviation() {
		skosSpellChecking.reset();
		Map<String, String> executionParameters = new HashMap<>();
		executionParameters.put(TestParameters.Values.SKOSTYPE, "skos");
		executionParameters.put(TestParameters.Values.VERSION, "0.0.0");
		executionParameters.put(TestParameters.Values.VOCABULARY, "a15_abbr");
		skosSpellChecking.setExecutionParameters(executionParameters);
		skosSpellChecking.run();
		Result result = skosSpellChecking.getExecutionResult();
		Assert.assertEquals("There should be exactly zero error.", 0, result.getErrorCount());
		String uri = "http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-010-num-0082";
		String abbr = "\"RES\"";
		String place = "scopeNote";
		assertContainsMessage(result, Message.Type.INFO, new String[] {}, new String[] { uri, abbr, place });
	}

	@Test
	public void testSkosWithErrorAndAbbreviation() {
		skosSpellChecking.reset();
		Map<String, String> executionParameters = new HashMap<>();
		executionParameters.put(TestParameters.Values.SKOSTYPE, "skos");
		executionParameters.put(TestParameters.Values.VERSION, "0.0.0");
		executionParameters.put(TestParameters.Values.VOCABULARY, "a15_invalid_abbr");
		skosSpellChecking.setExecutionParameters(executionParameters);
		skosSpellChecking.run();
		Result result = skosSpellChecking.getExecutionResult();
		String uri = "http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-010-num-0082";
		String abbr = "\"RES\"";
		String word = "exammen";
		String place = "scopeNote";
		assertContainsMessage(result, Message.Type.ERROR, new String[] {}, new String[] { uri, word, place, abbr });
	}

}
