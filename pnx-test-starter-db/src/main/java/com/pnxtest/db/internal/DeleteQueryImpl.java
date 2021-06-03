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
package com.pnxtest.db.internal;

import com.pnxtest.core.steps.PnxSteps;
import com.pnxtest.db.DbConfig;
import com.pnxtest.db.api.UpdateQuery;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteQueryImpl extends BaseQuery<UpdateQuery> implements UpdateQuery{
    public DeleteQueryImpl(String sql){
        super(sql);
    }

    public DeleteQueryImpl(String sql, DbConfig dbConfig){
        super(sql, dbConfig);
    }


    @Override
    public int execute() {
        DataSource ds = getCurrentDataSource();
        ParsedSql parsedSql = parseBinder();
        //delete
        PnxSteps.start(parsedSql.getSql(), "SQL", "Delete");
        String subKey = parsedSql.getSql().substring(0, 6).toLowerCase();
        if(!subKey.equalsIgnoreCase("delete")){
            PnxSteps.success(getDescription() + "\nno any records affected due to wrong sql query");
            PnxSteps.end();
            return 0;
        }

        try(Connection conn = ds.getConnection();
            PreparedStatement pst = conn.prepareStatement(parsedSql.getSql())){
            setPstBinding(parsedSql, pst);
            int updatedCount = pst.executeUpdate();
            PnxSteps.success(getDescription() + "\n" + updatedCount + " records deleted");
            PnxSteps.end();
            return updatedCount;
        }catch (SQLException ex){
            throw new PnxSqlException("Database connection or implement failed: " + ds.toString(), ex);
        }
    }


}
