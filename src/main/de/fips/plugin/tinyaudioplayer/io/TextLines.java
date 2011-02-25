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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TextLines implements Iterable<String>, Iterator<String>, Closeable {
	private final BufferedReader in;
	private volatile boolean ignoreEmptyLines;
	private volatile boolean hasNext;
	private volatile boolean nextDefined;
	private String next;

	public TextLines ignoringEmptyLines() {
		ignoreEmptyLines = true;
		return this;
	}

	public boolean hasNext() {
		if (!nextDefined) {
			hasNext = getNext();
			nextDefined = true;
		}
		return hasNext;
	}

	public String next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		nextDefined = false;
		return next;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public void close() {
		try {
			in.close();
		} catch (IOException ignore) {
		}
		next = null;
		nextDefined = true;
		hasNext = false;
	}

	@Override
	public Iterator<String> iterator() {
		return this;
	}

	private boolean getNext() {
		try {
			for (next = in.readLine(); ignoreEmptyLines && (next != null); next = in.readLine()) {
				next = next.trim();
				if (!next.isEmpty()) {
					break;
				}
			}
			return (next != null);
		} catch (IOException e) {
			return false;
		}
	}

	public static TextLines of(final File file) {
		try {
			return of(new FileReader(file));
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static TextLines of(final Reader reader) {
		return new TextLines(new BufferedReader(reader));
	}
}