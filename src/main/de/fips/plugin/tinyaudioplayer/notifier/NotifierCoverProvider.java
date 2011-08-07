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
