package com.kxd.framework.hbase.core.config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.util.Bytes;

import com.kxd.framework.hbase.anno.HBaseTable;
import com.kxd.framework.hbase.exception.SkyHBaseException;
import com.kxd.framework.hbase.util.HBaseUtil;

/**
 * POJO type and Hbase table mapping info.
 * 
 * @author xinzhi
 * */
public class TypeInfo {

    /**
     * Parse TypeInfo from POJO's type.
     * 
     * @param type POJO's type.
     * @return TypeInfo.
     * */
    public static TypeInfo parse(Class<?> type) {
        HBaseUtil.checkNull(type);

        TypeInfo typeInfo = new TypeInfo();
        typeInfo.type = type;

        Field[] fields = type.getDeclaredFields();

        HBaseTable hbaseTable = type.getAnnotation(HBaseTable.class);
        if (hbaseTable != null) {
            typeInfo.defaultFamily = hbaseTable.defaultFamily();
            typeInfo.tableName = hbaseTable.name();
        }

        for (Field field : fields) {

            field.setAccessible(true);

            ColumnInfo columnInfo = ColumnInfo.parse(type, field);
            if (columnInfo == null) {
                continue;
            }

            typeInfo.columnInfos.add(columnInfo);

        }

        typeInfo.init();

        return typeInfo;
    }

    /** POJO's type. */
    private Class<?>                             type;

    /** POJO's ColumnInfo list. */
    private List<ColumnInfo>                     columnInfos    = new ArrayList<ColumnInfo>();

    /** Versioned ColumnInfo, can be null. */
    private ColumnInfo                           versionedColumnInfo;

    /** default family **/
    private String                               defaultFamily;

    /** table name **/
    private String                               tableName;

    /**
     * Qualifier->Family-> ColumnInfo.
     * */
    private Map<String, Map<String, ColumnInfo>> columnInfosMap = new HashMap<String, Map<String, ColumnInfo>>();

    private TypeInfo() {
    }

    /**
     * Init this object.
     * */
    public void init() {

        HBaseUtil.checkNull(type);
        HBaseUtil.checkNull(columnInfos);

        int versionFieldCounter = 0;

        for (ColumnInfo columnInfo : columnInfos) {

            if (columnInfo.isVersioned) {
                versionFieldCounter++;
                versionedColumnInfo = columnInfo;
            }

            if (!columnInfosMap.containsKey(columnInfo.qualifier)) {
                columnInfosMap.put(columnInfo.qualifier, new HashMap<String, ColumnInfo>());
            }
            columnInfosMap.get(columnInfo.qualifier).put(columnInfo.family, columnInfo);
        }

        if (versionFieldCounter > 1) {
            throw new SkyHBaseException("more than one versioned fields.");
        }

    }

    /**
     * Is versioned TypeInfo.
     * */
    public boolean isVersionedType() {
        return versionedColumnInfo != null;
    }

    /**
     * Find ColumnInfo by family and qualifier.
     * */
    public ColumnInfo findColumnInfo(String family, String qualifier) {
        HBaseUtil.checkEmptyString(family);
        HBaseUtil.checkEmptyString(qualifier);

        return columnInfosMap.get(qualifier).get(family);
    }

    public Class<?> getType() {
        return type;
    }

    public List<ColumnInfo> getColumnInfos() {
        return columnInfos;
    }

    public ColumnInfo getVersionedColumnInfo() {
        return versionedColumnInfo;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-----------------" + getClass() + "-----------------------\n");
        sb.append("type=" + type + "\n");
        sb.append("versionedColumnInfo=" + versionedColumnInfo + "\n");
        for (ColumnInfo columnInfo : columnInfos) {
            sb.append(columnInfo + "\n");
        }
        sb.append("-----------------" + getClass() + "-----------------------\n");
        return sb.toString();
    }

    /**
     * @return the defaultFamily
     */
    public String getDefaultFamily() {
        return defaultFamily;
    }

    public byte[] getDefaultFamilyBytes() {
        return defaultFamily == null ? null : Bytes.toBytes(defaultFamily);
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
