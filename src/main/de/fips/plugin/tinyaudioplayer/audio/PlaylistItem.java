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

import java.io.File;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author: Philipp Eichhorn
 */
@EqualsAndHashCode(of={"displayableName", "infoTag"})
@ToString(of={"displayableName", "infoTag"})
public class PlaylistItem {
	@Getter
	private final String name;
	@Getter
	private final long seconds;
	@Getter
	private String location;
	private String displayableName;
	private PlaylistItemTag infoTag;

	public PlaylistItem(final String name, final String location, final long seconds) {
		this.name = name;
		this.seconds = seconds;
		setLocation(location, true);
	}

	public long getLength() {
		return ((infoTag == null) || (infoTag.getPlayTime() <= 0)) ? seconds : infoTag.getPlayTime();
	}

	public int getBitrate() {
		return (infoTag == null) ? -1 : infoTag.getBitRate();
	}

	public int getSamplerate() {
		return (infoTag == null) ? -1 : infoTag.getSamplingRate();
	}

	public int getChannels() {
		return (infoTag == null) ? -1 : infoTag.getChannels();
	}

	public void setLocation(final String l) {
		setLocation(l, false);
	}

	public void setLocation(final String l, final boolean readInfo) {
		location = l;
		if (readInfo && !StringUtils.isEmpty(location)) {
			infoTag = new PlaylistItemTagFactory().formFile(new File(l));
		}
		displayableName = null;
		getDisplayableName();
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

	public String getDisplayableName() {
		if (displayableName == null) {
			if (infoTag != null) {
				final StringBuilder builder = new StringBuilder();
				if (!(StringUtils.isEmpty(infoTag.getTitle()) || StringUtils.isEmpty(infoTag.getArtist()))) {
					builder.append(infoTag.getArtist()).append(" - ").append(infoTag.getTitle());
				} else if (!StringUtils.isEmpty(infoTag.getTitle())) {
					builder.append(infoTag.getTitle());
				} else {
					builder.append(name);
				}
				if (getLength() > 0) {
					builder.append(" (").append(getDisplayableLength()).append(")");
				}
				displayableName = builder.toString();
			} else {
				displayableName = name;
			}
		}
		return displayableName;
	}

	public PlaylistItemTag getInfoTag() {
		if (infoTag == null) {
			setLocation(location, true);
		}
		return infoTag;
	}
}
