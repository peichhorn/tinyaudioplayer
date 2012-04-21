package de.fips.plugin.tinyaudioplayer.io;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Test;

public class StringsTest {
	@Test
	public void givenEmptyLines_filterNonEmpty_shouldReturnNonEmptyLines() throws Exception {
		// setup
		final Iterable<String> lines = Strings.filterNonEmpty(Arrays.asList("a", "b", "", " \t ", "c"));
		// run + assert
		assertThat(lines.iterator()).containsOnly("a", "b", " \t ", "c");
	}

	@Test
	public void givenLinesWithWhitespaces_trim_shouldReturnTrimmedLines() throws Exception {
		// setup
		final Iterable<String> lines = Strings.trim(Arrays.asList("a ", "\tb", "", " \t ", "c"));
		// run + assert
		assertThat(lines.iterator()).containsOnly("a", "b", "", "", "c");
	}
}
