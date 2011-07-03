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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import lombok.ListenerSupport;
import lombok.NoArgsConstructor;

/**
 *
 * @author: Philipp Eichhorn
 */
@NoArgsConstructor
@ListenerSupport(IPlaylistListener.class)
public class Playlist implements Iterable<PlaylistItem> {
	private final List<PlaylistItem> tracks = new ArrayList<PlaylistItem>();
	private int currentIndex = -1;
	private boolean shuffle;
	private boolean repeat;

	public void toggleShuffle() {
		shuffle = !shuffle;
	}

	public void toggleRepeat() {
		repeat = !repeat;
	}

	public int add(final Playlist other) {
		return add(other, true);
	}

	public int add(final Playlist other, final boolean removeDuplicates) {
		if (other != null) {
			if (removeDuplicates) {
				final Set<PlaylistItem> set = new HashSet<PlaylistItem>(tracks);
				final int oldSize = tracks.size();
				for (final PlaylistItem track : other.tracks) {
					if (set.add(track)) {
						add(track);
					}
				}
				return tracks.size() - oldSize;
			} else {
				for (final PlaylistItem track : other) {
					add(track);
				}
				return other.tracks.size();
			}
		}
		return 0;
	}

	public int removeDuplicates() {
		if (hasTracks()) {
			final int oldSize = tracks.size();
			final PlaylistItem currentTrack = getCurrentTrack();
			final Set<PlaylistItem> set = new HashSet<PlaylistItem>();
			final List<PlaylistItem> uniqueTracks = new ArrayList<PlaylistItem>();
			for (final PlaylistItem track : tracks) {
				if (set.add(track)) {
					uniqueTracks.add(track);
				}
			}
			tracks.clear();
			tracks.addAll(uniqueTracks);
			currentIndex = tracks.indexOf(currentTrack);
			fireTrackChanged(currentTrack);
			return tracks.size() - oldSize;
		}
		return 0;
	}

	public boolean hasTracks() {
		return !tracks.isEmpty();
	}

	public int size() {
		return tracks.size();
	}

	public Object[] toArray() {
		return tracks.toArray();
	}

	public void add(final PlaylistItem track) {
		tracks.add(track);
		if (currentIndex < 0) {
			currentIndex = 0;
			fireTrackEnqueued(track);
			fireTrackChanged(track);
		} else {
			fireTrackEnqueued(track);
		}
	}

	public void remove(final PlaylistItem track) {
		final int index = tracks.indexOf(track);
		if (index >= 0) {
			tracks.remove(index);
			if (tracks.isEmpty()) {
				currentIndex = -1;
				fireTrackRemoved(track);
				firePlaylistCleared();
			} else {
				if (currentIndex > index) {
					currentIndex--;
				}
				fireTrackRemoved(track);
				fireTrackChanged(getCurrentTrack());
			}
		}
	}

	public void removeCurrent() {
		tracks.remove(currentIndex);
		if (tracks.isEmpty()) {
			currentIndex = -1;
			fireTrackRemoved(getCurrentTrack());
			firePlaylistCleared();
		} else {
			if (currentIndex >= tracks.size()) {
				currentIndex = tracks.size() - 1;
			}
			fireTrackRemoved(getCurrentTrack());
			fireTrackChanged(getCurrentTrack());
		}
	}

	public PlaylistItem getCurrentTrack() {
		if ((currentIndex >= 0) && (currentIndex < tracks.size())) {
			return tracks.get(currentIndex);
		}
		if (hasTracks()) {
			throw new IllegalStateException("Invalid 'currentIndex' in non-emtpy playlist!");
		}
		return null;
	}

	public void setCurrentTrack(final PlaylistItem item) {
		setCurrentTrack(tracks.indexOf(item));
	}

	public void setCurrentTrack(final int index) {
		if ((index >= 0) && (index < tracks.size())) {
			currentIndex = index;
		}
	}

	public PlaylistItem getPreviousTrack() {
		previous();
		return getCurrentTrack();
	}

	public PlaylistItem getNextTrack() {
		next();
		return getCurrentTrack();
	}

	public void clear() {
		tracks.clear();
		currentIndex = -1;
		firePlaylistCleared();
	}

	public void previous() {
		updateIndex((tracks.size() - 1), 0, -1);
	}

	public void next() {
		updateIndex(0, (tracks.size() - 1), 1);
	}

	private void updateIndex(final int first, final int last, final int diff) {
		if (shuffle) {
			currentIndex = new Random().nextInt(tracks.size());
		} else if (currentIndex == last) {
			if (repeat) {
				currentIndex = first;
			} else {
				currentIndex = last;
			}
		} else {
			currentIndex += diff;
		}
		fireTrackChanged(getCurrentTrack());
	}

	@Override
	public Iterator<PlaylistItem> iterator() {
		return tracks.iterator();
	}
}
