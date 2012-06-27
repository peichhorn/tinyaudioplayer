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
package de.fips.plugin.tinyaudioplayer.wizards.soundcloud;

import lombok.val;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class SearchPage extends WizardPage {
	private Text searchText;
	private Composite container;

	public SearchPage() {
		super("search.soundcloud");
		setTitle("Search SoundCloud");
		setDescription("Search SoundCloud...");
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			searchText.setText("");
		}
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		val layout = new GridLayout();
		container.setLayout(layout);
		searchText = new Text(container, SWT.BORDER | SWT.SINGLE);
		searchText.setData("org.eclipse.swtbot.widget.key", "searchText");
		searchText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (!getSearchText().isEmpty()) {
					setPageComplete(true);
				}
			}
		});
		searchText.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				e.doit = e.text.matches("[a-zA-Z0-9 ]*");
			}
		});
		val gd = new GridData(GridData.FILL_HORIZONTAL);
		searchText.setLayoutData(gd);
		setControl(container);
		setPageComplete(false);
	}

	public String getSearchText() {
		return searchText.getText();
	}
}