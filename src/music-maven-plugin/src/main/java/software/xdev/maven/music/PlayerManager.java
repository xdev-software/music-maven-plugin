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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.maven.plugin.logging.Log;

import software.xdev.maven.music.player.Player;
import software.xdev.maven.music.sources.MusicSource;


@SuppressWarnings("java:S6548")
public final class PlayerManager
{
	private static final PlayerManager INSTANCE = new PlayerManager();
	
	public static PlayerManager instance()
	{
		return INSTANCE;
	}
	
	@SuppressWarnings({"java:S3740", "rawtypes"})
	private final List<Player> availablePlayers;
	private final Map<Class<? extends MusicSource>, Player<?>> sourcePlayers = new HashMap<>();
	
	private Player<?> lastActivePlayer;
	
	private final ReentrantLock playLock = new ReentrantLock();
	
	private PlayerManager()
	{
		this.availablePlayers = ServiceLoader.load(Player.class)
			.stream()
			.map(ServiceLoader.Provider::get)
			.toList();
	}
	
	@SuppressWarnings("unchecked")
	public boolean play(final MusicSource source, final float defaultVolumeDB, final Log log)
	{
		this.playLock.lock();
		
		try
		{
			final Player<?> player = this.sourcePlayers.computeIfAbsent(
				source.getClass(),
				clazz -> this.availablePlayers.stream()
					.filter(p -> p.supportedMusicSourceType().isAssignableFrom(clazz))
					.findFirst()
					.orElseThrow());
			this.lastActivePlayer = player;
			log.info("[🎵] Now playing: " + source);
			return player.play(source, defaultVolumeDB, log);
		}
		finally
		{
			this.playLock.unlock();
		}
	}
	
	public synchronized void stopActivePlayer()
	{
		if(this.lastActivePlayer != null)
		{
			this.lastActivePlayer.stop();
			// Wait for proper stop
			if(this.playLock.isLocked())
			{
				this.playLock.lock();
				this.playLock.unlock();
			}
			this.lastActivePlayer = null;
		}
	}
}
