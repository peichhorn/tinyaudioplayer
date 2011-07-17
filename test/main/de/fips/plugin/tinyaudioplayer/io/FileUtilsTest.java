package de.fips.plugin.tinyaudioplayer.io;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URI;

import org.junit.Test;

public class FileUtilsTest {
	@Test
	public void whenFilenameStartsWithDot_fileNameWithoutExtension_shouldReturnFilename() throws Exception {
		assertEquals(".file", FileUtils.fileNameWithoutExtension(new File(".file")));
	}

	@Test
	public void whenFilenameHasNoExtension_fileNameWithoutExtension_shouldReturnFilename() throws Exception {
		assertEquals("file", FileUtils.fileNameWithoutExtension(new File("file")));
	}

	@Test
	public void whenFilenameHasExtension_fileNameWithoutExtension_shouldReturnFilenameWithoutExtension() throws Exception {
		assertEquals("file", FileUtils.fileNameWithoutExtension(new File("file.ext")));
	}

	@Test
	public void testRelativePath_File() throws Exception {
		// .\dirA
		// .\dirB
		assertEquals("..\\dirA", FileUtils.relativePath(new File("dirA").getAbsoluteFile(), new File("dirB")));
		// .\dirA\dirB
		// .\dirC
		assertEquals("..\\dirA\\dirB", FileUtils.relativePath(new File(new File("dirA"), "dirB"), new File("dirC")));
		// .\dirA
		// .\dirB\dirC
		assertEquals("..\\..\\dirA", FileUtils.relativePath(new File("dirA"), new File(new File("dirB"), "dirC")));
		// D:\dirA
		// C:\dirB\dirC
		assertEquals("D:\\dirA", FileUtils.relativePath(new File("D:\\dirA"), new File(new File("C:\\dirB"), "dirC")));
	}
	
	@Test
	public void testRelativePath_URI() throws Exception {
		// .\dirA\dirB
		// .\dirC
		assertEquals("..\\dirA\\dirB", FileUtils.relativePath(new File(new File("dirA"), "dirB").toURI(), new File("dirC")));
		// http://127.0.0.1:8015/stream
		// .\dirB
		assertEquals("http://127.0.0.1:8015/stream", FileUtils.relativePath(new URI("http://127.0.0.1:8015/stream"), new File("dirB")));
	}
}
