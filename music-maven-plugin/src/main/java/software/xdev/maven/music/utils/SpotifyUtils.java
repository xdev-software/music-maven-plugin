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
package software.xdev.maven.music.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Utility class for Spotify-related functionality.
 */
public final class SpotifyUtils {
    private static final String OS_NAME_LOWER = System.getProperty("os.name", "").toLowerCase();
    
    // For parsing Spotify URLs and URIs
    private static final Pattern SPOTIFY_URI_PATTERN =
        Pattern.compile("^spotify:(track|playlist|album|artist|episode|show):([a-zA-Z0-9]+)$");
    private static final Pattern SPOTIFY_URL_PATTERN = Pattern.compile(
        "^https:\\/\\/open\\.spotify\\.com\\/(track|playlist|album|artist|episode|show)\\/([a-zA-Z0-9]+)(?:\\?.*)?$");
    
    private SpotifyUtils() {
        // Utility class, prevent instantiation
    }
    
    /**
     * Validates and converts a Spotify URI or URL to the standard 'spotify:type:id' format.
     *
     * @param input The Spotify URI or URL to validate and convert
     * @return The standardized Spotify URI
     * @throws MojoExecutionException if the input is not a valid Spotify URI or URL
     */
    public static String validateAndConvertSpotifyUri(String input) throws MojoExecutionException {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        
        final String trimmed = input.trim();
        final Matcher uriMatcher = SPOTIFY_URI_PATTERN.matcher(trimmed);
        final Matcher urlMatcher = SPOTIFY_URL_PATTERN.matcher(trimmed);
        
        if (uriMatcher.matches()) {
            // Input is already a valid 'spotify:type:id' URI
            return trimmed;
        } else if (urlMatcher.matches()) {
            // Input is an 'https://open.spotify.com/...' URL, convert it
            final String type = urlMatcher.group(1);
            final String id = urlMatcher.group(2);
            return "spotify:" + type + ":" + id;
        }
        
        throw new MojoExecutionException("Invalid Spotify URI or URL format: " + input
            + System.lineSeparator()
            + "Please use a 'spotify:<type>:<id>' URI (e.g., spotify:track:xxxx) "
            + "or 'https://open.spotify.com/<type>/<id>' URL (e.g., https://open.spotify.com/track/xxxx).");
    }
    
    /**
     * Checks if Spotify is installed on the current system.
     *
     * @return true if Spotify is installed, false otherwise
     */
    public static boolean isSpotifyInstalled() {
        ProcessBuilder pb;
        if (OS_NAME_LOWER.contains("win")) {
            pb = new ProcessBuilder("where", "spotify");
        } else if (OS_NAME_LOWER.contains("mac")) {
            pb = new ProcessBuilder("test", "-d", "/Applications/Spotify.app");
        } else {
            // Assuming Linux or other Unix-like
            pb = new ProcessBuilder("which", "spotify");
        }
        
        try {
            final Process process = pb.start();
            consumeStream(process.getInputStream(), false);
            consumeStream(process.getErrorStream(), false);
            return process.waitFor() == 0;
        } catch (final IOException e) {
            return false;
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    /**
     * Checks if Spotify is currently running.
     *
     * @return true if Spotify is running, false otherwise
     */
    public static boolean isSpotifyRunning() {
        ProcessBuilder pb;
        try {
            if (OS_NAME_LOWER.contains("win")) {
                pb = new ProcessBuilder("tasklist", "/NH", "/FI", "IMAGENAME eq spotify.exe");
                final Process process = pb.start();
                final String output = consumeStream(process.getInputStream(), true);
                consumeStream(process.getErrorStream(), false);
                process.waitFor();
                return output.toLowerCase().contains("spotify.exe");
            } else if (OS_NAME_LOWER.contains("mac")) {
                pb = new ProcessBuilder(
                    "osascript",
                    "-e",
                    "tell application \"System Events\" to (name of processes) contains \"Spotify\"");
                final Process process = pb.start();
                final String output = consumeStream(process.getInputStream(), true).trim();
                consumeStream(process.getErrorStream(), false);
                final int exitCode = process.waitFor();
                return exitCode == 0 && "true".equalsIgnoreCase(output);
            } else {
                // Assuming Linux or other Unix-like
                pb = new ProcessBuilder("pgrep", "-x", "-i", "spotify");
                final Process process = pb.start();
                consumeStream(process.getInputStream(), false);
                consumeStream(process.getErrorStream(), false);
                return process.waitFor() == 0;
            }
        } catch (final IOException e) {
            return false;
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    /**
     * Plays a Spotify URI using platform-specific commands.
     *
     * @param spotifyUri The Spotify URI to play
     * @throws MojoExecutionException if playback fails
     */
    public static void playSpotifyUri(String spotifyUri) throws MojoExecutionException {
        ProcessBuilder pb;
        
        if (OS_NAME_LOWER.contains("win")) {
            // For 'start' command on Windows, an empty title "" is often needed if the path/URL might contain spaces
            pb = new ProcessBuilder("cmd", "/c", "start", "\"\"", spotifyUri);
        } else if (OS_NAME_LOWER.contains("mac")) {
            pb = new ProcessBuilder("osascript", "-e",
                String.format("tell application \"Spotify\" to play track \"%s\"", spotifyUri));
        } else if (OS_NAME_LOWER.contains("nux") || OS_NAME_LOWER.contains("nix")) {
            pb = new ProcessBuilder("dbus-send", "--print-reply", "--dest=org.mpris.MediaPlayer2.spotify",
                "/org/mpris/MediaPlayer2", "org.mpris.MediaPlayer2.Player.OpenUri", "string:" + spotifyUri);
        } else {
            throw new MojoExecutionException("Unsupported operating system for Spotify control: " + System.getProperty("os.name"));
        }
        
        try {
            final Process process = pb.start();
            final String stdOut = consumeStream(process.getInputStream(), true);
            final String stdErr = consumeStream(process.getErrorStream(), true);
            final int exitCode = process.waitFor();
            
            if (exitCode != 0) {
                String errorMessage = String.format(
                    "Failed to play Spotify URI '%s'. Exit code: %d.",
                    spotifyUri,
                    exitCode);
                if (stdErr != null && !stdErr.trim().isEmpty()) {
                    errorMessage += " Error: " + stdErr.trim();
                } else if (stdOut != null && !stdOut.trim().isEmpty()) {
                    errorMessage += " Output: " + stdOut.trim();
                }
                throw new MojoExecutionException(errorMessage);
            }
        } catch (final IOException e) {
            throw new MojoExecutionException(
                "Failed to execute command for Spotify URI '" + spotifyUri + "'.", e);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MojoExecutionException(
                "Spotify playback command interrupted for URI '" + spotifyUri + "'.", e);
        }
    }
    
    /**
     * Consumes an input stream, optionally capturing its output.
     *
     * @param stream The input stream to consume
     * @param captureOutput Whether to capture and return the stream's output
     * @return The captured output if captureOutput is true, empty string otherwise
     * @throws IOException if an I/O error occurs
     */
    private static String consumeStream(final InputStream stream, final boolean captureOutput) throws IOException {
        final StringBuilder sb = new StringBuilder();
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (captureOutput) {
                    sb.append(line).append(System.lineSeparator());
                }
            }
        }
        return sb.toString();
    }
} 