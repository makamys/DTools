package makamys.dtools.util;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.EnumUtils;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;

public class AnnotationBasedConfigHelper {
    
    private Logger logger;
    private Class<?> theConfigClass;
    
    public AnnotationBasedConfigHelper(Class<?> theConfigClass, Logger logger) {
        this.logger = logger;
        this.theConfigClass = theConfigClass;
    }
    
    public boolean loadFields(Configuration config) {
        return iterateOverConfigAnd(config, this::setConfigClassField);
    }
    
    private void setConfigClassField(Field field, Object newValue) {
        try {
            field.set(null, newValue);
        } catch (Exception e) {
            logger.error("Failed to set value of field " + field.getName());
            e.printStackTrace();
        }
    }
    
    private boolean iterateOverConfigAnd(Configuration config, BiConsumer<Field, Object> callback) {
        boolean needReload = false;
        
        for(Field field : theConfigClass.getFields()) {
            if(!Modifier.isStatic(field.getModifiers())) continue;
            
            NeedsReload needsReload = null;
            ConfigBoolean configBoolean = null;
            ConfigInt configInt = null;
            ConfigEnum configEnum = null;
            ConfigStringList configStringList = null;
            ConfigString configString = null;
            
            for(Annotation an : field.getAnnotations()) {
                if(an instanceof NeedsReload) {
                    needsReload = (NeedsReload) an;
                } else if(an instanceof ConfigInt) {
                    configInt = (ConfigInt) an;
                } else if(an instanceof ConfigBoolean) {
                    configBoolean = (ConfigBoolean) an;
                } else if(an instanceof ConfigEnum) {
                    configEnum = (ConfigEnum) an;
                } else if(an instanceof ConfigStringList) {
                    configStringList = (ConfigStringList) an;
                }  else if(an instanceof ConfigString) {
                    configString = (ConfigString) an;
                }
            }
            
            if(configBoolean == null && configInt == null && configEnum == null && configStringList == null && configString == null) continue;
            
            Object currentValue = null;
            Object newValue = null;
            try {
                currentValue = field.get(null);
            } catch (Exception e) {
                logger.error("Failed to get value of field " + field.getName());
                e.printStackTrace();
                continue;
            }
            
            if(configBoolean != null) {
                newValue = config.getBoolean(field.getName(), configBoolean.cat(), configBoolean.def(), configBoolean.com());
            } else if(configInt != null) {
                newValue = config.getInt(field.getName(), configInt.cat(), configInt.def(), configInt.min(), configInt.max(), configInt.com()); 
            } else if(configEnum != null) {
                boolean lowerCase = true;
                
                Class<? extends Enum> configClass = configEnum.clazz();
                Map<String, ? extends Enum> enumMap = EnumUtils.getEnumMap(configClass);
                String[] valuesStrUpper = (String[])enumMap.keySet().stream().toArray(String[]::new);
                String[] valuesStr = Arrays.stream(valuesStrUpper).map(s -> lowerCase ? s.toLowerCase() : s).toArray(String[]::new);
                
                // allow upgrading boolean to string list
                ConfigCategory cat = config.getCategory(configEnum.cat());
                Property oldProp = cat.get(field.getName());
                String oldVal = null;
                if(oldProp != null && oldProp.getType() != Type.STRING) {
                    oldVal = oldProp.getString();
                    cat.remove(field.getName());
                }
                
                String newValueStr = config.getString(field.getName(), configEnum.cat(),
                        lowerCase ? configEnum.def().toLowerCase() : configEnum.def().toUpperCase(), configEnum.com(), valuesStr);
                if(oldVal != null) {
                    newValueStr = oldVal;
                }
                if(!enumMap.containsKey(newValueStr.toUpperCase())) {
                    newValueStr = configEnum.def().toUpperCase();
                    if(lowerCase) {
                        newValueStr = newValueStr.toLowerCase();
                    }
                }
                newValue = enumMap.get(newValueStr.toUpperCase());
                
                Property newProp = cat.get(field.getName());
                if(!newProp.getString().equals(newValueStr)) {
                    newProp.set(newValueStr);
                }
            } else if(configStringList != null) {
                newValue = config.getStringList(field.getName(), configStringList.cat(), configStringList.def(), configStringList.com());
            } else if(configString != null) {
                newValue = config.getString(field.getName(), configString.cat(), configString.def(), configString.com());
            }
            
            if(needsReload != null && !newValue.equals(currentValue)) {
                needReload = true;
            }
            
            callback.accept(field, newValue);
        }
        
        return needReload;
    }
    
    public void saveFields(Configuration config) {
        iterateOverConfigAnd(config, (field, newValue) -> {
            try {
                Object fieldValue = field.get(null);
                if(!fieldValue.equals(newValue)) {
                    for(String catName : config.getCategoryNames()) {
                        ConfigCategory cat = config.getCategory(catName);
                        Property prop = cat.get(field.getName());
                        if(prop != null) {
                            try {
                                setProperty(prop, fieldValue);
                            } catch(Exception e) {
                                logger.error("Failed to save field " + field.getName());
                                e.printStackTrace();
                            }
                            return;
                        }   
                    }
                    logger.error("Couldn't find property named " + field.getName() + ", can't save new value");
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        });
        
        if(config.hasChanged()) {
            config.save();
        }
    }
    
    private static void setProperty(Property prop, Object newValue) {
        switch(prop.getType()) {
        case BOOLEAN:
            prop.set((Boolean)newValue);
            break;
        case DOUBLE:
            prop.set((Double)newValue);
            break;
        case INTEGER:
            prop.set((Integer)newValue);
            break;
        case STRING:
            prop.set((String)newValue);
            break;
        default:
            throw new UnsupportedOperationException();
        }
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface NeedsReload {

    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface ConfigBoolean {

        String cat();
        boolean def();
        String com() default "";

    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface ConfigInt {

        String cat();
        int min();
        int max();
        int def();
        String com() default "";

    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface ConfigString {

        String cat();
        String def();
        String com() default "";

    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface ConfigStringList {

        String cat();
        String[] def();
        String com() default "";

    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface ConfigEnum {

        String cat();
        String def();
        String com() default "";
        Class<? extends Enum> clazz();

    }
}
