/*
 Copyright (c) 2021-2022
 This file is part of PnxTest framework.

 PnxTest is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero Public License version 3 as
 published by the Free Software Foundation

 PnxTest is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero Public License for more details.

 You should have received a copy of the GNU Affero Public License
 along with PnxTest.  If not, see <http://www.gnu.org/licenses/>.

 For more information, please contact the author at this address:
 chen.baker@gmail.com
 */
package com.pnxtest.db;

import com.pnxtest.db.api.BatchQuery;
import com.pnxtest.db.api.SelectQuery;
import com.pnxtest.db.api.UpdateQuery;
import com.pnxtest.db.internal.*;

public class PnxSql {

    private PnxSql(){}

    public static SelectQuery select(String sql){
        return new SelectQueryImpl(sql);
    }

    public static SelectQuery select(String sql, DbConfig dbConfig){
        return new SelectQueryImpl(sql, dbConfig);
    }


    public static UpdateQuery update(String sql){
        return new UpdateQueryImpl(sql);
    }

    public static UpdateQuery update(String sql, DbConfig dbConfig){
        return new UpdateQueryImpl(sql, dbConfig);
    }

    public static UpdateQuery insert(String sql){
        return new InsertQueryImpl(sql);
    }

    public static UpdateQuery insert(String sql, DbConfig dbConfig){
        return new InsertQueryImpl(sql, dbConfig);
    }

    public static UpdateQuery delete(String sql){
        return new DeleteQueryImpl(sql);
    }

    public static UpdateQuery delete(String sql, DbConfig dbConfig){
        return new DeleteQueryImpl(sql, dbConfig);
    }

    public static BatchQuery batch(String sql){
        return new BatchQueryImpl(sql);
    }

    public static BatchQuery batch(String sql, DbConfig dbConfig){
        return new BatchQueryImpl(sql, dbConfig);
    }

}
