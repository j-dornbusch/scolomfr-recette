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
package fr.scolomfr.recette.cli.commands.output;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import com.github.zafarkhaja.semver.Version;

import de.vandermeer.asciitable.v2.RenderedTable;
import de.vandermeer.asciitable.v2.V2_AsciiTable;
import de.vandermeer.asciitable.v2.render.V2_AsciiTableRenderer;
import de.vandermeer.asciitable.v2.render.WidthLongestLine;
import de.vandermeer.asciitable.v2.themes.V2_E_TableThemes;

@Component
public class AcsiiConsoleFormatter implements ConsoleFormatter {

	private RenderedTable getRenderedTable(V2_AsciiTable at) {
		V2_AsciiTableRenderer rend = new V2_AsciiTableRenderer();
		rend.setTheme(V2_E_TableThemes.UTF_STRONG_DOUBLE.get());
		WidthLongestLine width = new WidthLongestLine();

		rend.setWidth(width);
		RenderedTable rt = rend.render(at);
		return rt;
	}

	@Override
	public String formatFormats(List<Pair<String, Pair<String, String>>> filePathsByVersion) {
		V2_AsciiTable at = new V2_AsciiTable();
		at.addStrongRule();
		for (Pair<String, Pair<String, String>> source : filePathsByVersion) {
			at.addRow(source.getFirst().toString(), source.getSecond().getFirst(), source.getSecond().getSecond());
			at.addRule();
		}
		return getRenderedTable(at).toString();
	}

	@Override
	public String formatVersions(List<Pair<Version, Pair<String, String>>> filePathsByFormat) {
		V2_AsciiTable at = new V2_AsciiTable();
		at.addStrongRule();
		for (Pair<Version, Pair<String, String>> source : filePathsByFormat) {
			at.addRow(source.getFirst().toString(), source.getSecond().getFirst(), source.getSecond().getSecond());
			at.addRule();
		}
		return getRenderedTable(at).toString();
	}

	@Override
	public String formatTestOrganisation(Map<String, Map<String, Map<String, Map<String, String>>>> testsOrganisation) {
		V2_AsciiTable at = new V2_AsciiTable();
		at.addStrongRule();
		Set<Entry<String, Map<String, Map<String, Map<String, String>>>>> requirementsEntries = testsOrganisation
				.entrySet();
		for (Entry<String, Map<String, Map<String, Map<String, String>>>> requirementEntry : requirementsEntries) {
			at.addStrongRule();
			at.addRow(null, null,
					requirementEntry.getValue().get("index") + " : " + requirementEntry.getValue().get("label"));
			at.addStrongRule();
			Map<String, Map<String, String>> tests = requirementEntry.getValue().get("tests");
			if (null == tests) {
				continue;
			}
			for (Entry<String, Map<String, String>> entry : tests.entrySet()) {
				at.addRow("", entry.getValue().get("index"), entry.getValue().get("label"));
				at.addRule();
			}
		}
		return getRenderedTable(at).toString();
	}

}