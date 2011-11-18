/*
 * Copyright © 2011 Philipp Eichhorn.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.fips.plugin.tinyaudioplayer.audio;

import static de.fips.plugin.tinyaudioplayer.audio.PlaylistItemTag.playlistItemTag;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import lombok.NoArgsConstructor;

import org.tritonus.share.sampled.file.TAudioFileFormat;

@NoArgsConstructor()
public class PlaylistItemTagFactory {
	private final static Map<String, IPlaylistItemTagBuilder> builder = new HashMap<String, IPlaylistItemTagBuilder>();
	static {
		builder.put("mp3", new MpegPlaylistItemTagBuilder());
		builder.put("ogg", new OggPlaylistItemTagBuilder());
		builder.put("flac", new FlacPlaylistItemTagBuilder());
	}

	public PlaylistItemTag formURI(final URI uri) {
		PlaylistItemTag tag = null;
		try {
			AudioFileFormat aff = AudioSystem.getAudioFileFormat(new File(uri));
			final String type = aff.getType().toString().toLowerCase();
			final IPlaylistItemTagBuilder tagBuilder = builder.get(type);
			if (tagBuilder != null) {
				tag = tagBuilder.fromAudioFileFormat(aff);
			}
		} catch (IllegalArgumentException ignore) {
			// File(URI) preconditions did not hold
		} catch (UnsupportedAudioFileException ignore) {
		} catch (IOException ignore) {
		}
		return tag;
	}

	private static class OggPlaylistItemTagBuilder implements IPlaylistItemTagBuilder {
		@Override
		public PlaylistItemTag fromAudioFileFormat(final AudioFileFormat aff) {
			final PlaylistItemTag.$OptionalDef builder = playlistItemTag();
			if (aff instanceof TAudioFileFormat) {
				final Map<?, ?> props = ((TAudioFileFormat) aff).properties();
				Object currentValue = props.get("ogg.channels");
				if (currentValue != null) {
					builder.channels((Integer) currentValue);
				}
				currentValue = props.get("ogg.frequency.hz");
				if (currentValue != null) {
					builder.samplingRate((Integer) currentValue);
				}
				currentValue = props.get("ogg.bitrate.nominal.bps");
				if (currentValue != null) {
					builder.bitRate((Integer) currentValue);
				}
				currentValue = props.get("title");
				if (currentValue != null) {
					builder.title((String) currentValue);
				}
				currentValue = props.get("author");
				if (currentValue != null) {
					builder.artist((String) currentValue);
				}
				currentValue = props.get("album");
				if (currentValue != null) {
					builder.album((String) currentValue);
				}
				currentValue = props.get("year");
				if (currentValue != null) {
					builder.year((String) currentValue);
				}
				currentValue = props.get("duration");
				if (currentValue != null) {
					builder.playTime(((Long) currentValue) / 1000000L);
				}
				currentValue = props.get("ogg.comment.genre");
				if (currentValue != null) {
					builder.genre((String) currentValue);
				}
				currentValue = props.get("ogg.comment.track");
				if (currentValue != null) {
					try {
						builder.track(new Integer((String) currentValue));
					} catch (NumberFormatException ignore) {
					}
				}
			}
			return builder.build();
		}
	}

	private static class MpegPlaylistItemTagBuilder implements IPlaylistItemTagBuilder {
		@Override
		public PlaylistItemTag fromAudioFileFormat(final AudioFileFormat aff) {
			final PlaylistItemTag.$OptionalDef builder = playlistItemTag();
			if (aff instanceof TAudioFileFormat) {
				final Map<?, ?> props = ((TAudioFileFormat) aff).properties();
				Object currentValue = props.get("mp3.channels");
				if (currentValue != null) {
					builder.channels((Integer) currentValue);
				}
				currentValue = props.get("mp3.frequency.hz");
				if (currentValue != null) {
					builder.samplingRate((Integer) currentValue);
				}
				currentValue = props.get("mp3.bitrate.nominal.bps");
				if (currentValue != null) {
					builder.bitRate((Integer) currentValue);
				}
				currentValue = props.get("title");
				if (currentValue != null) {
					builder.title((String) currentValue);
				}
				currentValue = props.get("author");
				if (currentValue != null) {
					builder.artist((String) currentValue);
				}
				currentValue = props.get("album");
				if (currentValue != null) {
					builder.album((String) currentValue);
				}
				currentValue = props.get("year");
				if (currentValue != null) {
					builder.year((String) currentValue);
				}
				currentValue = props.get("duration");
				if (currentValue != null) {
					builder.playTime(((Long) currentValue) / 1000000L);
				}
				currentValue = props.get("mp3.id3tag.genre");
				if (currentValue != null) {
					final StringBuilder genre = new StringBuilder();
					final Pattern pattern = Pattern.compile("[^()]+");
					final Matcher matcher = pattern.matcher((String) currentValue);
					while (matcher.find()) {
						final String id3tagGenre = matcher.group();
						try {
							genre.append(ID3TAG_GENRES[Integer.valueOf(id3tagGenre)]);
						} catch (NumberFormatException e) {
							genre.append(id3tagGenre);
						}
					}
					builder.genre(genre.toString());
				}
				currentValue = props.get("mp3.id3tag.track");
				if (currentValue != null) {
					try {
						builder.track(Integer.valueOf((String) currentValue));
					} catch (NumberFormatException ignore) {
					}
				}
			}
			return builder.build();
		}
	}

	private static class FlacPlaylistItemTagBuilder implements IPlaylistItemTagBuilder {
		@Override
		public PlaylistItemTag fromAudioFileFormat(final AudioFileFormat aff) {
			final AudioFormat af = aff.getFormat();
			return playlistItemTag() //
				.channels(af.getChannels()) //
				.samplingRate((int) af.getSampleRate()) //
				.bitRate(af.getSampleSizeInBits()).build();
		}
	}

	private static interface IPlaylistItemTagBuilder {
		public PlaylistItemTag fromAudioFileFormat(final AudioFileFormat aff);
	}

	// http://www.id3.org/id3v2.3.0#head-129376727ebe5309c1de1888987d070288d7c7e7
	private final static String[] ID3TAG_GENRES = new String[] { "Blues", "Classic Rock", "Country", "Dance", "Disco", "Funk", "Grunge", "Hip-Hop", "Jazz", "Metal", "New Age",
		"Oldies", "Other", "Pop", "R&B", "Rap", "Reggae", "Rock", "Techno", "Industrial", "Alternative", "Ska", "Death Metal", "Pranks", "Soundtrack", "Euro-Techno",
		"Ambient", "Trip-Hop", "Vocal", "Jazz+Funk", "Fusion", "Trance", "Classical", "Instrumental", "Acid", "House", "Game", "Sound Clip", "Gospel", "Noise", "AlternRock",
		"Bass", "Soul", "Punk", "Space", "Meditative", "Instrumental Pop", "Instrumental Rock", "Ethnic", "Gothic", "Darkwave", "Techno-Industrial", "Electronic", "Pop-Folk",
		"Eurodance", "Dream", "Southern Rock", "Comedy", "Cult", "Gangsta", "Top 40", "Christian Rap", "Pop/Funk", "Jungle", "Native American", "Cabaret", "New Wave",
		"Psychadelic", "Rave", "Showtunes", "Trailer", "Lo-Fi", "Tribal", "Acid Punk", "Acid Jazz", "Polka", "Retro", "Musical", "Rock & Roll", "Hard Rock",
		// These were made up by the authors of Winamp but backported into the ID3 spec
		"Folk", "Folk-Rock", "National Folk", "Swing", "Fast Fusion", "Bebob", "Latin", "Revival", "Celtic", "Bluegrass", "Avantgarde", "Gothic Rock", "Progressive Rock",
		"Psychedelic Rock", "Symphonic Rock", "Slow Rock", "Big Band", "Chorus", "Easy Listening", "Acoustic", "Humour", "Speech", "Chanson", "Opera", "Chamber Music",
		"Sonata", "Symphony", "Booty Bass", "Primus", "Porn Groove", "Satire", "Slow Jam", "Club", "Tango", "Samba", "Folklore", "Ballad", "Power Ballad", "Rhythmic Soul",
		"Freestyle", "Duet", "Punk Rock", "Drum Solo", "A capella", "Euro-House", "Dance Hall",
		// These were also invented by the Winamp folks but ignored by the ID3 authors.
		"Goa", "Drum & Bass", "Club-House", "Hardcore", "Terror", "Indie", "BritPop", "Negerpunk", "Polsk Punk", "Beat", "Christian Gangsta Rap", "Heavy Metal", "Black Metal",
		"Crossover", "Contemporary Christian", "Christian Rock", "Merengue", "Salsa", "Thrash Metal", "Anime", "Jpop", "Synthpop" };
}
