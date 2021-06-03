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

package com.pnxtest.core.assertions;

import org.hamcrest.Matchers;
import org.hamcrest.io.FileMatchers;

import java.io.File;

public class FileAssert extends AbstractBaseAssert<FileAssert, File>{


    FileAssert(File actual) {
        super(actual, FileAssert.class);
    }

    public FileAssert isDirectory(){
        setKeywords("isDirectory");
        assertThat(FileMatchers.anExistingDirectory());
        return this;
    }

    public FileAssert isFile(){
        setKeywords("isFile");
        assertThat(FileMatchers.anExistingFile());
        return this;
    }

    public FileAssert isFileOrDirectory(){
        setKeywords("IsFileOrDirectory");
        assertThat(FileMatchers.anExistingFileOrDirectory());
        return this;
    }

    public FileAssert isNamed(String fileName){
        setKeywords("fileIsNamed");
        assertThat(FileMatchers.aFileNamed(Matchers.equalTo(fileName)));
        return this;
    }

    public FileAssert hasSize(long fileSize){
        setKeywords("fileHasSize");
        assertThat(FileMatchers.aFileWithSize(fileSize));
        return this;
    }



}
