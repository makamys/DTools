package makamys.coretweaks;

import static makamys.coretweaks.CoreTweaks.LOGGER;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import makamys.coretweaks.diagnostics.FMLBarProfiler;
import makamys.coretweaks.diagnostics.MethodProfiler;

@IFMLLoadingPlugin.SortingIndex(1001) // Run after deobf (FMLDeobfTweaker has an index of 1000)
public class CoreTweaksPlugin implements IFMLLoadingPlugin {

	public CoreTweaksPlugin() {
		LOGGER.info("Instantiating CoreTweaksPlugin");
		Config.reload();
		CoreTweaks.init();
	}
	
	@Override
	public String[] getASMTransformerClass() {
		List<String> transformerClasses = new ArrayList<>();
		if(MethodProfiler.isActive()) {
			transformerClasses.add("makamys.coretweaks.asm.ProfilerTransformer");
		}
		if(FMLBarProfiler.isActive()) {
		    transformerClasses.add("makamys.coretweaks.asm.FMLBarProfilerTransformer");
        }
		
		return transformerClasses.toArray(new String[] {});
	}

	@Override
	public String getModContainerClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSetupClass() {
		if(MethodProfiler.isActive()) {
			MethodProfiler.instance.init();
		}
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAccessTransformerClass() {
		// TODO Auto-generated method stub
		return null;
	}
	

	
}
