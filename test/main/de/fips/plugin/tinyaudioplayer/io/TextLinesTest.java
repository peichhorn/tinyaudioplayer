package de.fips.plugin.tinyaudioplayer.io;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.StringReader;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TextLinesTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void whenFileIsIllegal_textLinesIn_shouldThrowException() throws Exception {
		// setup
		final File nonExistingFile = new File("non.existingfile");
		// run + assert
		thrown.expect(IllegalArgumentException.class);
		TextLines.textLinesIn(nonExistingFile);
	}

	@Test
	public void whenInvoked_remove_shouldThrowException() throws Exception {
		// setup
		final StringReader reader = new StringReader("a\nb\nc");
		final TextLines lines = TextLines.textLinesIn(reader);
		// run + assert
		thrown.expect(UnsupportedOperationException.class);
		lines.remove();
	}

	@Test
	public void whenNoMoreItemsAreAvailable_next_shouldThrowException() throws Exception {
		// setup
		final StringReader reader = new StringReader("");
		final TextLines lines = TextLines.textLinesIn(reader).ignoringEmptyLines();
		// run + assert
		assertFalse(lines.hasNext());
		thrown.expect(NoSuchElementException.class);
		lines.next();
	}

	@Test
	public void whenNotOtherwiseConfigured_next_shouldReturnEmptyLines() throws Exception {
		// setup
		final StringReader reader = new StringReader("a\nb\n\nc");
		final TextLines lines = TextLines.textLinesIn(reader);
		final List<String> allExpected = asList("a", "b", "", "c");
		// run + assert
		for (String expected : allExpected) {
			assertTrue(lines.hasNext());
			assertEquals(expected, lines.next());
		}
		assertFalse(lines.hasNext());
	}

	@Test
	public void whenConfigured_next_shouldIgnoreEmptyLines() throws Exception {
		// setup
		final StringReader reader = new StringReader("a\nb\n\nc");
		final TextLines lines = TextLines.textLinesIn(reader).ignoringEmptyLines();
		final List<String> allExpected = asList("a", "b", "c");
		// run + assert
		for (String expected : allExpected) {
			assertTrue(lines.hasNext());
			assertEquals(expected, lines.next());
		}
		assertFalse(lines.hasNext());
	}
}
