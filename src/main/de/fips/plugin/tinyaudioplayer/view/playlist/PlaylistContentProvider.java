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
package de.fips.plugin.tinyaudioplayer.view.playlist;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import de.fips.plugin.tinyaudioplayer.audio.IPlaylistListener;
import de.fips.plugin.tinyaudioplayer.audio.Playlist;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistItem;

public class PlaylistContentProvider implements IStructuredContentProvider, IPlaylistListener {
	private Playlist playlist;
	private Viewer viewer;

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		this.viewer = viewer;
		playlist = (Playlist) oldInput;
		removeListener();
		playlist = (Playlist) newInput;
		addListener();
	}

	@Override
	public Object[] getElements(final Object inputElement) {
		return ((Playlist) inputElement).toArray();
	}

	@Override
	public void dispose() {
		removeListener();
	}

	@Override
	public void trackEnqueued(final PlaylistItem item) {
		refresh();
	}

	@Override
	public void trackChanged(final PlaylistItem item) {
		refresh();
	}

	@Override
	public void trackRemoved(final PlaylistItem item) {
		refresh();
	}

	@Override
	public void playlistCleared() {
		refresh();
	}

	private void refresh() {
		if (viewer != null) {
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					viewer.refresh();
				}
			});
		}
	}

	private void addListener() {
		if (playlist != null) {
			playlist.addPlaylistListener(this);
		}
	}

	private void removeListener() {
		if (playlist != null) {
			playlist.removePlaylistListener(this);
		}
	}
}