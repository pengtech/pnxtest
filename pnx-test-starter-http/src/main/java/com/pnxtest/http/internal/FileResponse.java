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

package com.pnxtest.http.internal;

import com.pnxtest.core.environment.PnxContext;
import com.pnxtest.http.api.RawResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.*;

class FileResponse extends BaseResponse<File> {
    private File body;

    public FileResponse(RawResponse r, String pathOrFilename) {
        super(r);
        try {
            //overwrite existing file, if exists
            CopyOption[] options = new CopyOption[]{
                    StandardCopyOption.REPLACE_EXISTING
            };

            Path target;
            if(!pathOrFilename.contains("/")){
                target = Paths.get(PnxContext.getTestResultLocation(), "download", pathOrFilename);
                File location = target.getParent().toFile();
                if(!location.exists()){
                    location.mkdirs();
                }
            }else{
                target = Paths.get(pathOrFilename);
            }
            Files.copy(r.getContent(), target, options);
            body = target.toFile();
        } catch (IOException e) {
            throw new HttpException(String.format("specified file store location<%s> does not exist, please create it firstly!", pathOrFilename), e);
        }
    }

    @Override
    public File getBody() {
        return body;
    }
}
