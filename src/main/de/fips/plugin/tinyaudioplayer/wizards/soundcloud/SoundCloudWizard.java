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