package de.fips.plugin.tinyaudioplayer.junit;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.net.URI;

import org.fest.assertions.GenericAssert;

import de.fips.plugin.tinyaudioplayer.audio.PlaylistItem;

public class PlaylistItemAssert extends GenericAssert<PlaylistItemAssert, PlaylistItem> {

	PlaylistItemAssert(final PlaylistItem actual) {
		super(PlaylistItemAssert.class, actual);
	}

	public PlaylistItemAssert hasName(final String name) {
		isNotNull();
		final String errorMessage = String.format("Expected item name to be <%s> but was <%s>", name, actual.getName());
		assertThat(actual.getName()).overridingErrorMessage(errorMessage).isEqualTo(name);
		return this;
	}

	public PlaylistItemAssert hasLocation(final File location) {
		return hasLocation(location.toURI());
	}

	public PlaylistItemAssert hasLocation(final URI location) {
		isNotNull();
		String errorMessage = String.format("Expected item location to be <%s> but was <%s>", location, actual.getLocation());
		assertThat(actual.getLocation()).overridingErrorMessage(errorMessage).isEqualTo(location);
		return this;
	}

	public PlaylistItemAssert hasLength(final int length) {
		isNotNull();
		String errorMessage = String.format("Expected item length to be <%s> but was <%s>", length, actual.getLength());
		assertThat(actual.getLength()).overridingErrorMessage(errorMessage).isEqualTo(length);
		return this;
	}
}
