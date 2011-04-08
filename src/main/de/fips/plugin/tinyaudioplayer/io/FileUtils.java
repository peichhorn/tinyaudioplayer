package de.fips.plugin.tinyaudioplayer.io;

import java.io.File;
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

	public static String relativePath(final File file, File relativeTo) {
		String path = "";

		if (relativeTo.isFile()) {
			relativeTo = relativeTo.getParentFile();
		}

		List<String> fileList = getDelimitedStringAsList(file.getAbsolutePath(), File.separator);
		List<String> relativeList = getDelimitedStringAsList(relativeTo.getAbsolutePath(), File.separator);

		int size = fileList.size();
		int relativeSize = relativeList.size();
		int count = 0;

		while ((count < size) && (count < relativeSize)) {
			if (fileList.get(count).equalsIgnoreCase(relativeList.get(count))) {
				count++;
			} else {
				break;
			}
		}

		for (int i = count; i < relativeSize; i++) {
			path += ".." + File.separator;
		}

		for (int i = count; i < size; i++) {
			path += fileList.get(i) + File.separator;
		}

		if (path.indexOf(File.separator) > -1) {
			path = path.substring(0, path.lastIndexOf(File.separator));
		}
		return path;
	}

	public static boolean removeDirectory(final File directory) {
		if (directory == null) {
			return false;
		} else if (!directory.exists()) {
			return true;
		} else if (!directory.isDirectory()) {
			return false;
		}
		final String[] list = directory.list();
		if (list != null) {
			for (String file : list) {
				File entry = new File(directory, file);
				if (entry.isDirectory()) {
					if (!removeDirectory(entry)) {
						return false;
					}
				} else if (!entry.delete()) {
					return false;
				}
			}
		}
		return directory.delete();
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
