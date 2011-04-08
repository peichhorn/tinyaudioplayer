package de.fips.plugin.tinyaudioplayer.io;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class FileUtilsTest {
	@Test
	public void testFileNameWithoutExtension() throws Exception {
		assertEquals(".file", FileUtils.fileNameWithoutExtension(new File(".file")));
		assertEquals("file", FileUtils.fileNameWithoutExtension(new File("file")));
		assertEquals("file", FileUtils.fileNameWithoutExtension(new File("file.ext")));
	}

	@Test
	public void testRelativePath() throws Exception {
		// -dirA
		// -dirB
		assertEquals("..\\dirA", FileUtils.relativePath(new File("dirA").getAbsoluteFile(), new File("dirB")));
		// -dirA-dirB
		// -dirC
		assertEquals("..\\dirA\\dirB", FileUtils.relativePath(new File(new File("dirA"), "dirB"), new File("dirC")));
		// -dirA
		// -dirB-dirC
		assertEquals("..\\..\\dirA", FileUtils.relativePath(new File("dirA"), new File(new File("dirB"), "dirC")));
	}
}
