# DTools

A Minecraft 1.7.10 mod containing various utilities for debugging, benchmarking and testing.

The mod was formerly part of [CoreTweaks](https://github.com/makamys/CoreTweaks), and got split off into a separate mod to make things more organized.

## Features

* Startup and frame time profiler
* Display RAM allocation rate
* Wireframe rendering
* Automatically load a world after the game starts 
* Command to print info about block that is currently being aimed at
* Class loading logger
* Dump Thaumcraft aspects of all items (requires [NHEI](https://github.com/GTNewHorizons/NotEnoughItems))
* Show Thaumcraft aspects for all items without having to scan them if player is in creative mode
* Dump mob spawn tables
* Log the camera position and world seed of each screenshot to a file
* Backports of sprint flying and `doWeatherCycle` (from Et Futurum Requiem)
* A simple F3+F4 gamemode switcher
* A one-button "dev world primer" that automatically sets [stuff like `doDaylightCycle false`](src/main/resources/assets/dtools/default_config/dtools/devsetup.ini).
* A keyboard shortcut to delete worlds, or just their region files, from the world selection GUI
* An OpenGL debug logger
* See the [Config](https://github.com/makamys/DTools/wiki/Config) page on the wiki for the full list.

## Usage

Most of the mod's features are disabled by default to minimize potential conflicts. Make sure you check the config and enable the desired features there.

### About `nomixin` builds

The mod comes in two flavors:
* The regular version embeds Mixin 0.7.11, allowing the mod to run standalone. However, this makes the jar a bit larger, and can cause problems in certain use cases.
* The version marked with `+nomixin` doesn't embed Mixin, which lets it avoid these problems. But it requires a separate [Mixin bootstrap mod](https://gist.github.com/makamys/7cb74cd71d93a4332d2891db2624e17c#mixin-bootstrap-mods) to be installed in order to run. If you have one installed already, getting this version is recommended.

## Suggested mods

For more 1.7.10 bugfix/performance/debug mods, refer to [this list](https://gist.github.com/makamys/7cb74cd71d93a4332d2891db2624e17c).

## Credits

- Sprint flying code is based on code from [Et Futurum Requiem](https://github.com/Roadhog360/Et-Futurum-Requiem).

## License

This mod is licensed under the [MIT License](LICENSE).

## Contributing

When running in an IDE, add these program arguments
```
--tweakClass org.spongepowered.asm.launch.MixinTweaker --mixin dtools.mixin.json --mixin dtools-init.mixin.json --mixin dtools-preinit.mixin.json
```
and these VM arguments
```
-Dfml.coreMods.load=makamys.dtools.DToolsPlugin
```
