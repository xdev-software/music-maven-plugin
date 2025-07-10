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
package software.xdev.maven.music.sources;

import software.xdev.maven.music.sources.mp3ogg.ClassPathMusicSource;
import software.xdev.maven.music.sources.mp3ogg.FileMusicSource;
import software.xdev.maven.music.sources.mp3ogg.URIMusicSource;


public class WrappedMusicSource implements MusicSource
{
	private ClassPathMusicSource classpath;
	private URIMusicSource uri;
	private FileMusicSource file;
	
	public WrappedMusicSource()
	{
	}
	
	public WrappedMusicSource(final ClassPathMusicSource classpath)
	{
		this.classpath = classpath;
	}
	
	public MusicSource getMusicSource()
	{
		if(this.classpath != null)
		{
			return this.classpath;
		}
		if(this.uri != null)
		{
			return this.uri;
		}
		return this.file;
	}
	
	public ClassPathMusicSource getClasspath()
	{
		return this.classpath;
	}
	
	public void setClasspath(final ClassPathMusicSource classpath)
	{
		this.classpath = classpath;
	}
	
	public URIMusicSource getUri()
	{
		return this.uri;
	}
	
	public void setUri(final URIMusicSource uri)
	{
		this.uri = uri;
	}
	
	public FileMusicSource getFile()
	{
		return this.file;
	}
	
	public void setFile(final FileMusicSource file)
	{
		this.file = file;
	}
}
