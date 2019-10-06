package ch.romix.schirizettel.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.junit.Assert;
import org.junit.Test;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class DataTransformerTest {

	@Test
	public void testTransformation() throws Exception {
		File srcFile = File.createTempFile("junitSource", "tmp");
		srcFile.deleteOnExit();
		File destFile = File.createTempFile("junitDest", "tmp");
		destFile.deleteOnExit();
		try (CSVWriter source = new CSVWriter(new OutputStreamWriter(new FileOutputStream(srcFile)), ',', '"')) {
			source.writeNext(new String[] { "TeamA", "TeamB" });

			source.writeNext(new String[] { "Ruswil", "Rickenbach" });
			source.writeNext(new String[] { "Wolhusen", "Langnau" });
			source.writeNext(new String[] { "Wolhusen", "Ruswil" });
			source.writeNext(new String[] { "Rickenbach", "Langnau" });
			source.writeNext(new String[] { "Langnau", "Ruswil" });
		}
		DataTransformer.transformDataToThreeDatasetsARow(new FileInputStream(srcFile), destFile);

		try (CSVReader result = new CSVReader(new InputStreamReader(new FileInputStream(destFile)), ',', '"')) {
			String[] header = result.readNext();
			assertLine("TeamA1", "TeamB1", "TeamA2", "TeamB2", "TeamA3", "TeamB3", header);
			String[] line = result.readNext();
			Assert.assertNotNull(line);
			assertLine("Ruswil", "Rickenbach", "Wolhusen", "Ruswil", "Langnau", "Ruswil", line);
			line = result.readNext();
			Assert.assertNotNull(line);
			assertLine("Wolhusen", "Langnau", "Rickenbach", "Langnau", "", "", line);
		}
	}

	private void assertLine(String s0, String s1, String s2, String s3, String s4, String s5, String[] line) {
		Assert.assertEquals(s0, line[0]);
		Assert.assertEquals(s1, line[1]);
		Assert.assertEquals(s2, line[2]);
		Assert.assertEquals(s3, line[3]);
		Assert.assertEquals(s4, line[4]);
		Assert.assertEquals(s5, line[5]);
	}

}
