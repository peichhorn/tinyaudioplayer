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
import java.io.IOException;
import java.net.URI;

/**
 * This interface is used to abstract different playlist data-formats.<br>
 * Implementations of this interface are able to handle playlist-like files.
 * <p>
 * <b>Details:</b><br>
 * Methods get invoked after this scheme:
 * <pre>
 * visitComment(String)
 * ...
 * visitBegin(File)
 *   ...
 *   visitEntryBegin()
 *     visitLocation(String)
 *     ...
 *     visitLength(Long)
 *     visitTitle(String)
 *     visitComment(String)
 *   visitEntryEnd()
 *   visitEntryBegin()
 *     visitTitle(String)
 *     visitLength(Long)
 *     visitLocation(String)
 *   visitEntryEnd()
 *   visitComment(String)
 *   ...
 * visitEnd(File)
 * </pre>
 */
public interface IPlaylistFileVisitor {
	public void visitBegin(File file) throws IOException;

	public void visitComment(String comment) throws IOException;

	public void visitEntryBegin() throws IOException;

	public void visitLocation(URI location) throws IOException;

	public void visitTitle(String title) throws IOException;

	public void visitLength(Long length) throws IOException;

	public void visitEntryEnd() throws IOException;

	public void visitNumberOfEntries(Integer numberofentries) throws IOException;

	public void visitEnd(File file) throws IOException;
}
