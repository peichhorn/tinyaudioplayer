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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.fips.plugin.tinyaudioplayer.TinyAudioPlayerConstants;
import de.fips.plugin.tinyaudioplayer.TinyAudioPlayerPlugin;
import de.fips.plugin.tinyaudioplayer.audio.Playlist;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistItem;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistItemTag;

public class MThreeUWriter extends AbstractWriter<Playlist>{

	public String formatName() {
		return "Playlist File";
	}

	public String formatExtensions() {
		return "*.m3u";
	}

	public void write(final File file, final Playlist playlist) {
		try {
			final BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write("#EXTM3U");
			bw.newLine();
			for (final PlaylistItem pli : playlist) {
				bw.write("#EXTINF:" + getExtInfFromPlaylistItem(pli));
				bw.newLine();
				bw.write(getRelativePath(new File(pli.getLocation()), file));
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			TinyAudioPlayerPlugin.log(new Status(IStatus.ERROR, TinyAudioPlayerConstants.PLUGIN_ID, "Creating M3U failed!", e));
		}
	}

	private String getExtInfFromPlaylistItem(final PlaylistItem pli) {
		final StringBuilder extInf = new StringBuilder();
		extInf.append(pli.getSeconds()).append(",").append(pli.getName());
		final PlaylistItemTag infoTag = pli.getInfoTag();
		if (infoTag != null) {
			extInf.append(pli.getLength());
			if ((infoTag.getTitle() != null)) {
				if (infoTag.getArtist() != null) extInf.append(",").append(infoTag.getArtist());
				extInf.append(",").append(infoTag.getTitle());
			}
		}
		return extInf.toString();
	}
	
	public static String getRelativePath(final File file, File relativeTo) {
		String path = "";

		if (relativeTo.isFile()) {
			relativeTo = relativeTo.getParentFile();
		}

		List<String> fileList = getDelimitedStringAsList(file.getAbsolutePath(), File.separator);
		List<String> relativeList = getDelimitedStringAsList(relativeTo.getAbsolutePath(), File.separator);

		int size = fileList.size();
		int relativeSize = relativeList.size();
		int count = 0;

		while ((count < size) && (count < relativeSize)) {
			if (fileList.get(count).equalsIgnoreCase(relativeList.get(count))) {
				count++;
			} else {
				break;
			}
		}

		for (int i = count; i < relativeSize; i++) {
			path += ".." + File.separator;
		}

		for (int i = count; i < size; i++) {
			path += fileList.get(i) + File.separator;
		}

		if (path.indexOf(File.separator) > -1) {
			path = path.substring(0, path.lastIndexOf(File.separator));
		}
		return path;
	}

	private static List<String> getDelimitedStringAsList(final String str, final String delimiter) {
		final List<String> resultList = new ArrayList<String>();
		final StringTokenizer st = new StringTokenizer(str, delimiter);
		while (st.hasMoreTokens()) {
			resultList.add(st.nextToken());
		}
		return resultList;
	}
}
