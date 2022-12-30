package makamys.dtools;

import static makamys.dtools.DTools.LOGGER;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import makamys.dtools.diagnostics.FMLBarProfiler;
import makamys.dtools.diagnostics.MethodProfiler;

@IFMLLoadingPlugin.SortingIndex(1001) // Run after deobf (FMLDeobfTweaker has an index of 1000)
public class DToolsPlugin implements IFMLLoadingPlugin {

	public DToolsPlugin() {
		LOGGER.info("Instantiating DToolsPlugin");
		Config.reload();
		DTools.init();
	}
	
	@Override
	public String[] getASMTransformerClass() {
		List<String> transformerClasses = new ArrayList<>();
		if(MethodProfiler.isActive()) {
			transformerClasses.add("makamys.dtools.asm.ProfilerTransformer");
		}
		if(FMLBarProfiler.isActive()) {
		    transformerClasses.add("makamys.dtools.asm.FMLBarProfilerTransformer");
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
