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
package de.fips.plugin.tinyaudioplayer.view.playlist;

import lombok.RequiredArgsConstructor;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Color;

import de.fips.plugin.tinyaudioplayer.TinyAudioPlayer;
import de.fips.plugin.tinyaudioplayer.audio.Playlist;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistItem;

@RequiredArgsConstructor
public class PlaylistItemLabelProvider extends StyledCellLabelProvider {
	private final TinyAudioPlayer player;

	@Override
	public void update(final ViewerCell cell) {
		final PlaylistItem item = (PlaylistItem)cell.getElement();
		cell.setText(item.getDisplayableName());
		if (player != null) {
			final Playlist playlist = player.getPlaylist();
			if (!playlist.isEmpty() && (item.equals(playlist.getCurrentTrack()))) {
				cell.setBackground(new Color(null, 225, 225, 225));
			} else {
				cell.setBackground(new Color(null, 255, 255, 255));
			}
		}
	}
}