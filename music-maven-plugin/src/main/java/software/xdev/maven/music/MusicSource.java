/*
 * Copyright © 2024 XDEV Software (https://xdev.software)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package software.xdev.maven.music;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;


public class MusicSource
{
	/*
	 * Source
	 */
	private String uri;
	private String classpath;
	private String file;
	
	/**
	 * Optional: Overwrites the default volume in dB.
	 */
	private Float volumeDB;
	
	public String getUri()
	{
		return this.uri;
	}
	
	public void setUri(final String uri)
	{
		this.uri = uri;
	}
	
	public String getClasspath()
	{
		return this.classpath;
	}
	
	public void setClasspath(final String classpath)
	{
		this.classpath = classpath;
	}
	
	public String getFile()
	{
		return this.file;
	}
	
	public void setFile(final String file)
	{
		this.file = file;
	}
	
	public Float getVolumeDB()
	{
		return this.volumeDB;
	}
	
	public void setVolumeDB(final Float volumeDB)
	{
		this.volumeDB = volumeDB;
	}
	
	public InputStream openInputStream()
	{
		if(this.getClasspath() != null)
		{
			return this.getClass().getResourceAsStream(this.getClasspath());
		}
		
		if(this.getFile() != null)
		{
			final File f = new File(this.getFile());
			try
			{
				return new FileInputStream(f);
			}
			catch(final FileNotFoundException e)
			{
				throw new UncheckedIOException("Failed to find " + f.getAbsolutePath(), e);
			}
		}
		
		try
		{
			return URI.create(this.getUri()).toURL().openStream();
		}
		catch(final IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}
	
	public static MusicSource fromClassPath(final String classpath)
	{
		final MusicSource source = new MusicSource();
		source.setClasspath(classpath);
		return source;
	}
	
	@Override
	public String toString()
	{
		if(this.getClasspath() != null)
		{
			return "[classpath]:" + this.getClasspath();
		}
		if(this.getFile() != null)
		{
			return "[file]:" + this.getFile();
		}
		return this.getUri();
	}
}
