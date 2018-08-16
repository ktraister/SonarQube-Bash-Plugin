package org.sonar.plugins.bash;

import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.PropertyType;
import org.sonar.api.config.PropertyDefinition;

@Properties({})
public class BashPlugin implements Plugin {

	public void define(final Context context) {
		context.addExtension(PropertyDefinition.builder(Constants.SKIP_TOKENIZER).name("Skip tokenizer")
				.description("Flag whether to skip tokenizer").defaultValue("false").type(PropertyType.BOOLEAN)
				.build());
		context.addExtension(PropertyDefinition.builder(Constants.SKIP_PLUGIN).name("Skip plugin")
				.description("Flag whether to skip plugin").defaultValue("false").type(PropertyType.BOOLEAN)
				.build());
		context.addExtension(PropertyDefinition.builder(Constants.SH_EXECUTABLE).name("Path to /bin/bashcutable")
				.description("Path to /bin/bashcutable").defaultValue("/bin/bash").type(PropertyType.STRING)
				.build());

		context.addExtension(PropertyDefinition.builder(Constants.FILE_SUFFIXES).name("Suffixes to analyze")
				.description("Suffixes supported by the plugin").defaultValue(".sh,.bash,.zsh,.ksh")
				.type(PropertyType.STRING).build());

		context.addExtensions(BashLanguage.class, BashQualityProfile.class);
		context.addExtensions(ScriptAnalyzerRulesDefinition.class, ScriptAnalyzerSensor.class);
		context.addExtension(TokenizerSensor.class);
	}
}
