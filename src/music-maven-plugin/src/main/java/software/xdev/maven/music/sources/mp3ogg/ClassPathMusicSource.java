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

import java.io.InputStream;


/**
 * Uses a classpath file as a music source.
 */
public class ClassPathMusicSource extends MP3OggMusicSource
{
	private String classpath;
	
	public ClassPathMusicSource()
	{
	}
	
	public ClassPathMusicSource(final String classpath)
	{
		this.classpath = classpath;
	}
	
	public String getClasspath()
	{
		return this.classpath;
	}
	
	public void setClasspath(final String classpath)
	{
		this.classpath = classpath;
	}
	
	@Override
	public InputStream openInputStream()
	{
		return this.getClass().getResourceAsStream(this.getClasspath());
	}
	
	@Override
	public String toString()
	{
		return "[classpath]:" + this.getClasspath();
	}
}
