package org.sonar.plugins.bash.fillers;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.highlighting.NewHighlighting;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.bash.ast.Tokens;
import org.sonar.plugins.bash.ast.Tokens.Token;

public class HighlightingFiller implements IFiller {

	private static final Logger LOGGER = Loggers.get(HighlightingFiller.class);

	public void fill(final SensorContext context, final InputFile f, final Tokens tokens) {

		try {
			final NewHighlighting highlighting = context.newHighlighting().onFile(f);
			for (final Token token : tokens.getToken()) {
				highlightToken(highlighting, token);
			}
			highlighting.save();
		} catch (Throwable e) {
			LOGGER.warn("Exception while running highlighting", e);
		}
	}

	@SuppressWarnings("deprecation")
	private static void highlightToken(final NewHighlighting highlighting, final Token token) {
		try {
			final List<String> kinds = Arrays.asList(token.getTokenFlags().toLowerCase().split(","));

			if (check("comment", token, kinds)) {
				highlighting.highlight(token.getStartOffset(), token.getEndOffset(), TypeOfText.COMMENT);
				return;
			}

			if (check("keyword", token, kinds)) {
				highlighting.highlight(token.getStartOffset(), token.getEndOffset(), TypeOfText.KEYWORD);
				return;
			}
			if (check("StringLiteral", token, kinds) || check("StringExpandable", token, kinds)) {
				highlighting.highlight(token.getStartOffset(), token.getEndOffset(), TypeOfText.STRING);
				return;
			}
			if (check("Variable", token, kinds)) {
				highlighting.highlight(token.getStartOffset(), token.getEndOffset(), TypeOfText.KEYWORD_LIGHT);
			}

		} catch (Throwable e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.warn("Exception while adding highliting for: " + token, e);
			}
		}
	}

	private static boolean check(final String txt, final Token token, final List<String> kinds) {
		return txt.equalsIgnoreCase(token.getKind()) || kinds.contains(txt.toLowerCase());
	}
}
