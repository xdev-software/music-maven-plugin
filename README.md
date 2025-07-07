[![Latest version](https://img.shields.io/maven-central/v/software.xdev/music-maven-plugin?logo=apache%20maven)](https://mvnrepository.com/artifact/software.xdev/music-maven-plugin)
[![Build](https://img.shields.io/github/actions/workflow/status/xdev-software/music-maven-plugin/check-build.yml?branch=develop)](https://github.com/xdev-software/music-maven-plugin/actions/workflows/check-build.yml?query=branch%3Adevelop)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=xdev-software_music-maven-plugin&metric=alert_status)](https://sonarcloud.io/dashboard?id=xdev-software_music-maven-plugin)

# music-maven-plugin

🎵 Plays some background music while Maven is building.

## Usage

### Basic

The following configuration will play some "elevator" music in the background as soon as Maven starts building:

```xml
<plugin>
    <groupId>software.xdev</groupId>
    <artifactId>music-maven-plugin</artifactId>
    <version>...</version>
    <!-- You might also want to add <inherited>false</inherited> so not every child executes this -->
    <executions>
        <execution>
            <phase>validate</phase>
            <goals>
                <goal>music</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

> [!TIP]
> To avoiding being self-trolled/not accidentally blasting music in the office space while waiting for the trollees to be trolled, you can set ``-Dmusic.skip`` or a global environment variable ``MAVEN_OPTS="-Dmusic.skip=true"``

### Customizing the music

You can customize the music by adding other sources:

```xml
<configuration>
    <sources>
        <musicSource>
            <uri>https://incompetech.com/music/royalty-free/mp3-royaltyfree/Corncob.mp3</uri>
        </musicSource>
        <musicSource>
            <!-- Relative to project directory -->
            <file>my_cool_music.mp3</file>
        </musicSource>
        <musicSource>
            <classpath>/default/Andrew_Codeman_-_03_-_Mussels_short_version.ogg</classpath>
        </musicSource>
    </sources>
    <shuffle>true</shuffle>
</configuration>
```

#### Supported codecs

| Codec | Container |
| --- | --- |
| [MP3](https://en.wikipedia.org/wiki/MP3) | ``.mp3`` |
| [Vorbis](https://en.wikipedia.org/wiki/Vorbis) | [``.ogg``](https://en.wikipedia.org/wiki/Ogg) |

> [!NOTE]
> Container files can also include different codecs.
> For example ``.ogg`` can also contain ``Opus``, ``Speex``, ``FLAC``, ... which are not supported.

> [!TIP]
> If you want to convert to supported formats you can do so with [``ffmpeg``](https://www.ffmpeg.org/):<br/>
> ``ffmpeg -i music.opus music.ogg``

## Installation
[Installation guide for the latest release](https://github.com/xdev-software/music-maven-plugin/releases/latest#Installation)

## Run the demo

> [!WARNING]
> Might be loud!

* Checkout the repo
* Run ``mvn install``

## Why?
This is obviously a joke/fun plugin.<br/>
You probably shouldn't use it in production or maybe only on April 1st to annoy your colleagues :P

We got the idea during XDEV's christmas party as [rfichtner](https://github.com/rfichtner) always needs more plugins for funnier Maven presentations and was inspired by [this GraalVM issue from joshlong](https://github.com/oracle/graal/issues/5327).

## Support
If you need support as soon as possible and you can't wait for any pull request, feel free to use [our support](https://xdev.software/en/services/support).

## Contributing
See the [contributing guide](./CONTRIBUTING.md) for detailed instructions on how to get started with our project.

## Dependencies and Licenses
View the [license of the current project](LICENSE) or the [summary including all dependencies](https://xdev-software.github.io/music-maven-plugin/dependencies)

The license and download information for the [built-in music](./music-maven-plugin/src/main/resources/default/) can be found in [sources.txt](./music-maven-plugin/src/main/resources/default/sources.txt).
