package com.kxd.framework.hbase.core.config;

import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.util.Bytes;

import com.kxd.framework.hbase.anno.HBaseColumn;
import com.kxd.framework.hbase.anno.HBaseTable;
import com.kxd.framework.hbase.exception.SkyHBaseException;
import com.kxd.framework.hbase.util.HBaseUtil;

/**
 * POJO's field and Hbase table's column mapping info.
 * 
 * @author xinzhi
 * */
public class ColumnInfo {


    /**
     * parse column info in air.
     * */
    static ColumnInfo parseInAir(Class<?> type, Field field, String family) {

        HBaseUtil.checkEmptyString(family);
        // use field name as qualifier.
        String qualifier = field.getName();

        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.type = type;
        columnInfo.field = field;
        columnInfo.family = family;
        columnInfo.familyBytes = Bytes.toBytes(family);
        columnInfo.qualifier = qualifier;
        columnInfo.qualifierBytes = Bytes.toBytes(qualifier);

        return columnInfo;

    }

    /**
     * Parse ColumnInfo from POJO's field.
     * 
     * @param type POJO's class type.
     * @param field POJO' field.
     * @return ColumnInfo.
     * */
    static ColumnInfo parse(Class<?> type, Field field) {
        String defaultFamily = null;

        HBaseTable hbaseTable = type.getAnnotation(HBaseTable.class);
        if (hbaseTable != null) {
            defaultFamily = hbaseTable.defaultFamily();
        }

        HBaseColumn hbaseColumn = field.getAnnotation(HBaseColumn.class);
        if (hbaseColumn == null) {
            return null;
        }

        String family = hbaseColumn.family();
        String qualifier = hbaseColumn.qualifier();

        if (StringUtils.isEmpty(family)) {
            family = defaultFamily;
        }

        if (StringUtils.isEmpty(family)) {
            throw new SkyHBaseException("family is null or empty. type=" + type + " field=" + field);
        }

        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.type = type;
        columnInfo.field = field;
        columnInfo.family = family;
        columnInfo.familyBytes = Bytes.toBytes(family);
        columnInfo.qualifier = qualifier;
        columnInfo.qualifierBytes = Bytes.toBytes(qualifier);

        return columnInfo;
    }

    /** POJO's class type. */
    public Class<?> type;

    /** POJO's field. */
    public Field    field;

    /** hbase's family. */
    public String   family;

    /** hbase's family bytes. */
    public byte[]   familyBytes;

    /** hbase's qualifier. */
    public String   qualifier;

    /** hbase's qualifier bytes. */
    public byte[]   qualifierBytes;

    /** isVersioned. */
    public boolean  isVersioned;

    private ColumnInfo() {
    }

    @Override
    public String toString() {
        return "type=" + type + " field=" + field + " family=" + family + " qualifier=" + qualifier + " isVersioned="
                + isVersioned;
    }
}