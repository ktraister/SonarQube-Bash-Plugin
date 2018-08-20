package org.sonar.plugins.bash;

import java.io.File;
import java.io.*;

import javax.xml.bind.JAXBContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.config.Settings;
import org.sonar.api.utils.TempFolder;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.bash.fillers.IssuesFiller;
import org.sonar.plugins.bash.issues.Objects;

public class ScriptAnalyzerSensor implements org.sonar.api.batch.sensor.Sensor {

	private final TempFolder folder;

	private static final String shCommand = "%s %s %s";

	private static final Logger LOGGER = Loggers.get(ScriptAnalyzerSensor.class);

	private final IssuesFiller issuesFiller;

	public ScriptAnalyzerSensor(final TempFolder folder) {
		this.folder = folder;
		this.issuesFiller = new IssuesFiller();

	}

	public void describe(final SensorDescriptor descriptor) {
		descriptor.onlyOnLanguage(BashLanguage.KEY).name(this.getClass().getSimpleName());
	}

	public void execute(final SensorContext context) {

		final Settings settings = context.settings();
		final boolean skipPlugin = settings.getBoolean(Constants.SKIP_PLUGIN);

		if (skipPlugin) {
			LOGGER.debug("Skipping sensor as skip plugin flag is set");
			return;
		}

		final String bashExecutable = settings.getString(Constants.SH_EXECUTABLE);

		try {
			final File parserFile = folder.newFile("sh", "scriptAnalyzer.sh");

			try {
				FileUtils.copyURLToFile(getClass().getResource("/scriptAnalyzer.sh"), parserFile);
			} catch (final Throwable e1) {
				LOGGER.warn("Exception while copying tokenizer script", e1);
				return;
			}
			final String scriptFile = parserFile.getAbsolutePath();
			final File resultsFile = folder.newFile();
			LOGGER.info("resultsFile " + resultsFile);
			final FileSystem fileSystem = context.fileSystem();
			final File sourceDir = fileSystem.baseDir().toPath().toFile();

			final String command = String.format(shCommand, scriptFile, sourceDir.getAbsolutePath(),
					resultsFile.toPath().toFile().getAbsolutePath());

			try {
				LOGGER.info(String.format("Starting running bash analysis: %s", command));
				//final Process process = new ProcessBuilder(bashExecutable, command).start();
				final Process process = new ProcessBuilder("/bin/bash", scriptFile, sourceDir.getAbsolutePath(), resultsFile.toPath().toFile().getAbsolutePath()).start();
				process.waitFor();
				LOGGER.info("process exit value: " + process.exitValue());
				LOGGER.info("process: " + process);
				LOGGER.info("Finished running bash analysis");

			} catch (final Throwable e) {
				LOGGER.warn("Error executing Bash script analyzer. Maybe shellcheck is not installed?", e);
				return;
			}


			LOGGER.info("start of outfile output");
			BufferedReader br = new BufferedReader(new FileReader(resultsFile));
			String st;
                        while ((st = br.readLine()) != null) {
                            LOGGER.info(st);
                        }
			LOGGER.info("end of outfile output");

			LOGGER.info("results: " + resultsFile);
			//https://stackoverflow.com/questions/17059227/read-xml-file-with-jaxb
			final JAXBContext jaxbContext = JAXBContext.newInstance(Objects.class);
			final Objects issues = (Objects) jaxbContext.createUnmarshaller().unmarshal(resultsFile);
			LOGGER.info("ISSUES: " + issues);
			this.issuesFiller.fill(context, sourceDir, issues);
		} catch (Throwable e) {
			LOGGER.warn("Unexpected exception while running analysis", e);
		}

	}

}
