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
package fr.scolomfr.recette.model.tests.impl;

import java.util.Collection;
import java.util.Iterator;

import org.openrdf.model.Resource;

import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.impl.AbstractQskosTestCase;
import fr.scolomfr.recette.model.tests.organization.TestParameters;

@TestParameters(names = { TestParameters.Values.VERSION, TestParameters.Values.VOCABULARY,
		TestParameters.Values.SKOSTYPE })
public abstract class AbstractCollectionResourceQskosTestCase extends AbstractQskosTestCase<Collection<Resource>> {

	@Override
	protected void populateResult(Collection<Resource> data) {
		if (data == null) {
			return;
		}
		Iterator<Resource> it = data.iterator();

		while (it.hasNext()) {
			Resource resource = it.next();
			String errorCode = generateUniqueErrorCode(resource.stringValue());
			boolean ignored = errorIsIgnored(errorCode);
			incrementErrorCount(ignored);
			addMessage(new Message(ignored ? Message.Type.IGNORED : Message.Type.ERROR, errorCode,
					i18n.tr("tests.impl.qskos." + getQskosIssueCode() + ".result.title"),
					i18n.tr("tests.impl.qskos." + getQskosIssueCode() + ".result.content",
							new Object[] { resource.stringValue() })));
		}

	}

}
