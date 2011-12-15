package de.fips.plugin.tinyaudioplayer.io;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;

import org.junit.Test;
import org.mockito.InOrder;
public class MThreeUFileReaderTest {
	@Test
	public void whenInvoked_parse_shouldCallVisitor() throws Exception {
		// setup
		final File testFile = new File(getClass().getResource("playlist.m3u").toURI());
		final IPlaylistFileVisitor visitor = mock(IPlaylistFileVisitor.class);
		final MThreeUFileReader reader = new MThreeUFileReader(visitor);
		final InOrder inOrder = inOrder(visitor);
		// run
		reader.read(testFile);
		// assert
		inOrder.verify(visitor).visitBegin(eq(testFile));
		inOrder.verify(visitor).visitEntryBegin();
		inOrder.verify(visitor).visitEntryEnd();
		inOrder.verify(visitor).visitEntryBegin();
		inOrder.verify(visitor).visitEntryEnd();
		inOrder.verify(visitor).visitEntryBegin();
		inOrder.verify(visitor).visitEntryEnd();
		inOrder.verify(visitor).visitEnd(eq(testFile));

		verify(visitor).visitLocation(eq(new File("01 - Track 01.mp3").toURI()));
		verify(visitor).visitTitle(eq("Artist - Track 01"));
		verify(visitor).visitLength(eq(220L));
		verify(visitor).visitLocation(eq(new File("Chapter 03 - Title.mp3").toURI()));
		verify(visitor).visitTitle(eq("Author - Book - Chapter 03 - Title"));
		verify(visitor).visitLength(eq(1167L));
		verify(visitor).visitLocation(eq(new File("Chapter 04 - Title.mp3").toURI()));
		verify(visitor).visitNumberOfEntries(eq(3));
	}
}
