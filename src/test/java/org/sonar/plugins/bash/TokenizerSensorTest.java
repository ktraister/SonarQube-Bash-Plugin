package org.sonar.plugins.bash;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.utils.internal.JUnitTempFolder;

public class TokenizerSensorTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@org.junit.Rule
	public JUnitTempFolder temp = new JUnitTempFolder();

	@Test
	public void testExecute() throws IOException {
		SensorContextTester ctxTester = SensorContextTester.create(folder.getRoot().getAbsoluteFile().toPath());
		ctxTester.settings().setProperty(Constants.SH_EXECUTABLE, "bash");
		File baseFile = folder.newFile("test.sh");

		FileUtils.copyURLToFile(getClass().getResource("/testFiles/test.sh"), baseFile);
		DefaultInputFile ti = new TestInputFileBuilder(folder.getRoot().getAbsolutePath(), "test.sh")
				.initMetadata(new String(Files.readAllBytes(baseFile.toPath()))).setLanguage(BashLanguage.KEY)
				.build();

		ctxTester.fileSystem().add(ti);

		final TokenizerSensor sut = new TokenizerSensor(temp);
		sut.execute(ctxTester);

		Assert.assertEquals(16, ctxTester.cpdTokens(ti.key()).size());
		Assert.assertEquals(2, ctxTester.measures(ti.key()).size());
		Assert.assertEquals(1, ctxTester.highlightingTypeAt(ti.key(), 1, 30).size());

	}

}
