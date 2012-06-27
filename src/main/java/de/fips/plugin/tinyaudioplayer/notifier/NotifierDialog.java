/*
 * Copyright � 2011-2012 Philipp Eichhorn.
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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import de.fips.plugin.tinyaudioplayer.TinyAudioPlayerPlugin;
import de.fips.plugin.tinyaudioplayer.preference.PreferencesConstants;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotifierDialog {
	private static List<Shell> activeShells = new ArrayList<Shell>();
	private static Image oldImage;

	private final NotifierTheming theming = new NotifierTheming();
	private final NotifierCoverProvider coverProvider = new NotifierCoverProvider();

	public static void notifyAsync(final String title, final String message, final URI location) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				NotifierDialog.notify(title, message, location);
			}
		});
	}

	public static void notify(final String title, final String message, final URI location) {
		new NotifierDialog().show(title, message, location);
	}

	private void show(final String title, final String message, final URI location) {
		val shell = new Shell(Display.getDefault().getActiveShell(), SWT.NO_FOCUS | SWT.NO_TRIM);
		shell.setLayout(new FillLayout());
		shell.setBackgroundMode(SWT.INHERIT_DEFAULT);
		theming.theme(shell, SWT.FOREGROUND, NotifierConstants.COLOR_ID);
		registerDisposeListener(shell);
		registerResizeListener(shell);

		val inner = createInnerComposite(shell);
		createImageLabel(inner, location);
		Composite right = createRightComposite(inner);
		createTitleLabel(right, title);
		createTextLabel(right, message);

		shell.pack();

		if ((Display.getDefault().getActiveShell() == null) || (Display.getDefault().getActiveShell().getMonitor() == null)) {
			return;
		}

		repositionShell(shell);
		removeUnwantedShells();
		rearrangeActiveShells(shell.getBounds().height);
		activeShells.add(shell);
		shell.setAlpha(0);
		shell.setVisible(true);
		fadeIn(shell);
	}

	private void removeUnwantedShells() {
		val allowedActiveShells = getActiveNotifications() - 1;
		for (val s : new ArrayList<Shell>(activeShells)) {
			if (activeShells.size() <= allowedActiveShells) {
				break;
			}
			s.dispose();
			activeShells.remove(s);
		}
	}

	private int getActiveNotifications() {
		return Math.max(1, TinyAudioPlayerPlugin.getDefault().getPreferenceStore().getInt(PreferencesConstants.ACTIVE_NOTIFICATIONS));
	}

	private void repositionShell(final Shell shell) {
		val clientArea = Display.getDefault().getActiveShell().getMonitor().getClientArea();
		val startX = clientArea.x + clientArea.width - shell.getBounds().width - 2;
		val startY = clientArea.y + clientArea.height - shell.getBounds().height - 2;
		shell.setLocation(startX, startY);
	}

	private void rearrangeActiveShells(final int newShellHeight) {
		val modifiable = new ArrayList<Shell>(activeShells);
		Collections.reverse(modifiable);
		for (val s : modifiable) {
			val curLoc = s.getLocation();
			s.setLocation(curLoc.x, curLoc.y - newShellHeight);
			if (curLoc.y - newShellHeight < 0) {
				activeShells.remove(s);
				s.dispose();
			}
		}
	}

	private void registerDisposeListener(final Shell shell) {
		shell.addListener(SWT.Dispose, new Listener() {
			@Override
			public void handleEvent(Event event) {
				activeShells.remove(shell);
			}
		});
	}

	private void registerResizeListener(final Shell shell) {
		shell.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event e) {
				try {
					if (oldImage != null) {
						oldImage.dispose();
					}
					oldImage = updateBackgroundImage(shell);
				} catch (Exception ignore) {
				}
			}
		});
	}

	private Image updateBackgroundImage(final Shell shell) {
		val rect = shell.getClientArea();
		val newImage = new Image(Display.getDefault(), Math.max(1, rect.width), rect.height);
		val gc = new GC(newImage);
		theming.theme(gc, SWT.FOREGROUND, NotifierConstants.GRADIENT_COLOR_1_ID);
		theming.theme(gc, SWT.BACKGROUND, NotifierConstants.GRADIENT_COLOR_2_ID);
		gc.fillGradientRectangle(rect.x, rect.y, rect.width, rect.height, true);
		gc.setLineWidth(2);
		theming.theme(gc, SWT.FOREGROUND, NotifierConstants.BORDER_COLOR_ID);
		gc.drawRectangle(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2);
		gc.dispose();
		shell.setBackgroundImage(newImage);
		return newImage;
	}

	private Composite createInnerComposite(final Composite parent) {
		val inner = new Composite(parent, SWT.NONE);
		val gl = new GridLayout(2, false);
		gl.marginLeft = 2;
		gl.marginTop = 2;
		gl.marginRight = 2;
		gl.marginBottom = 2;
		inner.setLayout(gl);
		return inner;
	}

	private Composite createRightComposite(final Composite parent) {
		val right = new Composite(parent, SWT.NONE);
		right.setLayout(new GridLayout(1, false));
		return right;
	}

	private void createImageLabel(final Composite parent, final URI location) {
		val imgLabel = new CLabel(parent, SWT.NONE);
		imgLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_BEGINNING));
		val image = coverProvider.loadCoverFor(location);
		if (image != null) {
			imgLabel.setImage(image);
		}
	}

	private void createTitleLabel(final Composite parent, final String title) {
		val titleLabel = new CLabel(parent, SWT.NONE);
		titleLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER));
		titleLabel.setText(title);
		theming.theme(titleLabel, SWT.FOREGROUND, NotifierConstants.TITLE_COLOR_ID);
		theming.theme(titleLabel, SWT.NONE, NotifierConstants.TITLE_FONT_ID);
	}

	private void createTextLabel(final Composite parent, final String message) {
		val text = new Label(parent, SWT.WRAP);
		theming.theme(text, SWT.NONE, NotifierConstants.TEXT_FONT_ID);
		val gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		text.setLayoutData(gd);
		theming.theme(text, SWT.FOREGROUND, NotifierConstants.COLOR_ID);
		text.setText(message);
	}

	private void fadeIn(final Shell shell) {
		Display.getDefault().timerExec(NotifierConstants.FADE_TIMER, new RunnableWithShell(shell) {
			@Override
			public void guardedRun(Shell shell) {
				val cur = shell.getAlpha() + NotifierConstants.FADE_IN_STEP;
				if (cur > NotifierConstants.FINAL_ALPHA) {
					shell.setAlpha(NotifierConstants.FINAL_ALPHA);
					startTimer(shell);
					return;
				}
				shell.setAlpha(cur);
				Display.getDefault().timerExec(NotifierConstants.FADE_TIMER, this);
			}
		});
	}

	private void startTimer(final Shell shell) {
		Display.getDefault().timerExec(NotifierConstants.DISPLAY_TIME, new RunnableWithShell(shell) {
			@Override
			public void guardedRun(Shell shell) {
				fadeOut(shell);
			}
		});
	}

	private void fadeOut(final Shell shell) {
		Display.getDefault().timerExec(NotifierConstants.FADE_TIMER, new RunnableWithShell(shell) {
			@Override
			public void guardedRun(Shell shell) {
				val cur = shell.getAlpha() - NotifierConstants.FADE_OUT_STEP;
				if (cur <= 0) {
					shell.setAlpha(0);
					if (oldImage != null) {
						oldImage.dispose();
					}
					shell.dispose();
					activeShells.remove(shell);
					return;
				}
				shell.setAlpha(cur);
				Display.getDefault().timerExec(NotifierConstants.FADE_TIMER, this);
			}
		});
	}
}
