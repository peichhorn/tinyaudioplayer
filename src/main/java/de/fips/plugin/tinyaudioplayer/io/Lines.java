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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Yield.yield;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Lines {
	public static Iterable<String> in(final File file) throws FileNotFoundException {
		return in(new FileReader(file));
	}

	public static Iterable<String> in(final InputStream inputStream) {
		return in(new InputStreamReader(inputStream));
	}

	public static Iterable<String> in(final Reader reader) {
		return in(new BufferedReader(reader));
	}

	private static Iterable<String> in(final BufferedReader in) {
		try {
			for (String next = in.readLine(); next != null; next = in.readLine()) yield(next);
		} finally {
			in.close();
		}
	}
}
