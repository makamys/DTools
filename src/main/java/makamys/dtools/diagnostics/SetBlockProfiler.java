package makamys.dtools.diagnostics;

import static makamys.dtools.DTools.LOGGER;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import makamys.dtools.DTools;
import makamys.dtools.listener.IFMLEventListener;
import makamys.dtools.util.Util;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class SetBlockProfiler implements IFMLEventListener {
    
    public static SetBlockProfiler instance;
    
    private List<Record> data = new ArrayList<>();
    private static StringPool stringPool = new StringPool();
    
    @Override
    public void onServerStarted(FMLServerStartedEvent event) {
        data.clear();
    }
    
    @Override
    public void onServerStopped(FMLServerStoppedEvent event) {
        try(Writer w = new FileWriter(Util.childFile(DTools.OUT_DIR, "setblockprofiler.txt"))) {
            Output out = new WriterOutput(w);
            
            out.writeLine(data.size() + " total calls");
            out.writeLine("");
            
            ThingCounter<Integer> callerCounter = new ThingCounter<>();
            callerCounter.displayAs(x -> stringPool.lookup(x));
            callerCounter.setMinimum(10);
            
            for(Record rec : data) {
                callerCounter.count(rec.traceId);
            }
            
            out.writeLine("Breakdown by callers:");
            callerCounter.print(out);
            
            ThingCounter<Pair<Block, Integer>> blockCounter = new ThingCounter<>();
            blockCounter.displayAs(SetBlockProfiler::getBlockName);
            
            for(Record rec : data) {
                blockCounter.count(Pair.of(rec.block, rec.meta));
            }
            
            out.writeLine("");
            out.writeLine("Breakdown by blocks:");
            blockCounter.print(out);
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        data.clear();
    }
    
    public static String getBlockName(Pair<Block, Integer> p) {
        if(p.getLeft() == Blocks.air) {
            return "Air";
        } else {
            try {
                return new ItemStack(p.getLeft(), 1, p.getRight()).getDisplayName();
            } catch(Exception e) {
                e.printStackTrace();
                return p.getLeft().getLocalizedName();
            }
        }
    }

    public void onSetBlock(int x, int y, int z, Block block, int meta, int flags) {
        String trace = "";
        StackTraceElement[] stes = new Throwable().getStackTrace();
        for(int i = 2; i < stes.length; i++) {
            StackTraceElement ste = stes[i];
            trace += ste.getClassName() + "." + ste.getMethodName() + " < ";
        }
        
        Record rec = new Record();
        rec.setTrace(trace);
        rec.x = x;
        rec.y = y;
        rec.z = z;
        rec.block = block;
        rec.meta = meta;
        rec.flags = flags;
        
        data.add(rec);
    }
    
    public static class Record {
        public int traceId;
        public int x;
        public int y;
        public int z;
        public Block block;
        public int meta;
        public int flags;
        
        public String getTrace() {
            return stringPool.lookup(traceId);
        }
        
        public void setTrace(String trace) {
            traceId = stringPool.getId(trace);
        }
    }
    
    public static class StringPool {
        Map<Integer, String> idToString = new HashMap<>();
        Map<String, Integer> stringToId = new HashMap<>();
        
        public String lookup(int id) {
            return idToString.get(id);
        }
        
        public int getId(String string) {
            Integer id = stringToId.get(string);
            if(id == null) {
                id = idToString.size();
                idToString.put(id, string);
                stringToId.put(string, id);
            }
            return id;
        }
    }
    
    public static class ThingCounter<T> {
        
        private Function<T, String> displayFunc = Object::toString;
        private int min = 0;
        
        Multiset<T> set = HashMultiset.create();
        
        public void count(T thing) {
            set.add(thing);
        }
        
        public void print(Output out) throws IOException {
            for(Multiset.Entry<T> ent : set.entrySet().stream().sorted(this::compareMultisetEntries).collect(Collectors.toList())) {
                if(ent.getCount() >= min) {
                    out.writeLine(StringUtils.leftPad("" + ent.getCount(), 10) + "  " + displayFunc.apply(ent.getElement()));
                }
            }
        }
        
        public int compareMultisetEntries(Multiset.Entry<T> a, Multiset.Entry<T> b) {
            if(a.getCount() != b.getCount()) {
                return -Integer.compare(a.getCount(), b.getCount());
            } else {
                return displayFunc.apply(a.getElement()).compareTo(displayFunc.apply(b.getElement()));
            }
        }
        
        public void displayAs(Function<T, String> func) {
            displayFunc = func;
        }
        
        public void setMinimum(int min) {
            this.min = min;
        }
        
    }
    
    public static interface Output {
        void writeLine(String line) throws IOException;
    }
    
    public static class WriterOutput implements Output {
        private final Writer w;
        public WriterOutput(Writer writer) {
            this.w = writer;
        }
        
        @Override
        public void writeLine(String line) throws IOException {
            w.write(line + "\n");
        }
    }

}
