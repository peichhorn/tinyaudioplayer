package de.fips.plugin.tinyaudioplayer.io;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TextLinesTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void whenFileIsIllegal_in_shouldThrowException() throws Exception {
		// setup
		final File nonExistingFile = new File("non.existingfile");
		// run + assert
		thrown.expect(FileNotFoundException.class);
		TextLines.in(nonExistingFile, false);
	}

	@Test
	public void whenInvoked_remove_shouldThrowException() throws Exception {
		// setup
		final StringReader reader = new StringReader("a\nb\nc");
		final Iterator<String> lines = TextLines.in(reader, false).iterator();
		// run + assert
		thrown.expect(UnsupportedOperationException.class);
		lines.remove();
	}

	@Test
	public void whenNoMoreItemsAreAvailable_next_shouldThrowException() throws Exception {
		// setup
		final StringReader reader = new StringReader("");
		final Iterator<String> lines = TextLines.in(reader, true).iterator();
		// run + assert
		assertThat(lines.hasNext()).isFalse();
		thrown.expect(NoSuchElementException.class);
		lines.next();
	}

	@Test
	public void whenNotOtherwiseConfigured_next_shouldReturnEmptyLines() throws Exception {
		// setup
		final StringReader reader = new StringReader("a\nb\n\nc");
		final Iterable<String> lines = TextLines.in(reader, false);
		// run + assert
		assertThat(lines.iterator()).containsOnly("a", "b", "", "c");
	}

	@Test
	public void whenConfigured_next_shouldIgnoreEmptyLines() throws Exception {
		// setup
		final StringReader reader = new StringReader("a\nb\n\nc");
		final Iterable<String> lines = TextLines.in(reader, true);
		// run + assert
		assertThat(lines.iterator()).containsOnly("a", "b", "c");
	}
}
