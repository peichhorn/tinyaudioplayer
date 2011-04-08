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

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.commands.ICommandImageService;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

import de.fips.plugin.tinyaudioplayer.TinyAudioPlayer;
import de.fips.plugin.tinyaudioplayer.TinyAudioPlayerPlugin;

public class MuteHandler extends AbstractHandler implements IElementUpdater {
	private String commandId;

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final TinyAudioPlayer player = TinyAudioPlayerPlugin.getDefaultPlayer();
		player.setMute(!player.isMute());
		final ICommandService commandService = (ICommandService) TinyAudioPlayerPlugin.getDefaultWorkbench().getService(ICommandService.class);
		if (commandService != null) {
			commandId = event.getCommand().getId();
			commandService.refreshElements(commandId, null);
		}
		return null;
	}

	@Override
	public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
		final ICommandImageService commandImageService = (ICommandImageService) TinyAudioPlayerPlugin.getDefaultWorkbench().getService(ICommandImageService.class);
		if ((commandImageService != null) && (commandId != null)) {
			final TinyAudioPlayer player = TinyAudioPlayerPlugin.getDefaultPlayer();
			final int type = player.isMute() ? ICommandImageService.TYPE_DISABLED : ICommandImageService.TYPE_DEFAULT;
			element.setIcon(commandImageService.getImageDescriptor(commandId, type));
		}
	}
}
