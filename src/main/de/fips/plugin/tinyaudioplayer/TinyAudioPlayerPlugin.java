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
package de.fips.plugin.tinyaudioplayer;

import lombok.Getter;
import lombok.NoArgsConstructor;

import org.eclipse.core.runtime.ILog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

@NoArgsConstructor
public class TinyAudioPlayerPlugin extends AbstractUIPlugin {
	private static TinyAudioPlayerPlugin sharedInstance;
	
	@Getter
	private final TinyAudioPlayer player = new TinyAudioPlayer();

	public void start(final BundleContext context) throws Exception {
		super.start(context);
		sharedInstance = this;
	}

	public void stop(final BundleContext context) throws Exception {
		sharedInstance = null;
		super.stop(context);
	}

	public static TinyAudioPlayerPlugin getDefault() {
		return sharedInstance;
	}

	public static ILog getDefaultLog() {
		return getDefault().getLog();
	}
	
	public static IWorkbench getDefaultWorkbench() {
		return getDefault().getWorkbench();
	}
	
	public static ImageRegistry getDefaultImageRegistry() {
		return getDefault().getImageRegistry();
	}
	
	public static TinyAudioPlayer getDefaultPlayer() {
		return getDefault().getPlayer();
	}

	public static ImageDescriptor getImageDescriptor(final String path) {
		return imageDescriptorFromPlugin(TinyAudioPlayerConstants.PLUGIN_ID, path);
	}
}
