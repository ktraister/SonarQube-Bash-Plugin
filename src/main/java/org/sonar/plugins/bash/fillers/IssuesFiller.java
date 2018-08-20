package org.sonar.plugins.bash.fillers;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.scan.filesystem.PathResolver;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.bash.ScriptAnalyzerRulesDefinition;
import org.sonar.plugins.bash.issues.Objects;
import org.sonar.plugins.bash.issues.Objects.Object.Property;

public class IssuesFiller {

	private static final Logger LOGGER = Loggers.get(IssuesFiller.class);

	public void fill(final SensorContext context, final File sourceDir, final Objects issues) {
		final FileSystem fileSystem = context.fileSystem();
		int x = 0;
		for (final Objects.Object o : issues.getObject()) {
			try {
				x = 0;
				final List<Objects.Object.Property> props = o.getProperty();
				x = 1;
				final String ruleName = getProperty("RuleName", props);
				x = 2;
				final String initialFile = getProperty("File", props);
				x = 3;
				final String fsFile = new PathResolver().relativePath(sourceDir, new File(initialFile));
				x = 4;
				final String message = getProperty("Message", props);
				x = 5; 
				final String line = getProperty("Line", props);
				x = 6;
				int issueLine = getLine(line);
				x = 7;

				//it's breaking on the next line
				final RuleKey ruleKey = RuleKey.of(ScriptAnalyzerRulesDefinition.getRepositoryKeyForLanguage(),
						ruleName);
				x = 8; 
				final org.sonar.api.batch.fs.InputFile file = fileSystem
						.inputFile(fileSystem.predicates().and(fileSystem.predicates().hasRelativePath(fsFile)));
				x = 9; 
				if (file == null) {
					LOGGER.warn(String.format("File %s not found", fsFile));
					continue;
				}
				final NewIssue issue = context.newIssue().forRule(ruleKey);
				final NewIssueLocation loc = issue.newLocation().message(message).on(file);
				if (issueLine > 0) {
					loc.at(file.selectLine(issueLine));
				}
				issue.at(loc);
				issue.save();
			} catch (final Throwable e) {
				LOGGER.warn("Unexpected exception while adding issue", e);
				LOGGER.warn("x: " + x);
			}

		}
	}

	private int getLine(final String line) {
		int issueLine = -1;
		if (StringUtils.isNotEmpty(line)) {
			try {
				issueLine = Integer.parseInt(line);
			} catch (Throwable e) {
				LOGGER.debug(String.format("Was not able to parse line: '%s'", line));
			}
		}
		return issueLine;
	}

	private static String getProperty(final String key, final List<Objects.Object.Property> props) {
		for (final Property p : props) {
			if (key.equalsIgnoreCase(p.getName())) {
				return p.getValue();
			}
		}
		return "";
	}
}
