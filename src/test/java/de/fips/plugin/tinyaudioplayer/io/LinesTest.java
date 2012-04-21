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

public class LinesTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void givenNonExsistingFile_in_shouldThrowException() throws Exception {
		// setup
		final File nonExistingFile = new File("non.existingfile");
		// run + assert
		thrown.expect(FileNotFoundException.class);
		Lines.in(nonExistingFile);
	}

	@Test
	public void remove_shouldThrowException() throws Exception {
		// setup
		final StringReader reader = new StringReader("a\nb\nc");
		final Iterator<String> lines = Lines.in(reader).iterator();
		// run + assert
		thrown.expect(UnsupportedOperationException.class);
		lines.remove();
	}

	@Test
	public void givenNoLine_next_shouldThrowException() throws Exception {
		// setup
		final StringReader reader = new StringReader("");
		final Iterator<String> lines = Lines.in(reader).iterator();
		// run + assert
		assertThat(lines.hasNext()).isFalse();
		thrown.expect(NoSuchElementException.class);
		lines.next();
	}

	@Test
	public void givenMultipleLines_next_shouldReturnEachLine() throws Exception {
		// setup
		final StringReader reader = new StringReader("a\nb\n\n \t \nc");
		final Iterable<String> lines = Lines.in(reader);
		// run + assert
		assertThat(lines.iterator()).containsOnly("a", "b", "", " \t ", "c");
	}
}
