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

package com.pnxtest.core;

import com.pnxtest.core.exceptions.CmdArgsParseException;

import java.util.*;

/**
 * a simple console arguments parser class
 * @author nicolas.chen
 */
class CmdParser {
    private Map<String, List<String>> cmdParams = new HashMap<>();

    public CmdParser(){}


    public void parse(String[] args) throws CmdArgsParseException {
        List<String> options = null;
        for (int i = 0; i < args.length; i++) {
            final String arg = args[i];

            if (arg.charAt(0) == '-') {
                if (arg.length() < 2) {
                    throw new CmdArgsParseException(String.format("Error at argument: %s", arg));
                }

                options = new ArrayList<>();
                cmdParams.put(arg.substring(1), options);
            }else if(options !=null){
                options.add(arg);
            }else{
                throw new CmdArgsParseException("Illegal parameter usage");
            }
        }

        if(!cmdParams.isEmpty() && !cmdParams.containsKey("target")
                && !cmdParams.containsKey("env")
                && !cmdParams.containsKey("config")
                && !cmdParams.containsKey("outputting")
                && !cmdParams.containsKey("help")
        ){
            throw new CmdArgsParseException("Command usage error!");
        }

        if(cmdParams.containsKey("help")){
            printHelper();
            System.exit(1);
        }

//        if(!cmdParams.containsKey("target") ||
//                (cmdParams.containsKey("target") && cmdParams.get("target").size() == 0)){
//            //target file is required
//            throw new CmdArgsParseException("-target is required");
//        }


        if(cmdParams.containsKey("env") && cmdParams.get("env").size()==0){
            //if -env, must specify env id
            throw new CmdArgsParseException("Need specify environment Id after -env");
        }

        if(cmdParams.containsKey("config") && cmdParams.get("config").size()==0){
            //if -config, must specify config location
            throw new CmdArgsParseException("Need specify config location after -config");

        }

        if(cmdParams.containsKey("outputting") && cmdParams.get("outputting").size()==0){
            //if -out, must specify outputting location
            throw new CmdArgsParseException("Need specify outputting location after -out");
        }


    }

    public String getOptionValue(String key){
       return getOptionValue(key, null);
    }


    public String getOptionValue(String key, String defaultVal){
        List<String> vals = cmdParams.get(key);
        if(vals == null || vals.size() ==0){
            return defaultVal;
        }

        return String.join(" ", vals);
    }

    public void printHelper(){
        System.out.println("PnxTest commandline usage:");
        System.out.println("\t-target    \t<suiteFile>         \t[required] specify test suite file");
        System.out.println("\t-env       \t<EnvironmentId>     \t[optional] specify test environment ID");
        System.out.println("\t-config    \t<configLocation>    \t[optional] specify test config location");
        System.out.println("\t-outputting\t<outputtingLocation>\t[optional] specify test outputting location");
        System.out.println("\t-help      \t                     \tprint helper");
        System.out.println();
    }

}
