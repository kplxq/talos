/**
 * Kaixindai Financing Services Jiangsu Co., Ltd.
 * Copyright (c) 2012-2016 All Rights Reserved.
 */
package com.kxd.framework.hbase.demo;

import java.util.Date;

import com.kxd.framework.hbase.anno.HBaseColumn;
import com.kxd.framework.hbase.anno.HBaseTable;
import com.kxd.framework.hbase.core.HBaseEntity;

/**
 * 请输入功能描述
 * 
 * @author lhldyf 2016年10月8日
 */
@HBaseTable(name = "testlh1", defaultFamily = "default")
public class TestHBaseDmo extends HBaseEntity {

    @HBaseColumn(family = "id", qualifier = "id")
    private String  id;

    @HBaseColumn(family = "default", qualifier = "name")
    private String  name;

    @HBaseColumn(family = "default", qualifier = "dateValue")
    private Date    dateValue;

    @HBaseColumn(family = "default", qualifier = "intValue")
    private Integer intValue;

    @HBaseColumn(family = "default", qualifier = "longValue")
    private long    longValue;

    @HBaseColumn(family = "default", qualifier = "booleanValue")
    private boolean booleanValue;

    /**
     * @return the dateValue
     */
    public Date getDateValue() {
        return dateValue;
    }

    /**
     * @param dateValue the dateValue to set
     */
    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    /**
     * @return the intValue
     */
    public Integer getIntValue() {
        return intValue;
    }

    /**
     * @param intValue the intValue to set
     */
    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }

    /**
     * @return the longValue
     */
    public long getLongValue() {
        return longValue;
    }

    /**
     * @param longValue the longValue to set
     */
    public void setLongValue(long longValue) {
        this.longValue = longValue;
    }

    /**
     * @return the booleanValue
     */
    public boolean isBooleanValue() {
        return booleanValue;
    }

    /**
     * @param booleanValue the booleanValue to set
     */
    public void setBooleanValue(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getRowKey() {
        return id.getBytes();
    }

    public String toString() {
        StringBuffer bf = new StringBuffer();
        bf.append("id:").append(id).append(",name:").append(name).append(",rowInfo:").append(super.getRow());
        return bf.toString();
    }

    public boolean equals(TestHBaseDmo dmo) {
        boolean eq = true;
        eq = this.id.equals(dmo.getId()) && this.name.equals(dmo.getName());
        if (!eq) {
            return false;
        }

        if (null != dmo.getRow() && null != this.getRow()) {
            if (dmo.getRow().size() != this.getRow().size()) {
                return false;
            } else {
                for (String key : dmo.getRow().keySet()) {
                    if (!dmo.getRow().get(key).equals(this.getRow().get(key))) {
                        return false;
                    }
                }
                return true;
            }
        } else if (null == dmo.getRow() && null == this.getRow()) {
            return true;
        } else {
            return false;
        }
    }

}
