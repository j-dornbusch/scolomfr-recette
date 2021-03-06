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
package fr.scolomfr.recette.model.tests.impl.caseconventions;

import java.util.Iterator;
import java.util.List;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;

import fr.scolomfr.recette.model.sources.representation.utils.JenaEngine;
import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.execution.result.ResultImpl.State;
import fr.scolomfr.recette.model.tests.impl.AbstractJenaTestCase;
import fr.scolomfr.recette.model.tests.impl.DuplicateErrorCodeException;
import fr.scolomfr.recette.model.tests.organization.TestCaseIndex;
import fr.scolomfr.recette.model.tests.organization.TestParameters;

/**
 * Check respect of case conventions
 */
@TestCaseIndex(index = "a23")
@TestParameters(names = { TestParameters.Values.VERSION, TestParameters.Values.VOCABULARY,
		TestParameters.Values.SKOSTYPE })
public class CaseConventionsRespectSkos extends AbstractJenaTestCase {

	@Override
	public void run() {
		int numerator = 0;
		int denominator = 0;
		progressionMessage(i18n.tr("tests.impl.data.loading.title"), 0);
		String format = getSkosType();
		Model model = getModel(getVersion(), getVocabulary(), format);
		if (model == null) {
			return;
		}
		Property prefLabel = model.getProperty(JenaEngine.Constant.SKOS_CORE_NS.toString(),
				JenaEngine.Constant.SKOS_PREFLABEL_PROPERTY.toString());
		Property altLabel = model.getProperty(JenaEngine.Constant.SKOS_CORE_NS.toString(),
				JenaEngine.Constant.SKOS_ALTLABEL_PROPERTY.toString());
		Selector prefLabelSelector = new SimpleSelector(null, prefLabel, (RDFNode) null);
		Selector altLabelSelector = new SimpleSelector(null, altLabel, (RDFNode) null);
		StmtIterator stmts1 = model.listStatements(prefLabelSelector);
		StmtIterator stmts2 = model.listStatements(altLabelSelector);
		ExtendedIterator<Statement> stmts = stmts1.andThen(stmts2);
		Resource vocab001 = model.getResource("http://data.education.fr/voc/scolomfr/scolomfr-voc-001");
		Resource vocab006 = model.getResource("http://data.education.fr/voc/scolomfr/scolomfr-voc-006");
		Resource vocab024 = model.getResource("http://data.education.fr/voc/scolomfr/scolomfr-voc-024");
		List<Statement> list = stmts.toList();
		int numberOfStatements = list.size();
		Iterator<Statement> it = list.iterator();
		while (it.hasNext()) {
			denominator++;
			if (denominator % 100 == 0) {
				progressionMessage("", (float) denominator / (float) numberOfStatements * 100.f);
			}
			Statement statement = it.next();

			if (jenaEngine.memberOfVocab(vocab001, statement.getSubject(), model)
					|| jenaEngine.memberOfVocab(vocab024, statement.getSubject(), model)
					|| jenaEngine.memberOfVocab(vocab006, statement.getSubject(), model)) {
				continue;
			}
			Literal labelLit = (Literal) statement.getObject();
			Property predicate = statement.getPredicate();

			String label = labelLit.getString();
			String errorCode = null;
			try {
				errorCode = generateUniqueErrorCode(statement, predicate, label);
			} catch (DuplicateErrorCodeException e1) {
				logger.trace(ERROR_CODE_DUPLICATE, errorCode, e1);
				continue;
			}

			boolean ignored = errorIsIgnored(errorCode);
			Character firstLetter = label.charAt(0);
			if (!Character.isLetter(firstLetter)) {
				continue;
			}
			boolean firstLetterIsLower = Character.isLowerCase(firstLetter);
			Boolean isSigle = isSigle(label);

			if (!firstLetterIsLower && !isSigle) {
				numerator++;
				incrementErrorCount(ignored);
				Message message = new Message(ignored ? Message.Type.IGNORED : Message.Type.ERROR, errorCode,
						i18n.tr("tests.impl.a23.result.invalid.title"),
						i18n.tr("tests.impl.a23.result.invalid.content", new Object[] { statement.getSubject().getURI(),
								statement.getPredicate().getLocalName(), label }));
				addMessage(message);
			}
			if (!firstLetterIsLower && isSigle) {
				Message message = new Message(Message.Type.INFO, errorCode,
						i18n.tr("tests.impl.a23.result.ignored.title"),
						i18n.tr("tests.impl.a23.result.ignored.content", new Object[] { statement.getSubject().getURI(),
								statement.getPredicate().getLocalName(), label }));
				addMessage(message);
			}
			refreshComplianceIndicator(denominator - numerator, denominator);

		}
		progressionMessage("", 100);
		setState(State.FINAL);
	}

	private Boolean isSigle(String label) {
		Character firstLetter = label.charAt(0);
		boolean firstLetterIsLower = Character.isLowerCase(firstLetter);
		Character secondLetter = label.length() > 1 ? label.charAt(1) : 'X';
		boolean secondLetterIsLower = label.length() > 1 && Character.isLowerCase(secondLetter);
		return !firstLetterIsLower && !secondLetterIsLower;
	}

	private String generateUniqueErrorCode(Statement statement, Property predicate, String label)
			throws DuplicateErrorCodeException {
		return generateUniqueErrorCode(
				Integer.toString(label.hashCode()) + '_' + statement.getSubject() + '_' + predicate.getLocalName());
	}

}
