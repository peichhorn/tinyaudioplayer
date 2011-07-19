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
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.Delegate;
import lombok.ListenerSupport;
import lombok.NoArgsConstructor;

/**
 *
 * @author: Philipp Eichhorn
 */
@NoArgsConstructor
@ListenerSupport(IPlaylistListener.class)
public class Playlist implements Iterable<PlaylistItem>, Cloneable {
	@Delegate(types = IListSubset.class)
	private final List<PlaylistItem> tracks = new CopyOnWriteArrayList<PlaylistItem>();
	private final List<PlaylistItem> selectedTracks = new CopyOnWriteArrayList<PlaylistItem>();
	private int currentIndex = -1;
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
			final int oldSize = tracks.size();
			if (allowDuplicates) {
				for (final PlaylistItem track : other.tracks) {
					add(track);
				}
			} else {
				final Set<String> uniqueTrackNames = new HashSet<String>();
				for (final PlaylistItem track : tracks) {
					uniqueTrackNames.add(track.getDisplayableName());
				}
				for (final PlaylistItem track : other.tracks) {
					if (uniqueTrackNames.add(track.getDisplayableName())) {
						add(track);
					}
				}
			}
			return tracks.size() - oldSize;
		}
		return 0;
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
		remove(tracks.indexOf(track));
		
	}

	public void removeCurrent() {
		remove(currentIndex);
	}
	
	public void invertSelection() {
		final List<PlaylistItem> newSelectedTracks = new ArrayList<PlaylistItem>(tracks);
		newSelectedTracks.removeAll(selectedTracks);
		selectedTracks.clear();
		selectedTracks.addAll(newSelectedTracks);
	}

	public void removeSelected() {
		for (final PlaylistItem track : selectedTracks) {
			remove(track);
		}
		selectedTracks.clear();
	}
	
	public int removeDuplicates() {
		if (!isEmpty()) {
			final String currentTrackName = getCurrentTrack().getDisplayableName();
			final Set<String> uniqueTrackNames = new HashSet<String>();
			final List<PlaylistItem> oldTracks = new ArrayList<PlaylistItem>(tracks);
			tracks.clear();
			currentIndex = 0;
			firePlaylistCleared();
			for (final PlaylistItem track : oldTracks) {
				final String trackName = track.getDisplayableName();
				if (uniqueTrackNames.add(trackName)) {
					if (currentTrackName.equals(trackName)) {
						currentIndex = tracks.size();
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
			final PlaylistItem track = tracks.get(index);
			tracks.remove(index);
			if (tracks.isEmpty()) {
				currentIndex = -1;
				fireTrackRemoved(track);
				firePlaylistCleared();
			} else {
				if (currentIndex >= index) {
					currentIndex--;
				}
				fireTrackRemoved(track);
				fireTrackChanged(getCurrentTrack());
			}
		}
	}

	public PlaylistItem getCurrentTrack() {
		if (isValidIndex(currentIndex)) {
			return tracks.get(currentIndex);
		}
		return null;
	}

	public void setCurrentTrack(final PlaylistItem item) {
		setCurrentTrack(tracks.indexOf(item));
	}

	public void setCurrentTrack(final int index) {
		if (isValidIndex(index)) {
			currentIndex = index;
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
		currentIndex = -1;
		firePlaylistCleared();
	}

	public void previous() {
		updateIndex((tracks.size() - 1), 0, -1);
	}

	public void next() {
		updateIndex(0, (tracks.size() - 1), 1);
	}
	
	public Playlist clone() {
		final Playlist clone = new Playlist();
		clone.add(this);
		return clone;
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
	
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append(this.getClass().getSimpleName()).append("\n");
		int trackCounter = 1;
		for (PlaylistItem track : tracks) {
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
