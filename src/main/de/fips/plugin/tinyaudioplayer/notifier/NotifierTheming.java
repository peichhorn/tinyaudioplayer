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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.themes.IThemeManager;

import de.fips.plugin.tinyaudioplayer.TinyAudioPlayerPlugin;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public final class NotifierTheming {

	public static void theme(final Control control, final int what, final String themeVariable) {
		final IThemeManager themeManager = TinyAudioPlayerPlugin.getDefaultWorkbench().getThemeManager();
		switch (what) {
		case SWT.FOREGROUND:
			control.setForeground(themeManager.getCurrentTheme().getColorRegistry().get(themeVariable));
			break;
		case SWT.BACKGROUND:
			control.setBackground(themeManager.getCurrentTheme().getColorRegistry().get(themeVariable));
			break;
		default:
			control.setFont(themeManager.getCurrentTheme().getFontRegistry().get(themeVariable));
		}
	}

	public static void theme(final GC gc, final int what, final String themeVariable) {
		final IThemeManager themeManager = TinyAudioPlayerPlugin.getDefaultWorkbench().getThemeManager();
		switch (what) {
		case SWT.FOREGROUND:
			gc.setForeground(themeManager.getCurrentTheme().getColorRegistry().get(themeVariable));
			break;
		case SWT.BACKGROUND:
			gc.setBackground(themeManager.getCurrentTheme().getColorRegistry().get(themeVariable));
			break;
		default:
			gc.setFont(themeManager.getCurrentTheme().getFontRegistry().get(themeVariable));
		}
	}
}
