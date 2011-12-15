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
package de.fips.plugin.tinyaudioplayer.handler;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.IWorkbench;
import org.junit.Test;
import org.mockito.InOrder;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import de.fips.plugin.tinyaudioplayer.TinyAudioPlayer;
import de.fips.plugin.tinyaudioplayer.TinyAudioPlayerPlugin;

public class MuteHandlerTest {
	@Test
	public void whenInvoked_execute_shouldTogglePlayerMute() throws Exception {
		// setup
		final TinyAudioPlayer player = mock(TinyAudioPlayer.class);
		final ExecutionEvent event = new ExecutionEvent();
		final BundleContext context = mock(BundleContext.class);
		doReturn(mock(Bundle.class)).when(context).getBundle();
		final TinyAudioPlayerPlugin plugin = spy(new TinyAudioPlayerPlugin(player));
		plugin.start(context);
		doReturn(mock(IWorkbench.class)).when(plugin).getWorkbench();
		doReturn(true).when(player).isMute();
		// run
		new MuteHandler().execute(event);
		// assert
		InOrder inOrder = inOrder(player);
		inOrder.verify(player).isMute();
		inOrder.verify(player).setMute(false);
	}
}
