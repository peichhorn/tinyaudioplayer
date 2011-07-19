package de.fips.plugin.tinyaudioplayer.wizards.soundcloud;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
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
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		searchText = new Text(container, SWT.BORDER | SWT.SINGLE);
		searchText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (!getSearchText().isEmpty()) {
					setPageComplete(true);
				}
			}
		});
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		searchText.setLayoutData(gd);
		setControl(container);
		setPageComplete(false);
	}

	public String getSearchText() {
		return searchText.getText();
	}
}