package org.sonar.plugins.bash;

import org.sonar.api.config.Settings;
import org.sonar.api.resources.AbstractLanguage;

public class BashLanguage extends AbstractLanguage {

	public static final String NAME = "Bash";
	public static final String KEY = "sh";
	public static final String[] DEFAULT_FILE_SUFFIXES = new String[] { "sh", "bash", "zsh", "ksh" };
	private final Settings settings;

	public BashLanguage(final Settings settings) {
		super(KEY, NAME);
		this.settings = settings;

	}

	public String[] getFileSuffixes() {
		final String[] suffixes = this.settings.getStringArray(Constants.FILE_SUFFIXES);
		if (suffixes == null || suffixes.length == 0) {
			return DEFAULT_FILE_SUFFIXES;
		}
		return suffixes;
	}

}
