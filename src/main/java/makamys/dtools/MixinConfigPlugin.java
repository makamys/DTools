package makamys.dtools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.MixinEnvironment.Phase;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import cpw.mods.fml.relauncher.ReflectionHelper;
import makamys.dtools.diagnostics.WrongMixinHelper;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class MixinConfigPlugin implements IMixinConfigPlugin {
    
    @Override
    public void onLoad(String mixinPackage) {
        Config.reload();
        
    	if(JVMArgs.LAUNCH_MINIMIZED != null || JVMArgs.LAUNCH_ON_DESKTOP != null) {
    		((Set<String>)ReflectionHelper.getPrivateValue(LaunchClassLoader.class, Launch.classLoader, "classLoaderExceptions")).remove("org.lwjgl.");
    	}
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        List<String> mixins = new ArrayList<>();
        
        Phase phase = MixinEnvironment.getCurrentEnvironment().getPhase();
        if(phase == Phase.PREINIT) {
            
        } else if(phase == Phase.INIT) {
        	if(JVMArgs.LAUNCH_MINIMIZED != null) {
        		mixins.add("tweak.launchminimized.MixinWindowsDisplay");
        	}
        	if(JVMArgs.LAUNCH_ON_DESKTOP != null) {
        		mixins.add("tweak.launchondesktop.MixinLinuxDisplay");
        	}
        } else if(phase == Phase.DEFAULT) {
            if(Config.wireframe) mixins.add("diagnostics.wireframe.MixinEntityRenderer");
            
            if(Config.frameProfilerHooks) mixins.addAll(Arrays.asList("diagnostics.frameprofiler.MixinEntityRenderer",
                                                                        "diagnostics.frameprofiler.MixinMinecraft"));
            if(Config.freezeInputKey) mixins.addAll(Arrays.asList(
                    "automation.freezeinput.MixinMinecraft",
                    "automation.freezeinput.MixinMouseHelper"
            ));
            if(WrongMixinHelper.isEnabled()) mixins.addAll(Arrays.asList(
                    "diagnostics.wrongmixin.MixinGuiAchievements",
                    "diagnostics.wrongmixin.MixinGuiTextField"
            ));
            if(Config.logScreenshotPosition) mixins.add("diagnostics.wherewasi.MixinScreenshotHelper");
            if(Compat.isThaumcraftPresent() && Config.unlockAllAspects) mixins.addAll(Arrays.asList(
                    "tweak.thaumcraft.unlockallaspects.MixinClientTickEventsFML",
                    "tweak.thaumcraft.unlockallaspects.MixinPlayerKnowledge"
            ));
            if(!Compat.isEtFuturumRequiemPresent() && Config.sprintFlying) mixins.add("tweak.flyspeed.MixinEntityPlayer");
            if(!Compat.isEtFuturumRequiemPresent() && Config.doWeatherCycle) mixins.addAll(Arrays.asList(
                    "tweak.doweathercycle.MixinCommandHandler",
                    "tweak.doweathercycle.MixinWorldInfo")
            );
            if(Config.devWorldSetup) mixins.addAll(Arrays.asList(
                    "tweak.devsetup.MixinGuiCreateWorld")
            );
            if(Config.worldDeleter) mixins.addAll(Arrays.asList(
                    "tweak.worlddeleter.MixinGuiSelectWorld")
            );
        }
        return mixins;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

}
