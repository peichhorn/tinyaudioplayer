package de.fips.plugin.tinyaudioplayer.wizards.soundcloud;

import org.eclipse.jface.wizard.Wizard;

import de.fips.plugin.tinyaudioplayer.TinyAudioPlayerPlugin;
import de.fips.plugin.tinyaudioplayer.audio.Playlist;

public class SoundCloudWizard extends Wizard {
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

	String getSearchText() {
		return searchPage.getSearchText();
	}

	Playlist getPlaylist() {
		return filterResultsPage.getPlaylist();
	}
}