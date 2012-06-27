/*
 * Copyright © 2011-2012 Philipp Eichhorn.
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.*;

@NoArgsConstructor
@ListenerSupport(IPlaylistListener.class)
public class Playlist implements Iterable<PlaylistItem> {
	@Delegate(types = IListSubset.class)
	private final List<PlaylistItem> tracks = new CopyOnWriteArrayList<PlaylistItem>();
	private final List<PlaylistItem> selectedTracks = new CopyOnWriteArrayList<PlaylistItem>();
	private AtomicInteger currentIndex = new AtomicInteger(-1);
	private volatile boolean shuffle;
	private volatile boolean repeat;

	public void toggleShuffle() {
		shuffle = !shuffle;
	}

	public void toggleRepeat() {
		repeat = !repeat;
	}

	public int add(final Playlist other) {
		return add(other, true);
	}

	public int add(final Playlist other, final boolean allowDuplicates) {
		if (other != null) {
			val oldSize = tracks.size();
			if (allowDuplicates) {
				for (val track : other.tracks) {
					add(track);
				}
			} else {
				val uniqueTrackNames = new HashSet<String>();
				for (val track : tracks) {
					uniqueTrackNames.add(track.getDisplayableName());
				}
				for (val track : other.tracks) {
					if (uniqueTrackNames.add(track.getDisplayableName())) {
						add(track);
					}
				}
			}
			return tracks.size() - oldSize;
		}
		return 0;
	}

	public void add(final List<PlaylistItem> tracks) {
		if (tracks != null) for (PlaylistItem track : tracks) {
			add(track);
		}
	}

	public void add(final PlaylistItem track) {
		if (tracks != null) {
			tracks.add(track);
			if (currentIndex.get() < 0) {
				currentIndex.set(0);
				fireTrackEnqueued(track);
				fireTrackChanged(track);
			} else {
				fireTrackEnqueued(track);
			}
		}
	}

	public void remove(final PlaylistItem track) {
		remove(tracks.indexOf(track));

	}

	public void removeCurrent() {
		remove(currentIndex.get());
	}

	public void removeSelected() {
		for (final PlaylistItem track : selectedTracks) {
			remove(track);
		}
		selectedTracks.clear();
	}

	public int removeDuplicates() {
		if (!isEmpty()) {
			val currentTrackName = getCurrentTrack().getDisplayableName();
			val uniqueTrackNames = new HashSet<String>();
			val oldTracks = new ArrayList<PlaylistItem>(tracks);
			tracks.clear();
			currentIndex.set(0);
			firePlaylistCleared();
			for (val track : oldTracks) {
				val trackName = track.getDisplayableName();
				if (uniqueTrackNames.add(trackName)) {
					if (currentTrackName.equals(trackName)) {
						currentIndex.set(tracks.size());
					}
					tracks.add(track);
					fireTrackEnqueued(track);
				}
			}
			return oldTracks.size() - tracks.size();
		}
		return 0;
	}

	private void remove(final int index) {
		if (isValidIndex(index)) {
			val track = tracks.get(index);
			tracks.remove(index);
			if (tracks.isEmpty()) {
				currentIndex.set(-1);
				fireTrackRemoved(track);
				firePlaylistCleared();
			} else {
				if (currentIndex.get() >= index) {
					currentIndex.decrementAndGet();
				}
				fireTrackRemoved(track);
				fireTrackChanged(getCurrentTrack());
			}
		}
	}

	public PlaylistItem getCurrentTrack() {
		if (isValidIndex(currentIndex.get())) {
			return tracks.get(currentIndex.get());
		}
		return null;
	}

	public void setCurrentTrack(final PlaylistItem item) {
		setCurrentTrack(tracks.indexOf(item));
	}

	public void setCurrentTrack(final int index) {
		if (isValidIndex(index)) {
			currentIndex.set(index);
		}
	}

	private boolean isValidIndex(final int index) {
		return (index >= 0) && (index < tracks.size());
	}

	public void selectTracks(final List<PlaylistItem> items) {
		selectedTracks.clear();
		selectedTracks.addAll(items);
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
		currentIndex.set(-1);
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
			currentIndex.set(new Random().nextInt(tracks.size()));
		} else if (currentIndex.get() == last) {
			if (repeat) {
				currentIndex.set(first);
			} else {
				currentIndex.set(last);
			}
		} else {
			currentIndex.addAndGet(diff);
		}
		fireTrackChanged(getCurrentTrack());
	}

	public String toString() {
		val builder = new StringBuilder();
		builder.append(this.getClass().getSimpleName()).append("\n");
		int trackCounter = 1;
		for (val track : tracks) {
			if (selectedTracks.contains(track)) {
				builder.append("[").append(trackCounter++).append("] - ");
			} else {
				builder.append(" ").append(trackCounter++).append("  - ");
			}
			builder.append(track).append("\n");
		}
		return builder.toString();
	}

	private static interface IListSubset {
		boolean isEmpty();

		int size();

		Object[] toArray();

		Iterator<PlaylistItem> iterator();
	}
}
