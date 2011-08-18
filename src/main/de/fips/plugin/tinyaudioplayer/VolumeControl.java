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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;
import org.fest.util.VisibleForTesting;

/**
 * Simple Volume Control that allows to modify the
 * volume of the {@link TinyAudioPlayerPlugin}.
 *
 * @see TinyAudioPlayer#setVolume(float)
 * @author Philipp Eichhorn
 */
public class VolumeControl extends WorkbenchWindowControlContribution {
	private float volume = 1.0f;
	
	private final Image baseVolumeImage;
	private final Image volumeImage;
	
	public VolumeControl() {
		this(new ImageLocator());
	}
	
	@VisibleForTesting VolumeControl(final IImageLocator imageLocator) {
		super();
		baseVolumeImage = imageLocator.getImage("icons/16px-volume-base.png");
		volumeImage = imageLocator.getImage("icons/16px-volume.png");
	}

	@Override
	protected Control createControl(final Composite parent) {
		final Composite volumeControl = new Composite(parent, SWT.NONE);
		volumeControl.setData("org.eclipse.swtbot.widget.key", "volumeControl");
		volumeControl.setBackgroundMode(SWT.INHERIT_DEFAULT);
		volumeControl.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(final PaintEvent e) {
				e.gc.drawImage(baseVolumeImage, 0, 0);
				e.gc.setClipping(0, 0, (int) (32 * volume), 16);
				e.gc.drawImage(volumeImage, 0, 0);
				e.gc.dispose();
			}
		});
		final Listener listener = new Listener() {
			private boolean mouseDown;
			@Override
			public void handleEvent(final Event event) {
				switch (event.type) {
				case SWT.MouseDown:
					mouseDown = true;
				case SWT.MouseMove: {
					updateVolume(event);
					break;
				}
				case SWT.MouseUp:
				case SWT.MouseEnter:
				case SWT.MouseExit: {
					mouseDown = false;
					break;
				}
				}
			}

			private void updateVolume(final Event event) {
				if (mouseDown) {
					final Rectangle rect = volumeControl.getBounds();
					volume = ((float) event.x / (float) rect.width * 2.0f);
					TinyAudioPlayerPlugin.getDefaultPlayer().setVolume(volume);
					volumeControl.redraw();
				}
			}
		};
		volumeControl.addListener(SWT.MouseDown, listener);
		volumeControl.addListener(SWT.MouseMove, listener);
		volumeControl.addListener(SWT.MouseUp, listener);
		volumeControl.addListener(SWT.MouseEnter, listener);
		volumeControl.addListener(SWT.MouseExit, listener);
		volumeControl.setSize(64, 16);
		return volumeControl;
	}
	
	@Override
	public void dispose() {
		baseVolumeImage.dispose();
		volumeImage.dispose();
		super.dispose();
	}
	
	@VisibleForTesting static interface IImageLocator {
		public Image getImage(String imagePath);
	}
	
	private static class ImageLocator implements IImageLocator {
		@Override
		public Image getImage(final String imagePath) {
			return TinyAudioPlayerPlugin.getImageDescriptor(imagePath).createImage();
		}
	}
}
