package de.fips.plugin.tinyaudioplayer.view;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;
import org.junit.Test;

import de.fips.plugin.tinyaudioplayer.TinyAudioPlayer;

public class PlaylistViewKeyListenerTest {
	@Test
	public void whenInvoked_selectionChanged_shouldSelectTracksInPlaylist() throws Exception {
		// setup
		final TinyAudioPlayer player = mock(TinyAudioPlayer.class);
		final Event event = new Event();
		event.widget = mock(Widget.class);
		event.keyCode = SWT.DEL;
		final KeyEvent keyEvent = new KeyEvent(event);
		final PlaylistViewKeyListener listener = new PlaylistViewKeyListener(player);
		// run
		listener.keyPressed(keyEvent);
		// assert
		verify(player).removeSelected();
	}
}
