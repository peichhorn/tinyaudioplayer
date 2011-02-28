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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Some constants used by the {@link NotifierDialog}.
 * 
 * @author Philipp Eichhorn
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public final class NotifierConstants {
	public static final String COLOR_ID = "de.fips.plugin.tinyaudioplayer.notifier.color"; //$NON-NLS-1$
	public static final String TITLE_COLOR_ID = "de.fips.plugin.tinyaudioplayer.notifier.titlecolor"; //$NON-NLS-1$
	public static final String BORDER_COLOR_ID = "de.fips.plugin.tinyaudioplayer.notifier.bordercolor"; //$NON-NLS-1$
	public static final String GRADIENT_COLOR_1_ID = "de.fips.plugin.tinyaudioplayer.notifier.gradientcolor1"; //$NON-NLS-1$
	public static final String GRADIENT_COLOR_2_ID = "de.fips.plugin.tinyaudioplayer.notifier.gradientcolor2"; //$NON-NLS-1$
	public static final String TITLE_FONT_ID = "de.fips.plugin.tinyaudioplayer.notifier.titlefont"; //$NON-NLS-1$
	public static final String TEXT_FONT_ID = "de.fips.plugin.tinyaudioplayer.notifier.textfont"; //$NON-NLS-1$
	
	public static final int DISPLAY_TIME = 4500;
	public static final int FADE_TIMER = 50;
	public static final int FADE_IN_STEP = 30;
	public static final int FADE_OUT_STEP = 8;
	public static final int FINAL_ALPHA = 225;
}
