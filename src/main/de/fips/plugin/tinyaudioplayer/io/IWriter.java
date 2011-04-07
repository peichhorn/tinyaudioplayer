package de.fips.plugin.tinyaudioplayer.io;

import java.io.File;

public interface IWriter<E> {
    public boolean canHandle(File file);

    public String formatName();

	public String completeFormatName();

    public String formatExtensions();

    public void write(File file, E object);
}

