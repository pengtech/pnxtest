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
import com.pnxtest.db.api.Column;
import com.pnxtest.db.api.Entity;
import com.pnxtest.db.api.SelectQuery;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.*;
import java.time.*;
import java.util.*;

public class SelectQueryImpl extends BaseQuery<SelectQuery> implements SelectQuery {

    public SelectQueryImpl(String sql){
        super(sql);
    }

    public SelectQueryImpl(String sql, DbConfig dbConfig){
        super(sql, dbConfig);
    }

    @Override
    public Optional<Map<String,Object>> asOne(){
        DataSource ds = getCurrentDataSource();
        ParsedSql parsedSql = parseBinder();
        PnxSteps.start(parsedSql.getSql(), "SQL", "Select");

        String subKey = parsedSql.getSql().substring(0, 6).toLowerCase();


        if(!subKey.equalsIgnoreCase("select")){
            PnxSteps.success(getDescription() + "\nno any records retrieved due to wrong sql query");
            PnxSteps.end();
            return Optional.empty();
        }

        try(Connection conn = ds.getConnection();
            PreparedStatement pst = conn.prepareStatement(parsedSql.getSql())){
            setPstBinding(parsedSql, pst);
            ResultSet rs = pst.executeQuery();

            if(rs.next()){
                PnxSteps.success(getDescription() + "\none record retrieved");
                PnxSteps.end();
                return Optional.of(getRowAsMap(rs));
            }

            PnxSteps.success(getDescription() + "\nno any records retrieved");
            PnxSteps.end();
            return Optional.empty();
        }catch (SQLException ex){
            throw new PnxSqlException(ex);
        }

    }

    @Override
    public List<Map<String,Object>> asList(){
        DataSource ds = getCurrentDataSource();
        ParsedSql parsedSql = parseBinder();
        PnxSteps.start(parsedSql.getSql(), "SQL", "Select");

        String subKey = parsedSql.getSql().substring(0, 6).toLowerCase();
        if(!subKey.equalsIgnoreCase("select")){
            PnxSteps.success(getDescription() + "\nno any records retrieved due to wrong sql query");
            PnxSteps.end();
            return new ArrayList<>();
        }

        try(Connection conn = ds.getConnection();
            PreparedStatement pst = conn.prepareStatement(parsedSql.getSql())){
            setPstBinding(parsedSql, pst);
            ResultSet rs = pst.executeQuery();

            List<Map<String, Object>> ret = new ArrayList<>();
            while(rs.next()){
                ret.add(getRowAsMap(rs));
            }
            PnxSteps.success(getDescription() + "\n" + ret.size() + " records retrieved");
            PnxSteps.end();
            return ret;
        }catch (SQLException ex){
            throw new PnxSqlException(ex);
        }
    }


    @Override
    public <T> Optional<T> asOne(Class<T> entityClass) {
        DataSource ds = getCurrentDataSource();
        ParsedSql parsedSql = parseBinder();
        PnxSteps.start(parsedSql.getSql(), "SQL", "Select");

        String subKey = parsedSql.getSql().substring(0, 6).toLowerCase();
        if(!subKey.equalsIgnoreCase("select")){
            PnxSteps.success(getDescription() + "\nno any records retrieved due to wrong sql query");
            PnxSteps.end();
            return Optional.empty();
        }

        try(Connection conn = ds.getConnection();
            PreparedStatement pst = conn.prepareStatement(parsedSql.getSql())){
            setPstBinding(parsedSql, pst);

            ResultSet rs = pst.executeQuery();

            if(rs.next()){
                PnxSteps.success(getDescription() + "\none record retrieved");
                PnxSteps.end();
                return Optional.of(getRow(rs, entityClass));
            }

            PnxSteps.success(getDescription() + "\nno any records retrieved");
            PnxSteps.end();
            return Optional.empty();
        }catch (SQLException ex){
            throw new PnxSqlException(ex);
        }
    }

    @Override
    public <T> List<T> asList(Class<T> entityClass) {
        DataSource ds = getCurrentDataSource();
        ParsedSql parsedSql = parseBinder();
        PnxSteps.start(parsedSql.getSql(), "SQL", "Select");

        String subKey = parsedSql.getSql().substring(0, 6).toLowerCase();
        if(!subKey.equalsIgnoreCase("select")){
            PnxSteps.success(getDescription() + "\nno any records retrieved due to wrong sql query");
            PnxSteps.end();
            return new ArrayList<T>();
        }

        try(Connection conn = ds.getConnection();
            PreparedStatement pst = conn.prepareStatement(parsedSql.getSql())){

            setPstBinding(parsedSql, pst);
            ResultSet rs = pst.executeQuery();
            List<T> ret = new ArrayList<T>();
            while (rs.next()) {
                T bean = getRow(rs, entityClass);
                ret.add(bean);
            }

            PnxSteps.success(getDescription() + "\n" + ret.size() + " records retrieved");
            PnxSteps.end();
            return ret;
        }catch (SQLException ex){
            throw new PnxSqlException(ex);
        }
    }

    private <T> T getRow(ResultSet rs, Class<T> clazz){
        if(rs == null){
            throw new PnxSqlException("ResultSet is null");
        }

        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new PnxSqlException("No '@Entity' annotation found on class: " + clazz.getName());
        }

        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();//total columns
            Field[] fields = clazz.getDeclaredFields();

            T bean = clazz.newInstance();
            for (int idx = 1; idx <= columnCount; idx++) {
                String sqlColumnName = metaData.getColumnName(idx);
                Object sqlColumnValue = rs.getObject(idx);

                for (Field field : fields) {
                    if (!field.isAnnotationPresent(Column.class)) continue;

                    Column column = field.getAnnotation(Column.class);
                    if (!column.name().equalsIgnoreCase(sqlColumnName) || sqlColumnValue == null) continue;

                    Class<?> fieldType = field.getType();
                    //basic types
                    if (fieldType.isAssignableFrom(String.class)) {
                        this.setFieldProperty(bean, field.getName(), rs.getString(idx));
                    } else if (fieldType.isAssignableFrom(byte.class) || fieldType.isAssignableFrom(Byte.class)) {
                        this.setFieldProperty(bean, field.getName(), rs.getByte(idx));
                    } else if (fieldType.isAssignableFrom(short.class) || fieldType.isAssignableFrom(Short.class)) {
                        this.setFieldProperty(bean, field.getName(), rs.getShort(idx));
                    } else if (fieldType.isAssignableFrom(int.class) || fieldType.isAssignableFrom(Integer.class)) {
                        this.setFieldProperty(bean, field.getName(), rs.getInt(idx));
                    } else if (fieldType.isAssignableFrom(long.class) || fieldType.isAssignableFrom(Long.class)) {
                        this.setFieldProperty(bean, field.getName(), rs.getLong(idx));
                    } else if (fieldType.isAssignableFrom(float.class) || fieldType.isAssignableFrom(Float.class)) {
                        this.setFieldProperty(bean, field.getName(), rs.getFloat(idx));
                    } else if (fieldType.isAssignableFrom(double.class) || fieldType.isAssignableFrom(Double.class)) {
                        this.setFieldProperty(bean, field.getName(), rs.getDouble(idx));
                    } else if (fieldType.isAssignableFrom(BigDecimal.class)) {
                        this.setFieldProperty(bean, field.getName(), rs.getBigDecimal(idx));
                    } else if (fieldType.isAssignableFrom(boolean.class) || fieldType.isAssignableFrom(Boolean.class)) {
                        this.setFieldProperty(bean, field.getName(), rs.getBoolean(idx));
                    }
                    //sql date&time
                    else if (fieldType.isAssignableFrom(java.sql.Date.class)) {
                        this.setFieldProperty(bean, field.getName(), rs.getDate(idx));
                    } else if (fieldType.isAssignableFrom(Timestamp.class)) {
                        this.setFieldProperty(bean, field.getName(), rs.getTimestamp(idx));
                    } else if (fieldType.isAssignableFrom(Time.class)) {
                        this.setFieldProperty(bean, field.getName(), rs.getTime(idx));
                    }
                    //java data&time
                    else if (fieldType.isAssignableFrom(java.util.Date.class)) {
                        Timestamp timestamp = rs.getTimestamp(idx);
                        this.setFieldProperty(bean, field.getName(), timestamp == null ? null : new java.sql.Date(timestamp.getTime()));
                    } else if (fieldType.isAssignableFrom(LocalDate.class)) {
                        java.sql.Date date = rs.getDate(idx);
                        this.setFieldProperty(bean, field.getName(), date == null ? null : date.toLocalDate());
                    } else if (fieldType.isAssignableFrom(LocalTime.class)) {
                        Time time = rs.getTime(idx);
                        this.setFieldProperty(bean, field.getName(), time == null ? null : time.toLocalTime());
                    } else if (fieldType.isAssignableFrom(LocalDateTime.class)) {
                        Timestamp stamp = rs.getTimestamp(idx);
                        this.setFieldProperty(bean, field.getName(), stamp == null ? null : stamp.toLocalDateTime());
                    } else if (fieldType.isAssignableFrom(Year.class)) {
                        java.sql.Date date = rs.getDate(idx);
                        this.setFieldProperty(bean, field.getName(), date == null ? null : Year.from(date.toLocalDate()));
                    } else if (fieldType.isAssignableFrom(YearMonth.class)) {
                        java.sql.Date date = rs.getDate(idx);
                        this.setFieldProperty(bean, field.getName(), date == null ? null : YearMonth.from(date.toLocalDate()));
                    } else if (fieldType.isAssignableFrom(Instant.class)) {
                        Timestamp stamp = rs.getTimestamp(idx);
                        this.setFieldProperty(bean, field.getName(), stamp == null ? null : stamp.toInstant());
                    }
                    //binary
                    else if (fieldType.isAssignableFrom(byte[].class)) {
                        byte[] data = rs.getBytes(idx);
                        this.setFieldProperty(bean, field.getName(), data);
                    } else {
                        this.setFieldProperty(bean, field.getName(), rs.getObject(idx));
                    }
                }
            }

            return bean;
        }catch (SQLException | InstantiationException | IllegalAccessException ex){
            throw new PnxSqlException(ex);
        }
    }


    private void setFieldProperty(Object clazz, String fieldName, Object columnValue) {
        try {
            Field field = clazz.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(clazz, columnValue);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            //e.printStackTrace();
        }
    }

    private Map<String, Object> getRowAsMap(ResultSet rs){
        Map<String,Object> ret = new HashMap<>();
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();//total columns
            for(int idx=1;idx<=columnCount;idx++){
                String columnName = metaData.getColumnLabel(idx);
                Object columnValue;
                int type = metaData.getColumnType(idx);
                java.util.Calendar cal = Calendar.getInstance();
                //cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                if(dbConfig !=null && dbConfig.getTimeZone() !=null) {
                    cal.setTimeZone(TimeZone.getTimeZone(dbConfig.getTimeZone()));
                }
                switch (type) {
                    case Types.DATE:
                        columnValue = rs.getDate(columnName,cal);
                        break;
                    case Types.TIME:
                        columnValue = rs.getTime(columnName, cal);
                        break;
                    case Types.TIMESTAMP:
                        columnValue = rs.getTimestamp(columnName, cal);
                        break;
                    case Types.BLOB:
                        columnValue = rs.getBytes(columnName);
                        break;
                    case Types.CLOB:
                        columnValue = rs.getString(columnName);
                        break;
                    default:
                        columnValue = rs.getObject(columnName);
                        break;
                }
                ret.putIfAbsent(columnName, columnValue);
            }

        }catch (SQLException ex){
            throw new PnxSqlException(ex);
        }

        return  ret;
    }




}
