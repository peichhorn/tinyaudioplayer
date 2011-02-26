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

import java.io.File;

import org.apache.commons.lang.StringUtils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(of={"displayableName", "infoTag"})
@ToString(of={"displayableName", "infoTag"})
public class PlaylistItem {
	@Getter
	protected final String name;
	protected String displayableName;
	@Getter
	protected String location;
	@Getter
	protected final long seconds;
	protected PlaylistItemTag infoTag;

	public PlaylistItem(final String name, final String location, final long seconds) {
		this.name = name;
		this.seconds = seconds;
		setLocation(location, true);
	}

	public long getLength() {
		long length = seconds;
		if ((infoTag != null) && (infoTag.getPlayTime() > 0)) {
			length = infoTag.getPlayTime();
		}
		return length;
	}

	public int getBitrate() {
		int bitrate = -1;
		if (infoTag != null) {
			bitrate = infoTag.getBitRate();
		}
		return bitrate;
	}

	public int getSamplerate() {
		int samplerate = -1;
		if (infoTag != null) {
			samplerate = infoTag.getSamplingRate();
		}
		return samplerate;
	}

	public int getChannels() {
		int channels = -1;
		if (infoTag != null) {
			channels = infoTag.getChannels();
		}
		return channels;
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
		getFormattedDisplayName();
	}

	public String getFormattedLength() {
		long seconds = getLength();
		final StringBuilder length = new StringBuilder();
		if (seconds > -1) {
			long minutes = seconds / 60;
			long hours = minutes / 60;
			minutes %= 60;
			seconds %= 60;
			if (hours > 0) {
				if (hours <= 9) length.append("0");
				length.append(hours).append(":");
			}
			if (minutes <= 9) length.append("0");
			length.append(minutes).append(":");
			if (seconds <= 9) length.append("0");
			length.append(seconds);
		} else {
			length.append("unknown");
		}
		return length.toString();
	}

	public String getFormattedDisplayName() {
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
					builder.append(" (").append(getFormattedLength()).append(")");
				}
				displayableName = builder.toString();
			} else {
				displayableName = name;	
			}
		}
		return displayableName;
	}
	
	public void setFormattedDisplayName(final String fname) {
		displayableName = fname;
	}

	public PlaylistItemTag getInfoTag() {
		if (infoTag == null) {
			setLocation(location, true);
		}
		return infoTag;
	}
}
