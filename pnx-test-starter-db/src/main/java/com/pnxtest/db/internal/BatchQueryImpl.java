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
import com.pnxtest.db.api.BatchQuery;
import lexer.ColonStatementLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;
import static lexer.ColonStatementLexer.*;
import static org.antlr.v4.runtime.Token.EOF;

public class BatchQueryImpl implements BatchQuery {
    private Optional<Iterator<List<?>>> values = empty();
    private Optional<Iterator<Map<String, ?>>> namedValues = empty();

    private DbConfig dbConfig;
    private final String sql;

    public BatchQueryImpl(String sql, DbConfig dbConfig){
        this.sql = sql;
        this.dbConfig = dbConfig;
    }


    public BatchQueryImpl(String sql){
        this.sql = sql;
        this.dbConfig = null;
    }


    @Override
    public BatchQuery values(Iterator<List<?>> values) {
        this.values = Optional.of(values);
        return this;
    }

    @Override
    public BatchQuery values(Iterable<List<?>> values) {
        return values(values.iterator());
    }

    @Override
    public BatchQuery namedValues(Iterator<Map<String, ?>> namedValues) {
        this.namedValues = Optional.of(namedValues);
        return this;
    }

    @Override
    public BatchQuery namedValues(Iterable<Map<String, ?>> namedValues) {
        return namedValues(namedValues.iterator());
    }


    @Override
    public int[] execute() {
        if(!values.isPresent() && !namedValues.isPresent()){
            return new int[]{};
        }

        DataSource ds = getCurrentDataSource();
        ParsedSql parsedSql = parseBinder();
        //batch insert or update
        PnxSteps.start(parsedSql.getSql(), "SQL", "Batch");
        String subKey = parsedSql.getSql().substring(0, 6).toLowerCase();
        if(!subKey.equalsIgnoreCase("insert") && !subKey.equalsIgnoreCase("update")){
            PnxSteps.success(this.sql + "\nno any records affected due to wrong sql query");
            PnxSteps.end();
            return new int[]{};
        }

        try(Connection conn = ds.getConnection();
            PreparedStatement pst = conn.prepareStatement(parsedSql.getSql())){

            ParsedParameters parsedParameters = parsedSql.getParameters();
            List<String> paramPlaceHolders = parsedParameters.getParameterNames();
            if(parsedParameters.isPositional() && values.isPresent()){
                Iterator<List<?>> iterator = values.get();
                while (iterator.hasNext()){
                    List<?> rowData = iterator.next();
                    for(int idx=0;idx<paramPlaceHolders.size();idx++){
                        Object v = rowData.get(idx);
                        try {
                            pst.setObject(idx+1, v);
                        }catch (SQLException ex){
                            throw new PnxSqlException(ex);
                        }
                    }

                    pst.addBatch();
                }
                int[] updatedCount = pst.executeBatch();
                PnxSteps.success(this.sql + "\n" + updatedCount.length + " records implemented");
                PnxSteps.end();
                return updatedCount;

            }

            if(!parsedParameters.isPositional() && namedValues.isPresent()){
                Iterator<Map<String, ?>> iterator = namedValues.get();
                while (iterator.hasNext()){
                    Map<String, ?> rowData = iterator.next();
                    for(int idx=0;idx<paramPlaceHolders.size();idx++){
                        Object v = rowData.get(paramPlaceHolders.get(idx));
                        try {
                            pst.setObject(idx+1, v);
                        }catch (SQLException ex){
                            throw new PnxSqlException(ex);
                        }
                    }

                    pst.addBatch();
                }

                int[] updatedCount = pst.executeBatch();
                PnxSteps.success(this.sql + "\n" + updatedCount.length + " records implemented");
                PnxSteps.end();
                return updatedCount;
            }

            PnxSteps.success(this.sql + "\n no records implemented");
            PnxSteps.end();
            return new int[]{};

        }catch (SQLException ex){
            throw new PnxSqlException("Database connection or implement failed: " + ds.toString(), ex);
        }

    }

    protected DataSource getCurrentDataSource(){
        DataSource ds;
        if(dbConfig == null){
            ds = ConnectionFactory.getDataSource();
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


}
