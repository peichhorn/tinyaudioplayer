/*
 * Copyright © 2011-2012 Philipp Eichhorn.
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

import lombok.val;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Workaround to be able to test code that uses the {@link FileDialog}.
 */
public class FileChooser {
	public static boolean IS_IN_TEST_MODE = false;

	public final FileDialog dialog;

	public FileChooser(final Shell parent, int style) {
		dialog = new FileDialog(parent, style);
	}

	public void setFilterNames(final String... names) {
		dialog.setFilterNames(names);
	}

	public void setFilterExtensions(final String... extensions) {
		dialog.setFilterExtensions(extensions);
	}

	public String open() {
		if (IS_IN_TEST_MODE) {
			val testDialog = new InputDialog(dialog.getParent(), "FileChooser", "Please enter the file path", "", null);
			return testDialog.open() != 0 ? null : testDialog.getValue();
		} else {
			return dialog.open();
		}
	}
}
