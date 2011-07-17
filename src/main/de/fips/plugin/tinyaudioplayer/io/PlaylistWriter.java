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
package de.fips.plugin.tinyaudioplayer.io;

import java.io.File;
import java.io.IOException;

import de.fips.plugin.tinyaudioplayer.TinyAudioPlayerPlugin;
import de.fips.plugin.tinyaudioplayer.audio.Playlist;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistItem;

public class PlaylistWriter extends AbstractWriter<Playlist>{

	@Override
	public String formatName() {
		return "Playlist File";
	}

	@Override
	public String formatExtensions() {
		return "*.m3u;*.pls";
	}

	@Override
	public void write(final File file, final Playlist playlist) {
		IPlaylistFileVisitor visitor = null;
		if (file.getName().toLowerCase().endsWith(".m3u")) {
			visitor = new MThreeUFileWriter();
		} else if (file.getName().toLowerCase().endsWith(".pls")) {
			visitor = new PLSFileWriter();
		}
		if (visitor != null) {
			try {
				visitor.visitBegin(file);
				for (final PlaylistItem pli : playlist) {
					visitor.visitEntryBegin();
					visitor.visitLocation(pli.getLocation());
					visitor.visitLength(pli.getSeconds());
					visitor.visitTitle(pli.getName());
					visitor.visitEntryEnd();
				}
				visitor.visitNumberOfEntries(playlist.size());
				visitor.visitEnd(file);
			} catch (IOException e) {
				TinyAudioPlayerPlugin.logErr("Writing playlist failed!", e);
			}
		} else {
			TinyAudioPlayerPlugin.logErr("Writing playlist failed!");
		}
	}
}
