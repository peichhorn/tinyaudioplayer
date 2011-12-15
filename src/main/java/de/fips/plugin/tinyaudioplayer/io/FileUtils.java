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

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileUtils {
	public static String fileNameWithoutExtension(final File file) {
		String name = file.getName();
		final int index = name.lastIndexOf(".");
		if (index > 0) {
			name = name.substring(0, index);
		}
		return name;
	}

	public static String relativePath(URI uri, File relativeTo) {
		if (uri == null) return null;
		try {
			return FileUtils.relativePath(new File(uri), relativeTo);
		} catch(IllegalArgumentException ignore) {
			return uri.toString();
		}
	}

	public static String relativePath(final File file, File relativeTo) {
		final StringBuilder path = new StringBuilder(file.getAbsolutePath());

		if (relativeTo.isFile()) {
			relativeTo = relativeTo.getParentFile();
		}

		List<String> fileList = getDelimitedStringAsList(file.getAbsolutePath(), File.separator);
		List<String> relativeToList = getDelimitedStringAsList(relativeTo.getAbsolutePath(), File.separator);
		
		if (fileList.get(0).equals(relativeToList.get(0))) {
			int size = fileList.size();
			int relativeToSize = relativeToList.size();
			int count = 0;

			while ((count < size) && (count < relativeToSize)) {
				if (fileList.get(count).equalsIgnoreCase(relativeToList.get(count))) {
					count++;
				} else {
					break;
				}
			}
			path.setLength(0);
			for (int i = count; i < relativeToSize; i++) {
				path.append("..").append(File.separator);
			}

			for (int i = count; i < size; i++) {
				path.append(fileList.get(i)).append(File.separator);
			}

			if (path.indexOf(File.separator) > -1) {
				return path.substring(0, path.lastIndexOf(File.separator));
			}
		}
		return path.toString();
	}

	private static List<String> getDelimitedStringAsList(final String str, final String delimiter) {
		final List<String> resultList = new ArrayList<String>();
		final StringTokenizer st = new StringTokenizer(str, delimiter);
		while (st.hasMoreTokens()) {
			resultList.add(st.nextToken());
		}
		return resultList;
	}
}
