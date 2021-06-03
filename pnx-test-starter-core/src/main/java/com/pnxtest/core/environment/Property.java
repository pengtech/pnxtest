/*
 *  Copyright (c) 2020-2021
 *  This file is part of PnxTest framework.
 *
 *  PnxTest is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero Public License version 3 as
 *  published by the Free Software Foundation
 *
 *  PnxTest is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero Public License for more details.
 *
 *  You should have received a copy of the GNU Affero Public License
 *  along with PnxTest.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  For more information, please contact the author at this address:
 *  chen.baker@gmail.com
 *
 */

package com.pnxtest.core.environment;

import com.pnxtest.core.ApplicationKeys;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author nicolas.chen
 */
final class Property {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final Properties appProps = new Properties();
    private final VariableResolver variableResolver;


    public Property() {
        addSystemProperties();
        addDefaultProperties();
        variableResolver = new VariableResolver();
    }

    public void addSystemProperties(){
        for (Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
            if(!Pattern.matches("^(sun\\.|java\\.).*", String.valueOf(entry.getKey()))){
                appProps.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public void addDefaultProperties(){
        try {
            loadResourceFile("application.properties");

            String currentEnvId = getString(ApplicationKeys.PNX_ENVIRONMENT_ID);
            if (currentEnvId != null && !currentEnvId.trim().isEmpty()) {
                setProperty(ApplicationKeys.PNX_ENVIRONMENT_ID, currentEnvId);
                addBundle(currentEnvId);
            }

        }catch (Exception e){
            logger.log(Level.WARNING, e.getMessage());
        }

        File prjDir = new File(".").getAbsoluteFile().getParentFile();
        setProperty("project.path", prjDir.getAbsolutePath());
        if(!appProps.containsKey("project.name")) {
            setProperty("project.name", prjDir.getName());
        }

    }

    public Object getObject(String key){
        return appProps.get(key);
    }

    public synchronized Object setProperty(String key, Object value){
        return appProps.put(key, value);
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        if(key == null) return defaultValue;
        Object val = appProps.get(key);
        if(val instanceof Boolean){
            return (Boolean) val;
        }
        if(val instanceof String){
            String b = variableResolver.resolve((String)val);

            if("true".equalsIgnoreCase(b) || "on".equalsIgnoreCase(b)
                    || "yes".equalsIgnoreCase(b) || "1".equalsIgnoreCase(b)){
                return Boolean.TRUE;
            }

            if("false".equalsIgnoreCase(b) || "off".equalsIgnoreCase(b)
                    || "no".equalsIgnoreCase(b) || "0".equalsIgnoreCase(b)){
                return Boolean.FALSE;
            }
        }

        return defaultValue;
    }

    public String getString(String name){
        return getString(name, null);
    }

    public String getString(String key, String defaultValue){
        if(key == null) return defaultValue;
        Object val = appProps.get(key);

        if(val instanceof String){
            return variableResolver.resolve(String.valueOf(val));
        }

        return defaultValue;
    }


    public Long getLong(String key){
        return getLong(key, null);
    }

    public Long getLong(String key, Long defaultValue){
        if(key == null) return defaultValue;
        Object val = appProps.get(key);

        if(val instanceof Long){
            return (Long)val;
        }

        if(val instanceof String){
            try{
                String resolvedVal = variableResolver.resolve(String.valueOf(val));
                return Long.parseLong(resolvedVal);
            }catch (NumberFormatException e){
                return defaultValue;
            }
        }

        return defaultValue;
    }


    public Integer getInt(String key){
        return getInt(key, null);
    }

    public Integer getInt(String key, Integer defaultValue){
        if(key == null) return defaultValue;
        Object val = appProps.get(key);

        if(val instanceof Integer){
            return (Integer)val;
        }
        else if(val instanceof String){
            try{
                String resolvedVal = variableResolver.resolve(String.valueOf(val));
                return Integer.parseInt(resolvedVal);
            }catch (NumberFormatException e){
                return defaultValue;
            }
        }

        return defaultValue;
    }

    public Object removeProperty(String key){
        return appProps.remove(key);
    }

    public void addBundle(String environmentId) {
        String configPath = getString(ApplicationKeys.PNX_ENVIRONMENT_PATH);

        if(configPath == null) return;
        if(configPath.endsWith("\\") || configPath.endsWith("/")){
            configPath = configPath.substring(0, configPath.length()-1);
        }

        setProperty(ApplicationKeys.PNX_ENVIRONMENT_PATH, configPath);

        File envFile = new File(configPath, environmentId + ".env.properties");
        if(envFile.exists() && envFile.isFile()){
            loadFile(envFile);
        }
    }

    public void loadFile(File propFile){
        try{
            InputStream fileInputStream = new FileInputStream(propFile);
            appProps.load(fileInputStream);
            fileInputStream.close();
        } catch (IOException fe){
            logger.log(Level.WARNING, "Unable to load " + propFile.getAbsolutePath() + "!", fe);
        }
    }

    public void loadResourceFile(String propFile){
        try{
            InputStream fileInputStream  = getClass().getClassLoader().getResourceAsStream(propFile);
            appProps.load(fileInputStream);
            if(fileInputStream != null){
                fileInputStream.close();
            }
        } catch (IOException fe){
            logger.log(Level.WARNING, "Unable to load " + propFile+ "from resource directory!", fe);
        }
    }

    public Set<Object> keySet(){
        return appProps.keySet();
    }


}
