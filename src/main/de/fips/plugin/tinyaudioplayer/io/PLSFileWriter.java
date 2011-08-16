/*
 * Copyright © 2011 Philipp Eichhorn.
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
package de.fips.plugin.tinyaudioplayer.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;

public class PLSFileWriter implements IPlaylistFileVisitor {
	private BufferedWriter bw;
	private File file;
	private URI location;
	private Long length;
	private String title;
	private int trackCounter;

	@Override
	public void visitBegin(File file) throws IOException {
		if (bw != null) {
			bw.close();
		}
		trackCounter = 0;
		this.file = file;
		bw = new BufferedWriter(new FileWriter(this.file));
		bw.write("[playlist]");
		bw.newLine();
		bw.newLine();
	}

	@Override
	public void visitComment(String comment) throws IOException {
		checkIsOpen();
		bw.write("[");
		bw.write(comment);
		bw.write("]");
		bw.newLine();
	}

	@Override
	public void visitEntryBegin() throws IOException {
		checkIsOpen();
		location = null;
		length = 0L;
		title = "";
		trackCounter++;
	}

	@Override
	public void visitLocation(URI location) throws IOException {
		checkIsOpen();
		this.location = location;
	}

	@Override
	public void visitTitle(String title) throws IOException {
		checkIsOpen();
		this.title = title;
	}

	@Override
	public void visitLength(Long length) throws IOException {
		checkIsOpen();
		this.length = length;
	}

	@Override
	public void visitEntryEnd() throws IOException {
		checkIsOpen();
		if (location != null) {
			bw.write("File" + trackCounter + "=" + FileUtils.relativePath(location, file));
			bw.newLine();
			bw.write("Title" + trackCounter + "=" + title);
			bw.newLine();
			bw.write("Length" + trackCounter + "=" + length);
			bw.newLine();
			bw.newLine();
		}
	}

	@Override
	public void visitNumberOfEntries(Integer numberofentries) throws IOException {
		checkIsOpen();
	}

	@Override
	public void visitEnd(File file) throws IOException {
		checkIsOpen();
		bw.write("NumberOfEntries=" + trackCounter);
		bw.newLine();
		bw.write("Version=2");
		bw.newLine();
		bw.close();
		bw = null;
	}

	private void checkIsOpen() {
		if (bw == null) {
			throw new IllegalStateException();
		}
	}
}
