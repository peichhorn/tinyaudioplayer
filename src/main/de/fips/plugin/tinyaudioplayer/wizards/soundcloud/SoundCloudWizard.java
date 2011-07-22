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
package de.fips.plugin.tinyaudioplayer.wizards.soundcloud;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import de.fips.plugin.tinyaudioplayer.TinyAudioPlayerPlugin;
import de.fips.plugin.tinyaudioplayer.audio.Playlist;

public class SoundCloudWizard extends Wizard implements IImportWizard {
	private SearchPage searchPage;
	private FilterResultsPage filterResultsPage;

	public SoundCloudWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		searchPage = new SearchPage();
		filterResultsPage = new FilterResultsPage();
		addPage(searchPage);
		addPage(filterResultsPage);
	}

	@Override
	public boolean performFinish() {
		final Playlist newPlaylist = getPlaylist();
		if (newPlaylist != null) {
			TinyAudioPlayerPlugin.getDefaultPlayer().getPlaylist().add(newPlaylist);
		}
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	String getSearchText() {
		return searchPage.getSearchText();
	}

	Playlist getPlaylist() {
		return filterResultsPage.getPlaylist();
	}
}