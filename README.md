# DTools

A Minecraft 1.7.10 mod containing various utilities for debugging, benchmarking and testing.

The mod was formerly part of [CoreTweaks](https://github.com/makamys/CoreTweaks), and got split off into a separate mod to make things more organized.

## Features

* Startup and frame time profiler
* Display RAM allocation rate
* Wireframe rendering
* Automatically load a world after the game starts 
* Command to print info about block that is currently being aimed at
* See the [Config](https://github.com/makamys/DTools/wiki/Config) page on the wiki for the full list.

## Usage

Most of the mod's features are disabled by default to minimize potential conflicts. Make sure you check the config and enable the desired features there.

## Suggested mods

For more 1.7.10 bugfix/performance/debug mods, refer to [this list](https://gist.github.com/makamys/7cb74cd71d93a4332d2891db2624e17c).

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
