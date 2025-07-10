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
package software.xdev.maven.music.sources.mp3ogg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UncheckedIOException;


public class FileMusicSource extends MP3OggMusicSource
{
	private String file;
	
	public String getFile()
	{
		return this.file;
	}
	
	public void setFile(final String file)
	{
		this.file = file;
	}
	
	@Override
	public InputStream openInputStream()
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
	
	@Override
	public String toString()
	{
		return "[file]:" + this.getFile();
	}
}
