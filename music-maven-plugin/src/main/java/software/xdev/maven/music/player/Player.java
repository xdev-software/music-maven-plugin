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
package software.xdev.maven.music.player;

import org.apache.maven.plugin.logging.Log;

import software.xdev.maven.music.sources.MusicSource;


public interface Player<S extends MusicSource>
{
	Class<S> supportedMusicSourceType();
	
	@SuppressWarnings("unchecked")
	default boolean play(final Object source, final float defaultVolumeDB, final Log log)
	{
		return this.play((S)source, defaultVolumeDB, log);
	}
	
	/**
	 * @return <code>true</code> if the music was stopped externally
	 */
	boolean play(S source, float defaultVolumeDB, Log log);
	
	void stop();
}
