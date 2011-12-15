package de.fips.plugin.tinyaudioplayer.notifier;

import static org.mockito.Mockito.*;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.themes.ITheme;
import org.junit.Test;

public class NotifierThemingTest {

	@Test
	public void test_theme_control_foreground() throws Exception {
		// setup
		final String themeVariable = "a color";
		final Color color = new Color(null, 0, 0, 0);
		final ColorRegistry colorRegistry = mock(ColorRegistry.class);
		doReturn(color).when(colorRegistry).get(eq(themeVariable));
		final ITheme theme = mock(ITheme.class);
		doReturn(colorRegistry).when(theme).getColorRegistry();
		final NotifierTheming theming = new NotifierTheming(theme);
		final Control control = mock(Control.class);
		// run
		theming.theme(control, SWT.FOREGROUND, themeVariable);
		// assert
		verify(control).setForeground(eq(color));
		verifyNoMoreInteractions(control);
	}

	@Test
	public void test_theme_control_background() throws Exception {
		// setup
		final String themeVariable = "a color";
		final Color color = new Color(null, 0, 0, 0);
		final ColorRegistry colorRegistry = mock(ColorRegistry.class);
		doReturn(color).when(colorRegistry).get(eq(themeVariable));
		final ITheme theme = mock(ITheme.class);
		doReturn(colorRegistry).when(theme).getColorRegistry();
		final NotifierTheming theming = new NotifierTheming(theme);
		final Control control = mock(Control.class);
		// run
		theming.theme(control, SWT.BACKGROUND, themeVariable);
		// assert
		verify(control).setBackground(eq(color));
		verifyNoMoreInteractions(control);
	}

	@Test
	public void test_theme_control_font() throws Exception {
		// setup
		final String themeVariable = "a font";
		final Font font = new Font(null, "", 0, 0);
		final FontRegistry fontRegistry = mock(FontRegistry.class);
		doReturn(font).when(fontRegistry).get(eq(themeVariable));
		final ITheme theme = mock(ITheme.class);
		doReturn(fontRegistry).when(theme).getFontRegistry();
		final NotifierTheming theming = new NotifierTheming(theme);
		final Control control = mock(Control.class);
		// run
		theming.theme(control, SWT.NONE, themeVariable);
		// assert
		verify(control).setFont(eq(font));
		verifyNoMoreInteractions(control);
	}
}
