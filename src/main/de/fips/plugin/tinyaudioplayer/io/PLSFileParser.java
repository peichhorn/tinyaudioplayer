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
package de.fips.plugin.tinyaudioplayer.io;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import lombok.Cleanup;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PLSFileParser {
	private final IPlaylistFileVisitor visitor;
	
	public void parse(final File file) throws IOException {
		visitor.visitBegin(file);
		@Cleanup final TextLines lines = TextLines.of(file).ignoringEmptyLines();
		for (final String line : lines) {
			if (line.startsWith("[")) {
				visitor.visitComment(line);
			} else if ((line.toLowerCase().startsWith("file"))) {
				final StringTokenizer st = new StringTokenizer(line, "=");
				st.nextToken();
				final String fileName = st.nextToken().trim();
				File f = new File(file.getParentFile(), fileName);
				if (!f.exists()) {
					f = new File(fileName);
				}
				visitor.visitFile(f);
			} else if ((line.toLowerCase().startsWith("title"))) {
				final StringTokenizer st = new StringTokenizer(line, "=");
				st.nextToken();
				visitor.visitTitle(st.nextToken().trim());
			} else if ((line.toLowerCase().startsWith("length"))) {
				final StringTokenizer st = new StringTokenizer(line, "=");
				st.nextToken();
				visitor.visitLength(Long.valueOf(st.nextToken().trim()));
			} else if ((line.toLowerCase().startsWith("numberofentries"))) {
				final StringTokenizer st = new StringTokenizer(line, "=");
				st.nextToken();
				visitor.visitNumberOfEntries(new Integer(st.nextToken().trim()));
			}
		}
		visitor.visitEnd(file);
	}
}
