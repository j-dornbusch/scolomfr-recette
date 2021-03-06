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
package fr.scolomfr.recette.model.tests.impl.structuralanomaly;

import fr.scolomfr.recette.model.tests.impl.AbstractCollectionTupleQskosTestCase;
import fr.scolomfr.recette.model.tests.organization.TestCaseIndex;

/**
 * @see at.ac.univie.mminf.qskos4j.issues.relations.SolelyTransitivelyRelatedConcepts
 */
@TestCaseIndex(index = "q14")
public class SolelyTransitivelyRelatedConcepts extends AbstractCollectionTupleQskosTestCase {

	@Override
	protected String getQskosIssueCode() {
		return "strc";
	}

}
