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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import de.fips.plugin.tinyaudioplayer.audio.Playlist;
import de.fips.plugin.tinyaudioplayer.audio.PlaylistItem;
import de.fips.plugin.tinyaudioplayer.http.EclipseProxyConfiguration;
import de.fips.plugin.tinyaudioplayer.http.IProxyConfiguration;
import de.fips.plugin.tinyaudioplayer.http.SoundCloudPlaylistProvider;
import de.fips.plugin.tinyaudioplayer.view.playlist.PlaylistContentProvider;
import de.fips.plugin.tinyaudioplayer.view.playlist.PlaylistItemLabelProvider;

public class FilterResultsPage extends WizardPage {
	private CheckboxTableViewer viewer;
	private int numberOfPages = 1;
	private int currentPage = 1;
	private Composite pageNavigation;
	private Button previousPageButton;
	private Button nextPageButton;
	private Label pageLabel;
	private Composite container;
	private final SoundCloudPlaylistProvider playlistProvider;

	public FilterResultsPage() {
		this(new EclipseProxyConfiguration());
	}
	
	public FilterResultsPage(final IProxyConfiguration proxyConfiguration) {
		super("filter.results");
		setTitle("Filter Results");
		setDescription("Select the tracks you want to import...");
		playlistProvider = new SoundCloudPlaylistProvider().proxyConfiguration(proxyConfiguration);
	}

	private void updateTableViewer(int page) {
		currentPage = page;
		updatePageLabel();
		final IWizard wizard = getWizard();
		if (wizard instanceof SoundCloudWizard) {
			final String searchText = ((SoundCloudWizard) getWizard()).getSearchText();
			final Playlist playlist = playlistProvider.getPlaylistFor(searchText, currentPage);
			viewer.setInput(playlist);
			viewer.setAllChecked(true);
			viewer.refresh();
		}
	}

	private void updatePageLabel() {
		pageLabel.setText(currentPage + "/" + numberOfPages);
		pageNavigation.layout(true);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			final String searchText = ((SoundCloudWizard) getWizard()).getSearchText();
			numberOfPages = playlistProvider.getNumberOfPagesFor(searchText);
			updateTableViewer(1);
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
		
		pageNavigation = new Composite(container, SWT.NONE);
		layout = new GridLayout(3, false);
		pageNavigation.setLayout(layout);
		gd = new GridData();
		gd.horizontalAlignment = SWT.CENTER;
		pageNavigation.setLayoutData(gd);
		previousPageButton = new Button(pageNavigation, SWT.PUSH);
		previousPageButton.setText("<");
		previousPageButton.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				if (currentPage > 1) {
					updateTableViewer(currentPage - 1);
				}
			}
		});
		gd = new GridData();
		previousPageButton.setLayoutData(gd);
		pageLabel = new Label(pageNavigation, SWT.NONE);
		pageLabel.setText("1/1");
		gd = new GridData();
		pageLabel.setLayoutData(gd);
		nextPageButton = new Button(pageNavigation, SWT.PUSH);
		nextPageButton.setText(">");
		gd = new GridData();
		nextPageButton.setLayoutData(gd);
		nextPageButton.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				if (currentPage < numberOfPages) {
					updateTableViewer(currentPage + 1);
				}
			}
		});
		setControl(container);
		setPageComplete(false);
	}

	public Playlist getPlaylist() {
		final Object input = viewer.getInput();
		if (input instanceof Playlist) {
			final Playlist playlist = (Playlist) input;
			@SuppressWarnings("unchecked")
			final List<PlaylistItem> selectedTracks = (List<PlaylistItem>) (List<?>) Arrays.asList(viewer.getCheckedElements());
			playlist.selectTracks(selectedTracks);
			playlist.invertSelection();
			playlist.removeSelected();
			return playlist;
		}
		return null;
	}
}