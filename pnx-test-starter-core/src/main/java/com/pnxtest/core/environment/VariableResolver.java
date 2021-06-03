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
import com.pnxtest.core.api.ICryptoConfig;

import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class VariableResolver {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static final String VAR_BEGIN = "$";
    private static final String KEY_PREFIX = "${";
    private static final String PIPE_SEPARATOR = "|"; //separates the key name to the default value

    public String resolve(String source){
        if (null == source) {
            return null;
        }

        //secret
        Pattern p = Pattern.compile("secret\\.(.+)$");
        //Pattern p = Pattern.compile("\\$\\{__secret\\((.+)\\)\\}");
        Matcher m = p.matcher(source);
        if(m.matches()){
            String encodedText = m.group(1);
            try{
                ICryptoConfig crypto = PnxContext.getBean(ApplicationKeys.PNX_CRYPTO_IMPL, ICryptoConfig.class);
                return crypto.decrypt(encodedText);
            }catch (Exception e){
                logger.log(Level.WARNING, "crypto error:", e);
            }
        }

        //${var}
        p = Pattern.compile("\\$\\{([\\w\\.\\-\\|]+)\\}");
        m = p.matcher(source);

        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String envVarName = m.group(1);

            //String envVarValue = System.getenv(envVarName);
            StringTokenizer keyTokenizer = new StringTokenizer(envVarName, PIPE_SEPARATOR);
            String key = keyTokenizer.nextToken().trim();
            String defaultValue = null;
            if (keyTokenizer.hasMoreTokens()) {
                defaultValue = keyTokenizer.nextToken().trim();
            }

            String envVarValue = PnxContext.getString(key, defaultValue);
            if(envVarValue != null){
                m.appendReplacement(sb, Matcher.quoteReplacement(envVarValue));
            }else{
                m.appendReplacement(sb, key);
            }
        }
        m.appendTail(sb);

        String ret = sb.toString();
        if(p.matcher(ret).matches()){
            return resolve(ret);
        }
        return ret;

    }


    public static void main(String[] args){
//        String pattern0 = "123";
//        String pattern1 = "$123";
//        String pattern2 = "${123";
        String pattern3 = "${appName}";
//        String pattern4 = "${123}88";
//        String pattern5 = "00${123}${456}";
//        String pattern6 = "${1${2}3}";
//        String pattern7 = "${1${23}}";
        //PnxContext.setProperty("123", 456);

        VariableResolver variableResolver = new VariableResolver();
//        System.out.println(variableResolver.resolve0(pattern0));
//        System.out.println(variableResolver.resolve0(pattern1));
//        System.out.println(variableResolver.resolve0(pattern2));
        System.out.println(variableResolver.resolve(pattern3));
//        System.out.println(variableResolver.resolve0(pattern4));
//        System.out.println(variableResolver.resolve0(pattern5));
//        System.out.println(variableResolver.resolve0(pattern6));
//        System.out.println(variableResolver.resolve0(pattern7));

    }

}
