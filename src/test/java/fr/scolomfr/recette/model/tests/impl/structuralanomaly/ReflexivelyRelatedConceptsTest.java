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
package fr.scolomfr.recette.model.tests.impl.structuralanomaly;

import static fr.scolomfr.recette.model.tests.impl.ResultTestHelper.assertContainsMessage;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
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
@ActiveProfiles("web")
@ContextConfiguration(classes = { MvcConfiguration.class })
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
public class ReflexivelyRelatedConceptsTest {

	@Autowired
	private ReflexivelyRelatedConcepts reflexivelyRelatedConcepts;

	@Test
	public void testSkosWithSolelyTransitivelyRelatedConcepts() {
		reflexivelyRelatedConcepts.reset();
		Map<String, String> executionParameters = new HashMap<>();
		executionParameters.put(TestParameters.Values.SKOSTYPE, "skos");
		executionParameters.put(TestParameters.Values.VERSION, "0.0.0");
		executionParameters.put(TestParameters.Values.VOCABULARY, "q19_invalid");
		reflexivelyRelatedConcepts.setExecutionParameters(executionParameters);
		reflexivelyRelatedConcepts.run();
		Result result = reflexivelyRelatedConcepts.getResult();

		Assert.assertEquals("There should be exactly 2 errors.", 2, result.getErrorCount());
		String uri = "http://data.education.fr/voc/scolomfr/conceptA";
		assertContainsMessage(result, Message.Type.ERROR, new String[] {}, new String[] { uri });

	}

}
