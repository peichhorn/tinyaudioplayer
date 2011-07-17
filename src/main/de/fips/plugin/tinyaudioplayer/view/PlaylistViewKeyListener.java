package de.fips.plugin.tinyaudioplayer.view;

import lombok.RequiredArgsConstructor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

import de.fips.plugin.tinyaudioplayer.TinyAudioPlayer;

@RequiredArgsConstructor
public class PlaylistViewKeyListener extends KeyAdapter {
	private final TinyAudioPlayer player;

	@Override
	public void keyPressed(KeyEvent e) {
		if ((e.keyCode == SWT.DEL) || (e.keyCode == SWT.BS)) {
			player.removeSelected();
		}
	}
}