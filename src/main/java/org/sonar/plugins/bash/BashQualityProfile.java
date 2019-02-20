package org.sonar.plugins.bash;

import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileParser;
import org.sonar.api.utils.ValidationMessages;

public class BashQualityProfile extends ProfileDefinition {
	private XMLProfileParser xmlProfileParser;

	public BashQualityProfile(XMLProfileParser xmlProfileParser) {
		this.xmlProfileParser = xmlProfileParser;
	}

	public RulesProfile createProfile(final ValidationMessages validation) {
		return xmlProfileParser.parseResource(getClass().getClassLoader(), "bash-rules.xml", validation)
				.setName(Constants.PROFILE_NAME).setLanguage(BashLanguage.KEY);
	}
}
