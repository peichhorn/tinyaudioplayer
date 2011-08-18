package de.fips.plugin.tinyaudioplayer.junit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistItem;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public final class Assertions {
	public static PlaylistItemAssert assertThat(PlaylistItem actual) {
		return new PlaylistItemAssert(actual);
	}
}
