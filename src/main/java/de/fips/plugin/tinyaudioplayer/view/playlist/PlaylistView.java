/*
 * Copyright � 2011-2012 Philipp Eichhorn.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.fips.plugin.tinyaudioplayer.view.playlist;

import lombok.val;

import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.fips.plugin.tinyaudioplayer.TinyAudioPlayerPlugin;

public class PlaylistView extends ViewPart {
	private TableViewer viewer;

	@Override
	public void createPartControl(final Composite parent) {
		val player = TinyAudioPlayerPlugin.getDefaultPlayer();
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.addDoubleClickListener(new PlaylistDoubleClickListener(player));
		viewer.addSelectionChangedListener(new PlaylistSelectionChangedListener(player));
		viewer.setContentProvider(new PlaylistContentProvider());
		viewer.setLabelProvider(new PlaylistItemLabelProvider(player));
		viewer.getTable().addKeyListener(new PlaylistKeyListener(player));
		viewer.setInput(player.getPlaylist());
		ColumnViewerToolTipSupport.enableFor(viewer, ToolTip.RECREATE);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
