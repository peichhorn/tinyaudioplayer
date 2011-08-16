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
package de.fips.plugin.tinyaudioplayer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Entrypoint of the TinyAudioplayer Plugin.
 *
 * @author Philipp Eichhorn
 */
@RequiredArgsConstructor
public class TinyAudioPlayerPlugin extends AbstractUIPlugin {
	private static TinyAudioPlayerPlugin sharedInstance;

	@Getter
	private final TinyAudioPlayer player;

	public TinyAudioPlayerPlugin() {
		this(new TinyAudioPlayer());
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		sharedInstance = this;
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		sharedInstance = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance of this plugin.
	 */
	public static TinyAudioPlayerPlugin getDefault() {
		return sharedInstance;
	}

	private static void log(final IStatus status) {
		getDefault().getLog().log(status);
	}

	/**
	 * Logs a warning.
	 */
	public static void logWarn(final String message, final Object... args) {
		TinyAudioPlayerPlugin.log(new Status(IStatus.WARNING, TinyAudioPlayerConstants.PLUGIN_ID, String.format(message, args)));
	}

	/**
	 * Logs an error.
	 */
	public static void logErr(final String message, final Throwable throwable) {
		TinyAudioPlayerPlugin.log(new Status(IStatus.ERROR, TinyAudioPlayerConstants.PLUGIN_ID, message, throwable));
	}

	/**
	 * Logs an error.
	 */
	public static void logErr(final String message, final Object... args) {
		TinyAudioPlayerPlugin.log(new Status(IStatus.ERROR, TinyAudioPlayerConstants.PLUGIN_ID, String.format(message, args)));
	}

	/**
	 * Returns the {@link IWorkbench} of this plugins shared instance.
	 */
	public static IWorkbench getDefaultWorkbench() {
		return getDefault().getWorkbench();
	}

	/**
	 * Returns the {@link ImageRegistry} of this plugins shared instance.
	 */
	public static ImageRegistry getDefaultImageRegistry() {
		return getDefault().getImageRegistry();
	}

	/**
	 * Returns the {@link TinyAudioPlayer} of this plugins shared instance.
	 */
	public static TinyAudioPlayer getDefaultPlayer() {
		return getDefault().getPlayer();
	}

	/**
	 * Creates and returns a new image descriptor for an image file located within this plug-in.
	 */
	public static ImageDescriptor getImageDescriptor(final String path) {
		return imageDescriptorFromPlugin(TinyAudioPlayerConstants.PLUGIN_ID, path);
	}
}
