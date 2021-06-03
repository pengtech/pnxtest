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

import com.pnxtest.core.environment.PnxContext;
import com.pnxtest.db.DbConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

class ConnectionFactory {
    private static DataSource dataSource;
    private static DbConfig currentDbConfig;
    private static final Object mutex = new Object();
    private static final int DEFAULT_TIMEOUT = 10; //connection timeout
    private static final String DEFAULT_TIMEZONE = "GMT";

    private ConnectionFactory(){}

    public static synchronized DataSource getDataSource(){
        DataSource result = dataSource;
        if (result == null) {
            synchronized (mutex) {
                result = dataSource;
                if (result == null) {
                    dataSource = result = createDataSource();
                }
            }
        }
        return result;
    }




    private static DataSource createDataSource() {
        DbConfig dbConfig = new DbConfig();
        dbConfig.setDriver(PnxContext.getString("pnx.db.driver", null));
        dbConfig.setUrl(PnxContext.getString("pnx.db.url", ""));
        dbConfig.setUser(PnxContext.getString("pnx.db.user", null));
        dbConfig.setPassword(PnxContext.getString("pnx.db.password", null));
        dbConfig.setTimeout(PnxContext.getInt("pnx.db.timeout", DEFAULT_TIMEOUT));
        dbConfig.setTimeZone(PnxContext.getString("pnx.db.timezone", DEFAULT_TIMEZONE));
        currentDbConfig = dbConfig;

        return createDataSource(dbConfig);

    }

    public static DataSource createDataSource(DbConfig dbConfig) {
        HikariDataSource ds = new HikariDataSource();
        if(dbConfig.getUrl() != null){
            ds.setJdbcUrl(dbConfig.getUrl());
        }

        if(dbConfig.getUser() != null){
            ds.setUsername(dbConfig.getUser());
        }

        if(dbConfig.getPassword() != null){
            ds.setPassword(dbConfig.getPassword());
        }

        if(dbConfig.getDriver() != null){
            ds.setDriverClassName(dbConfig.getDriver());
        }

        if(dbConfig.getTimeout()>0){
            ds.setConnectionTimeout(dbConfig.getTimeout()*1000);
        }

        if(dbConfig.getTimeZone() == null){
            dbConfig.setTimeZone(DEFAULT_TIMEZONE);
        }

        return ds;
    }

    public static DbConfig getCurrentDbConfig() {
        return currentDbConfig;
    }
}
