package de.fips.plugin.tinyaudioplayer.handler;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.core.commands.ExecutionEvent;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import de.fips.plugin.tinyaudioplayer.TinyAudioPlayer;
import de.fips.plugin.tinyaudioplayer.TinyAudioPlayerPlugin;

public class EjectHandlerTest {
	@Test
	public void whenInvoked_execute_shouldCallPlayerEject() throws Exception {
		// setup
		final TinyAudioPlayer player = mock(TinyAudioPlayer.class);
		final ExecutionEvent event = new ExecutionEvent();
		final BundleContext context = mock(BundleContext.class);
		doReturn(mock(Bundle.class)).when(context).getBundle();
		new TinyAudioPlayerPlugin(player).start(context);
		// run
		new EjectHandler().execute(event);
		// assert
		verify(player).eject();
	}
}
