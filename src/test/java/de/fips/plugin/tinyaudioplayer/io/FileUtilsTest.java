package de.fips.plugin.tinyaudioplayer.io;

import static de.fips.plugin.tinyaudioplayer.io.FileUtils.*;
import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.net.URI;

import org.junit.Test;

public class FileUtilsTest {
	@Test
	public void whenFilenameStartsWithDot_fileNameWithoutExtension_shouldReturnFilename() throws Exception {
		assertThat(fileNameWithoutExtension(new File(".file"))).isEqualTo(".file");
	}

	@Test
	public void whenFilenameHasNoExtension_fileNameWithoutExtension_shouldReturnFilename() throws Exception {
		assertThat(fileNameWithoutExtension(new File("file"))).isEqualTo("file");
	}

	@Test
	public void whenFilenameHasExtension_fileNameWithoutExtension_shouldReturnFilenameWithoutExtension() throws Exception {
		assertThat(fileNameWithoutExtension(new File("file.ext"))).isEqualTo("file");
	}

	@Test
	public void testRelativePath_File() throws Exception {
		// .\dirA
		// .\dirB
		assertThat(relativePath(new File("dirA"), new File("dirB"))).isEqualTo("..\\dirA");
		// .\dirA\dirB
		// .\dirC
		assertThat(relativePath(new File(new File("dirA"), "dirB"), new File("dirC"))).isEqualTo("..\\dirA\\dirB");
		// .\dirA
		// .\dirB\dirC
		assertThat(relativePath(new File("dirA"), new File(new File("dirB"), "dirC"))).isEqualTo("..\\..\\dirA");
		// D:\dirA
		// C:\dirB\dirC
		assertThat(relativePath(new File("D:\\dirA"), new File(new File("C:\\dirB"), "dirC"))).isEqualTo("D:\\dirA");
	}
	
	@Test
	public void testRelativePath_URI() throws Exception {
		// .\dirA\dirB
		// .\dirC
		assertThat(relativePath(new File(new File("dirA"), "dirB").toURI(), new File("dirC"))).isEqualTo("..\\dirA\\dirB");
		// http://127.0.0.1:8015/stream
		// .\dirB
		assertThat(relativePath(new URI("http://127.0.0.1:8015/stream"), new File("dirB"))).isEqualTo("http://127.0.0.1:8015/stream");
	}
}
