package de.fips.plugin.tinyaudioplayer.notifier;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.themes.IThemeManager;

import de.fips.plugin.tinyaudioplayer.TinyAudioPlayerPlugin;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public final class NotifierTheming {

	public static void theme(final Control control, final int what, final String themeVariable) {
		final IThemeManager themeManager = TinyAudioPlayerPlugin.getDefaultWorkbench().getThemeManager();
		switch (what) {
		case SWT.FOREGROUND:
			control.setForeground(themeManager.getCurrentTheme().getColorRegistry().get(themeVariable));
			break;
		case SWT.BACKGROUND:
			control.setBackground(themeManager.getCurrentTheme().getColorRegistry().get(themeVariable));
			break;
		default:
			control.setFont(themeManager.getCurrentTheme().getFontRegistry().get(themeVariable));
		}
	}

	public static void theme(final GC gc, final int what, final String themeVariable) {
		final IThemeManager themeManager = TinyAudioPlayerPlugin.getDefaultWorkbench().getThemeManager();
		switch (what) {
		case SWT.FOREGROUND:
			gc.setForeground(themeManager.getCurrentTheme().getColorRegistry().get(themeVariable));
			break;
		case SWT.BACKGROUND:
			gc.setBackground(themeManager.getCurrentTheme().getColorRegistry().get(themeVariable));
			break;
		default:
			gc.setFont(themeManager.getCurrentTheme().getFontRegistry().get(themeVariable));
		}
	}
}
