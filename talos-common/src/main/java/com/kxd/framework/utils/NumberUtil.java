/**
 * Copyright 2012-2017 Kaixindai Financing Services Jiangsu Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kxd.framework.utils;

import com.kxd.framework.lang.AppException;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * 
 * 由于Java的简单类型不能够精确的对浮点数进行运算，这个工具类提供精 确的浮点数运算，包括加减乘除和四舍五入。
 * 
 * @author chenr 2014年1月7日
 */

public class NumberUtil {

    // 默认除法运算精度
    private static final int   DEF_DIV_SCALE   = 10;
    public static final String NUMBERFORMAT_01 = "###,##0.00";
    public static final String NUMBERFORMAT_02 = "###,##0.##";
    public static final String NUMBERFORMAT_03 = "#0.00";
    public static final String NUMBERFORMAT_04 = "#0.##";
    public static final String NUMBERFORMAT_05 = "￥###,##0.00";
    public static final String NUMBERFORMAT_06 = "￥###,##0.##";
    public static final String NUMBERFORMAT_07 = "#0.0";
    public static final BigDecimal ONE_MILLION = new BigDecimal("10000");

    private NumberUtil() {

    }

    /**
     * 提供精确的加法运算。
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();

    }

    /**
     * 取三个double值之和(解决java中算术运算不精确)
     * @param d1 double值1
     * @param d2 double值1
     * @param d3 double值1
     * @return double
     */
    public static double add(double d1, double d2, double d3) {
        BigDecimal b1 = new BigDecimal(Double.toString(d1));
        BigDecimal b2 = new BigDecimal(Double.toString(d2));
        BigDecimal b3 = new BigDecimal(Double.toString(d3));
        return b1.add(b2).add(b3).doubleValue();
    }

    /**
     * 提供精确的减法运算。
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */

    public static double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供精确的乘法运算。
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static double mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
     * 小数点以后10位，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static double div(double v1, double v2) {
        return div(v1, v2, DEF_DIV_SCALE);
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
            "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 提供精确的小数位四舍五入处理。
     * @param v 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
            "The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        return b.setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }
    
	public static double toDouble(String str){
		if(str==null)
			return 0;
		try {
			return Double.parseDouble(str);
		} catch (Exception e) {
			return 0;
		}
	}

    /**
     * 格式化数据
     * @param dou 参数值
     * @param format 格式
     * @return
     */
    public static String formatSL(double dou, int scale, String format) {
        if (scale < 0)
            scale = 2;
        if (StringUtils.isEmpty(format)) {
            format = NUMBERFORMAT_04;
        }
        DecimalFormat df = new DecimalFormat(format);// 最多保留几位小数，就用几个#，最少位就用0来确定
        BigDecimal b = new BigDecimal(Double.toString(dou));
        return df.format(b.setScale(scale, BigDecimal.ROUND_FLOOR));
    }
    
    public static double parseDouble(String source,String format){
        DecimalFormat df = new DecimalFormat(format);
        try {
            Number number = df.parse(source);
            return number.doubleValue();
        } catch (Exception e){
            throw new AppException(e.getMessage());
        }
        
    }
    
    /**
     * 余数算法
     * @param v1:分子
     * @param v2:分母
     * @param scale
     * @return
     */
    public static double remainder(double v1, double v2,int scale){
    	BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        BigDecimal value = b1.remainder(b2);
        return value.setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }
    
    /**
     * 获取万元单位，例如输入 10000 输出 1
     * @param source 需要转化的数字
     * @return
     */
    public static String getMillionUnits(String source){
    	return String.valueOf(new BigDecimal(source).divide(ONE_MILLION));
    }

    public static void main(String[] args) {
        System.out.println(remainder(15000d,1000d,2));
    }

}
