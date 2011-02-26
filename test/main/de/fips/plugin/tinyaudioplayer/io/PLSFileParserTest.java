package de.fips.plugin.tinyaudioplayer.io;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;

import org.junit.Test;

public class PLSFileParserTest {
	@Test
	public void readM3U() throws Exception {
		final File testFile = new File(getClass().getResource("playlist.pls").toURI());
		final IPlaylistFileVisitor visitor = mock(IPlaylistFileVisitor.class);
		final PLSFileParser reader = new PLSFileParser(visitor);
		reader.parse(testFile);
		verify(visitor).visitBegin(eq(testFile));
		verify(visitor, times(2)).visitEntryBegin();
		verify(visitor, times(2)).visitEntryEnd();
		verify(visitor).visitFile(eq(new File("01 - Track 01.mp3")));
		verify(visitor).visitTitle(eq("Artist - Track 01"));
		verify(visitor).visitLength(eq(Long.valueOf(220)));
		verify(visitor).visitFile(eq(new File("Chapter 03 - Title.mp3")));
		verify(visitor).visitTitle(eq("Author - Book - Chapter 03 - Title"));
		verify(visitor).visitLength(eq(Long.valueOf(1167)));
		verify(visitor).visitEnd(eq(testFile));
	}
}
