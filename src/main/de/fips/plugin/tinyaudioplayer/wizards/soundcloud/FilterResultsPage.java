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

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.fips.plugin.tinyaudioplayer.TinyAudioPlayerPlugin;
import de.fips.plugin.tinyaudioplayer.audio.Playlist;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistItem;
import de.fips.plugin.tinyaudioplayer.http.EclipseProxyConfiguration;
import de.fips.plugin.tinyaudioplayer.http.IProxyConfiguration;
import de.fips.plugin.tinyaudioplayer.http.SoundCloudPlaylistProvider;
import de.fips.plugin.tinyaudioplayer.view.playlist.PlaylistContentProvider;
import de.fips.plugin.tinyaudioplayer.view.playlist.PlaylistItemLabelProvider;

public class FilterResultsPage extends WizardPage {
	private CheckboxTableViewer viewer;
	private Composite container;
	private final SoundCloudPlaylistProvider playlistProvider;
	private Thread soundCloudScanner;

	public FilterResultsPage() {
		this(new EclipseProxyConfiguration());
	}
	
	public FilterResultsPage(final IProxyConfiguration proxyConfiguration) {
		super("filter.results");
		setTitle("Filter Results");
		setDescription("Select the tracks you want to import.\nPlease note that it may take a few moments for the table to show results.");
		playlistProvider = new SoundCloudPlaylistProvider().proxyConfiguration(proxyConfiguration);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			updateTableViewer();
		} else {
			stopScanner();
		}
		setPageComplete(visible);
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		viewer = CheckboxTableViewer.newCheckList(container, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.setContentProvider(new PlaylistContentProvider());
		viewer.setLabelProvider(new PlaylistItemLabelProvider(null));
		GridData gd = new GridData(GridData.FILL_BOTH);
		viewer.getControl().setLayoutData(gd);

		setControl(container);
		setPageComplete(false);
	}

	public Playlist getPlaylist() {
		final Object input = viewer.getInput();
		if (input instanceof Playlist) {
			@SuppressWarnings("unchecked")
			final List<PlaylistItem> selectedTracks = (List<PlaylistItem>) (List<?>) Arrays.asList(viewer.getCheckedElements());
			final Playlist playlist = new Playlist();
			playlist.add(selectedTracks);
			return playlist;
		}
		return null;
	}

	private void updateTableViewer() {
		viewer.setInput(new Playlist());
		final IWizard wizard = getWizard();
		if (wizard instanceof SoundCloudWizard) {
			stopScanner();
			startScanner(((SoundCloudWizard) getWizard()).getSearchText());
		}
	}

	private void stopScanner() {
		if ((soundCloudScanner != null) && soundCloudScanner.isAlive()) {
			soundCloudScanner.interrupt();
		}
		soundCloudScanner = null;
	}
	
	private void startScanner(final String searchText) {
		soundCloudScanner = new Thread("Scan Soundcloud for: '" + searchText + "'") {
			@Override
			public void run() {
				try {
					final Object input = viewer.getInput();
					if (input instanceof Playlist) {
						final Playlist playlist = (Playlist) input;
						final int numberOfPages = playlistProvider.getNumberOfPagesFor(searchText);
						for (int currentPage = 1; currentPage <= numberOfPages; currentPage++) {
							if (isInterrupted()) break;
							playlist.add(playlistProvider.getPlaylistFor(searchText, currentPage));
							if (isInterrupted()) break;
						}
					}
				} catch (Exception e) {
					TinyAudioPlayerPlugin.logErr("", e);
				}
			}
		};
		soundCloudScanner.start();
	}
}