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

import com.pnxtest.core.api.ICryptoConfig;
import com.pnxtest.core.util.StringUtil;

class DefaultCrypto implements ICryptoConfig {

    DefaultCrypto(){

    }

    public String decrypt(String cipherText){
        if(StringUtil.isEmpty(cipherText)) return "";
        StringBuilder sb = new StringBuilder();
        int len = cipherText.length();
        for(int i=0; i<len; i++){
            char c = cipherText.charAt(i);
            if(Character.isLetterOrDigit(c)){
                sb.append((char)(c-1));
            }else{
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public String encrypt(String plainText){
        if(StringUtil.isEmpty(plainText)) return "";
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<plainText.length(); i++){
            char c = plainText.charAt(i);
            if(Character.isLetterOrDigit(c)){
                sb.append((char)(c+1));
            }else{
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
