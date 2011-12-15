package de.fips.plugin.tinyaudioplayer.notifier;

import static org.mockito.Mockito.*;

import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

public class RunnableWithShellTest {

	@Test
	public void test_run_shellIsNull() throws Exception {
		// setup
		final Shell shell = null;
		final RunnableWithShell runnable = spy(new RunnableWithShellImpl(shell));
		// run
		runnable.run();
		// assert
		verify(runnable, never()).guardedRun(eq(shell));
	}

	@Test
	public void test_run_shellIsDisposed() throws Exception {
		// setup
		final Shell shell = mock(Shell.class);
		doReturn(true).when(shell).isDisposed();
		final RunnableWithShell runnable = spy(new RunnableWithShellImpl(shell));
		// run
		runnable.run();
		// assert
		verify(runnable, never()).guardedRun(eq(shell));
	}

	@Test
	public void test_run() throws Exception {
		// setup
		final Shell shell = mock(Shell.class);
		doReturn(false).when(shell).isDisposed();
		final RunnableWithShell runnable = spy(new RunnableWithShellImpl(shell));
		// run
		runnable.run();
		// assert
		verify(runnable).guardedRun(eq(shell));
	}

	private static class RunnableWithShellImpl extends RunnableWithShell {
		public RunnableWithShellImpl(Shell shell) {
			super(shell);
		}

		@Override
		protected void guardedRun(Shell shell) {
		}
	}
}
