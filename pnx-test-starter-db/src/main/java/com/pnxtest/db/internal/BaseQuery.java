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

import com.pnxtest.db.DbConfig;
import com.pnxtest.db.api.Query;
import lexer.ColonStatementLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import static lexer.ColonStatementLexer.*;
import static org.antlr.v4.runtime.Token.EOF;

abstract class BaseQuery<R extends Query> implements Query<R> {
    private Map<String, Object> nameParams = new HashMap<>();
    private Map<Integer, Object> positionParams = new HashMap<>();

    protected DbConfig dbConfig;
    protected String sql;

    BaseQuery(String sql, DbConfig dbConfig){
        this.sql = sql;
        this.dbConfig = dbConfig;
    }

    BaseQuery(String sql){
        this.sql = sql;
    }

    @Override
    public R bind(String name, Object value) {
        nameParams.putIfAbsent(name, value);
        return (R)this;
    }

    @Override
    public R bind(int positionIndex, Object value) {
        positionParams.putIfAbsent(positionIndex, value);
        return (R)this;
    }

    protected DataSource getCurrentDataSource(){
        DataSource ds;
        if(dbConfig == null){
            ds = ConnectionFactory.getDataSource();
            this.dbConfig = ConnectionFactory.getCurrentDbConfig();
        }else{
            ds = ConnectionFactory.createDataSource(dbConfig);
        }

        return ds;
    }

    protected ParsedSql parseBinder(){
        ParsedSql.Builder parsedSqlBuilder = ParsedSql.builder();
        ColonStatementLexer lexer = new ColonStatementLexer(CharStreams.fromString(sql));
        //lexer.addErrorListener(new ErrorListener());
        Token t = lexer.nextToken();
        while (t.getType() != EOF) {
            switch (t.getType()) {
                case COMMENT:
                case LITERAL:
                case QUOTED_TEXT:
                case DOUBLE_QUOTED_TEXT:
                    parsedSqlBuilder.append(t.getText());
                    break;
                case NAMED_PARAM:
                    parsedSqlBuilder.appendNamedParameter(t.getText().substring(1));
                    break;
                case POSITIONAL_PARAM:
                    parsedSqlBuilder.appendPositionalParameter();
                    break;
                case ESCAPED_TEXT:
                    parsedSqlBuilder.append(t.getText().substring(1));
                    break;
                default:
                    break;
            }
            t = lexer.nextToken();
        }


        return parsedSqlBuilder.build();
    }

    protected void setPstBinding(ParsedSql parsedSql, PreparedStatement pst){
        ParsedParameters parsedParameters = parsedSql.getParameters();
        List<String> paramPlaceHolders = parsedParameters.getParameterNames();

        for(int idx=0;idx<paramPlaceHolders.size();idx++){
            Object v;
            if(parsedParameters.isPositional()){
                v = positionParams.get((idx+1));
            }else{
                v = nameParams.get(paramPlaceHolders.get(idx));
            }
            try {
                pst.setObject(idx+1, v);
            }catch (SQLException ex){
                throw new PnxSqlException(ex);
            }
        }
    }

    protected String getDescription(){
        StringBuilder sb = new StringBuilder(this.sql);
        sb.append("\n");
        if(!this.positionParams.isEmpty()){
            sb.append(this.positionParams).append("\n");
        }

        if(!this.nameParams.isEmpty()){
            sb.append(this.nameParams).append("\n");
        }

        return sb.toString();
    }

}
