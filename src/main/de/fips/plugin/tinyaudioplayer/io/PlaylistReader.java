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

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.fips.plugin.tinyaudioplayer.TinyAudioPlayerConstants;
import de.fips.plugin.tinyaudioplayer.TinyAudioPlayerPlugin;
import de.fips.plugin.tinyaudioplayer.audio.Playlist;

public class PlaylistReader extends AbstractReader<Playlist> {
	private final transient ILog log = TinyAudioPlayerPlugin.getDefaultLog();
	
	public String formatName() {
		return "Playlist File";
	}

	public String formatExtensions() {
		return "*.m3u;*.pls";
	}

	public Playlist read(final File file) {
		Playlist playlist = new Playlist();
		if (file.isFile() && file.exists()) {
			try {
				final PlayistBuilder builder = new PlayistBuilder();
				if (file.getName().toLowerCase().endsWith(".m3u")) {
					new MThreeUFileParser(builder).parse(file);
				} else if (file.getName().toLowerCase().endsWith(".pls")) {
					new PLSFileParser(builder).parse(file);	
				}
				playlist = builder.getPlaylist();
				
				int numEntries = 0;
				if (builder.getNumEntries() != null) {
					numEntries = builder.getNumEntries() - playlist.size();
				}
				if (numEntries > 0) {
					log.log(new Status(IStatus.WARNING, TinyAudioPlayerConstants.PLUGIN_ID, numEntries + " playlist entries are missing!"));
				}
			} catch (Exception e) {
				log.log(new Status(IStatus.ERROR, TinyAudioPlayerConstants.PLUGIN_ID, "Reading playlist failed!", e));
			}
		}
		return playlist;
	}
}
