# 2.0.0
_This release contains breaking changes_

* Redesigned project to easier make custom extensions possible
* New declaration format:
Old:
```xml
<configuration>
    <sources>
        <musicSource>
            <uri>https://incompetech.com/music/royalty-free/mp3-royaltyfree/Corncob.mp3</uri>
        </musicSource>
        <musicSource>
            <file>my_cool_music.mp3</file>
        </musicSource>
        <musicSource>
            <classpath>/default/Andrew_Codeman_-_03_-_Mussels_short_version.ogg</classpath>
        </musicSource>
    </sources>
    <shuffle>true</shuffle>
</configuration>
```
New:
```xml
<configuration>
    <sources>
        <source>
            <uri>
                <uri>https://incompetech.com/music/royalty-free/mp3-royaltyfree/Corncob.mp3</uri>
            </uri>
        </source>
        <source>
            <file>
                <file>my_cool_music.mp3</file>
            </file>
        </source>
        <source>
            <classpath>
                <classpath>/default/Andrew_Codeman_-_03_-_Mussels_short_version.ogg</classpath>
            </classpath>
        </source>
    </sources>
</configuration>
```

# 1.0.3
* Migrated deployment to _Sonatype Maven Central Portal_ [#155](https://github.com/xdev-software/standard-maven-template/issues/155)
* Updated dependencies

# 1.0.2
* Added a property which allows disabling the plugin #11 @runeflobakk
  > enables the troller to subtly disable accidentally blasting the music in the office space while waiting for the trollees to be trolled, while avoiding being self-trolled

# 1.0.1
* Updated dependencies

# 1.0.0
_Initial release_
