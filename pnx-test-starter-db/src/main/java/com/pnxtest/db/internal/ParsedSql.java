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

import java.util.ArrayList;
import java.util.List;

class ParsedSql {
    static final String POSITIONAL_PARAM = "?";

    private final String sql;
    private final ParsedParameters parameters;

    private ParsedSql(String sql, ParsedParameters parameters) {
        this.sql = sql;
        this.parameters = parameters;
    }


    public String getSql() {
        return sql;
    }

    public ParsedParameters getParameters() {
        return parameters;
    }

    public static ParsedSql of(String sql, ParsedParameters parameters) {
        return new ParsedSql(sql, parameters);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Builder() {}

        private final StringBuilder sql = new StringBuilder();
        private boolean positional = false;
        private boolean named = false;
        private final List<String> parameterNames = new ArrayList<>();


        public Builder append(String sqlFragment) {
            sql.append(sqlFragment);
            return this;
        }


        public Builder appendPositionalParameter() {
            positional = true;
            parameterNames.add(POSITIONAL_PARAM);
            return append(POSITIONAL_PARAM);
        }


        public Builder appendNamedParameter(String name) {
            named = true;
            parameterNames.add(name);
            return append(POSITIONAL_PARAM);
        }

        public ParsedSql build() {
            if (positional && named) {
                throw new PnxSqlException(
                        "Cannot mix named and positional parameters in a SQL statement: " + parameterNames);
            }

            ParsedParameters parameters = new ParsedParameters(positional, parameterNames);

            return new ParsedSql(sql.toString(), parameters);
        }
    }



}
