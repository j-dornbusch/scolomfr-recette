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
package fr.scolomfr.recette.model.tests.impl.standardizedvocabularies;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import fr.scolomfr.recette.model.tests.execution.result.CommonMessageKeys;
import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.execution.result.Result.State;
import fr.scolomfr.recette.model.tests.impl.AbstractJenaTestCase;
import fr.scolomfr.recette.model.tests.impl.DuplicateErrorCodeException;
import fr.scolomfr.recette.model.tests.organization.TestCaseIndex;
import fr.scolomfr.recette.model.tests.organization.TestParameters;

/**
 * Check the spelling of skos langstrings
 */
@TestCaseIndex(index = "a4")
@TestParameters(names = { TestParameters.Values.VERSION, TestParameters.Values.SKOSTYPE })
public class MimeTypesCompleteness extends AbstractJenaTestCase {

	private static final String MEDIATYPES_URI_PREFIX = "http://purl.org/NET/mediatypes/";
	private static final String VOC_06_URI = "http://data.education.fr/voc/scolomfr/scolomfr-voc-006";
	private static final String IANA_URI = "http://www.iana.org/assignments/media-types/media-types.xml";
	private int numberOfMimeTypesInSkos;
	private int numerator;
	private int denominator;
	private int progressionCounter;
	ArrayList<String> mimeTypeUrisInSkos;

	@Override
	public void run() {
		numerator = 0;
		denominator = 0;
		progressionCounter = 0;
		progressionMessage(i18n.tr("tests.impl.data.loading.title"), 0);
		String format = getSkosType();
		Model model = getModel(getVersion(), getVocabulary(), format);
		Resource vocab006 = model.getResource(VOC_06_URI);
		List<Resource> mimeTypesInSkos = jenaEngine.getMembersOfVocab(vocab006, model);
		numberOfMimeTypesInSkos = mimeTypesInSkos.size();
		Iterator<Resource> it = mimeTypesInSkos.iterator();
		mimeTypeUrisInSkos = new ArrayList<>();
		String uri;
		while (it.hasNext()) {

			uri = it.next().getURI();
			uri = uri.replace(MEDIATYPES_URI_PREFIX, "");
			mimeTypeUrisInSkos.add(uri);
		}
		try {
			XMLReader myReader = XMLReaderFactory.createXMLReader();
			MimeTypesXMLContenthandler handler = new MimeTypesXMLContenthandler();
			handler.setOwner(this);
			myReader.setContentHandler(handler);
			myReader.parse(new InputSource(new URL(IANA_URI).openStream()));

		} catch (IOException | SAXException e) {
			Message message = new Message(Message.Type.FAILURE, getErrorCode(CommonMessageKeys.NETWORK_ERROR.name()),
					i18n.tr("tests.impl.a4.result.datafailure.title"),
					i18n.tr("tests.impl.a4.result.datafailure.content", new Object[] { e.getMessage() }));
			result.addMessage(message);
			stopTestCase();
			return;
		}
		Iterator<String> it2 = mimeTypeUrisInSkos.iterator();
		while (it2.hasNext()) {
			String mimeTypeUriInSkos = it2.next();
			String errorCode = getErrorCode("missinginiana" + MESSAGE_ID_SEPARATOR + mimeTypeUriInSkos);
			boolean ignored = errorIsIgnored(errorCode);

			if (StringUtils.isEmpty(errorCode)) {
				return;
			}
			denominator++;
			progressionCounter++;
			progressionMessage("", (float) progressionCounter / (float) numberOfMimeTypesInSkos * 100.f);

			result.incrementErrorCount(ignored);
			Message message = new Message(ignored ? Message.Type.IGNORED : Message.Type.ERROR, errorCode,
					i18n.tr("tests.impl.a4.result.missinginiana.title"),
					i18n.tr("tests.impl.a4.result.missinginiana.content", new Object[] { mimeTypeUriInSkos }));
			result.addMessage(message);
			refreshComplianceIndicator(result, denominator - numerator, denominator);
		}
		progressionMessage("", 100);
		result.setState(State.FINAL);
	}

	public void submitMimeType(String mimeTypeFromIana) {
		boolean obsolete = false;
		boolean deprecated = false;
		if (mimeTypeFromIana.contains("OBSOLETE")) {
			obsolete = true;
		}
		if (mimeTypeFromIana.contains("DEPRECATED")) {
			deprecated = true;
		}
		if (obsolete || deprecated) {
			mimeTypeFromIana = mimeTypeFromIana.replaceAll("\\s.*$", "");
		}
		if (mimeTypeUrisInSkos.contains(mimeTypeFromIana)) {
			denominator++;
			progressionCounter++;
			progressionMessage("", (float) progressionCounter / (float) numberOfMimeTypesInSkos * 100.f);
			mimeTypeUrisInSkos.remove(mimeTypeFromIana);
			if (obsolete || deprecated) {
				numerator++;
				String state = obsolete ? "obsolete" : "deprecated";
				String errorCode = getErrorCode(state + MESSAGE_ID_SEPARATOR + mimeTypeFromIana);
				boolean ignored = errorIsIgnored(errorCode);

				if (StringUtils.isEmpty(errorCode)) {
					return;
				}
				result.incrementErrorCount(ignored);
				Message message = new Message(ignored ? Message.Type.IGNORED : Message.Type.ERROR, errorCode,
						i18n.tr("tests.impl.a4.result." + state + ".title"),
						i18n.tr("tests.impl.a4.result." + state + ".content", new Object[] { mimeTypeFromIana }));
				result.addMessage(message);
			}
		} else {

			if (obsolete || deprecated) {
				String state = obsolete ? "obsolete" : "deprecated";
				String errorCode = getErrorCode(state + MESSAGE_ID_SEPARATOR + mimeTypeFromIana);
				if (StringUtils.isEmpty(errorCode)) {
					return;
				}
				Message message = new Message(Message.Type.INFO, errorCode,
						i18n.tr("tests.impl.a4.result.missinginskos." + state + ".title"),
						i18n.tr("tests.impl.a4.result.missinginskos." + state + ".content",
								new Object[] { mimeTypeFromIana }));
				result.addMessage(message);
			} else {
				numerator++;
				String errorCode = getErrorCode("missinginskos" + MESSAGE_ID_SEPARATOR + mimeTypeFromIana);
				boolean ignored = errorIsIgnored(errorCode);

				if (StringUtils.isEmpty(errorCode)) {
					return;
				}
				result.incrementErrorCount(ignored);
				Message message = new Message(ignored ? Message.Type.IGNORED : Message.Type.ERROR, errorCode,
						i18n.tr("tests.impl.a4.result.missinginskos.title"),
						i18n.tr("tests.impl.a4.result.missinginskos.content", new Object[] { mimeTypeFromIana }));
				result.addMessage(message);
			}

		}
		refreshComplianceIndicator(result, denominator - numerator, denominator);
	}

	private String getErrorCode(String mimeType) {
		String errorCode = null;
		try {
			errorCode = generateUniqueErrorCode(mimeType);
		} catch (DuplicateErrorCodeException e1) {
			logger.debug("Errorcode {} generated twice ", errorCode, e1);
		}
		return errorCode;
	}

}