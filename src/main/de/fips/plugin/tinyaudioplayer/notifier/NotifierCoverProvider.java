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
package de.fips.plugin.tinyaudioplayer.notifier;

import static de.fips.plugin.tinyaudioplayer.TinyAudioPlayerConstants.COVER_DETECTION_PATTERN;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.regex.Matcher;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import de.fips.plugin.tinyaudioplayer.TinyAudioPlayerPlugin;

public class NotifierCoverProvider {
	public Image loadCoverFor(final URI location){
		Image thumbnail = null;
		File parent = null;
		try {
			final File file = new File(location);
			parent = file.getParentFile();
		} catch(IllegalArgumentException ignore) {
			// File(URI) preconditions did not hold
		}
		if (parent != null) {
			String[] coverNames = parent.list(new FilenameFilter() {
				@Override
				public boolean accept(File file, String s) {
					final Matcher matcher = COVER_DETECTION_PATTERN.matcher(s.toLowerCase());
					return matcher.matches();
				}
			});
			if (coverNames.length > 0) {
				try {
					final File coverFile = new File(parent, coverNames[0]).getCanonicalFile().getAbsoluteFile();
					thumbnail = TinyAudioPlayerPlugin.getDefaultImageRegistry().get(coverFile.getPath());
					if (thumbnail == null) {
						final URL imageURL = coverFile.toURI().toURL();
						final ImageDescriptor descriptor = ImageDescriptor.createFromURL(imageURL);
						thumbnail = descriptor.createImage();
						thumbnail = new Image(Display.getDefault(), thumbnail.getImageData().scaledTo(80, 80));
						TinyAudioPlayerPlugin.getDefaultImageRegistry().put(coverFile.getPath(), thumbnail);
					}
				} catch (IOException ignore) {
				}
			}
		}
		return thumbnail;
	}
}
