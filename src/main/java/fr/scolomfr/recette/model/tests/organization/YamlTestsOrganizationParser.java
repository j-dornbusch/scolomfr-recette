/**
 * 
 * Scolomfr Recette
 * 
 * Copyright (C) 2017  Direction du Numérique pour l'éducation - Ministère de l'éducation nationale, de l'enseignement supérieur et de la Recherche
 * Copyright (C) 2017 Joachim Dornbusch
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
package fr.scolomfr.recette.model.tests.organization;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * Parses tests organisation file in Yaml format
 */
@Component
public class YamlTestsOrganizationParser implements TestsOrganizationParser {

	private TestCasesOrganization testsOrganization;

	@Override
	public TestCasesOrganization getTestOrganization() {
		return testsOrganization;
	}

	@Override
	public TestsOrganizationParser load(InputStream testsOrganizationFile) throws IOException {
		Constructor constructor = new Constructor(TestCasesOrganization.class);
		TypeDescription testsOrganizationDescription = new TypeDescription(TestCasesOrganization.class);
		testsOrganizationDescription.putListPropertyType("structure", HashMap.class);
		constructor.addTypeDescription(testsOrganizationDescription);
		Yaml yaml = new Yaml(constructor);
		testsOrganization = (TestCasesOrganization) yaml.load(testsOrganizationFile);
		return this;
	}

}
