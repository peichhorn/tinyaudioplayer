/*
Copyright © 2011 Philipp Eichhorn.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package de.fips.plugin.tinyaudioplayer.audio;

import static java.util.concurrent.TimeUnit.*;

import java.net.URI;

import lombok.Getter;
import lombok.LazyGetter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author: Philipp Eichhorn
 */
@RequiredArgsConstructor
@Getter
@ToString(of = { "displayableName" })
public class PlaylistItem {
	private final String name;
	private final URI location;
	private final long seconds;
	@LazyGetter
	private final String displayableName = createDisplayableName();
	@LazyGetter
	private final PlaylistItemTag infoTag = createInfoTag();

	public long getLength() {
		final PlaylistItemTag tag = getInfoTag();
		return ((tag == null) || (tag.getPlayTime() <= 0)) ? seconds : tag.getPlayTime();
	}

	public int getBitRate() {
		final PlaylistItemTag tag = getInfoTag();
		return (tag == null) ? -1 : tag.getBitRate();
	}

	public int getSampleRate() {
		final PlaylistItemTag tag = getInfoTag();
		return (tag == null) ? -1 : tag.getSamplingRate();
	}

	public int getChannels() {
		final PlaylistItemTag tag = getInfoTag();
		return (tag == null) ? -1 : tag.getChannels();
	}

	public String getDisplayableLength() {
		final long seconds = getLength();
		if (seconds < 0) return "unknown";
		final long millis = SECONDS.toMillis(getLength());
		final long hour = HOURS.toMillis(1);
		if (millis < hour) {
			return String.format("%1$TM:%1$TS", millis);
		} else {
			return String.format("%d:%2$TM:%2$TS", millis / hour, millis % hour);
		}
	}

	private String createDisplayableName() {
		final PlaylistItemTag tag = getInfoTag();
		final StringBuilder builder = new StringBuilder();
		if (tag != null) {
			if (!(StringUtils.isEmpty(tag.getTitle()) || StringUtils.isEmpty(tag.getArtist()))) {
				builder.append(tag.getArtist()).append(" - ").append(tag.getTitle());
			} else if (!StringUtils.isEmpty(tag.getTitle())) {
				builder.append(tag.getTitle());
			} else {
				builder.append(name);
			}
		} else {
			builder.append(name);
		}
		if (getLength() > 0) {
			builder.append(" (").append(getDisplayableLength()).append(")");
		}
		return builder.toString();
	}

	private PlaylistItemTag createInfoTag() {
		return new PlaylistItemTagFactory().formURI(getLocation());
	}
}
