package ren.kura;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>文件名称: AbstractFakeDataMaker.java
 * <p>描述: 工具的抽象类
 *
 * @author liuhao
 * @Date: 2021/9/16 10:37 下午
 * @since 1.0
 */
public abstract class AbstractFakeDataMaker {

    /**
     * AbstractFakeDataMaker:: makeString
     * <p>TO:生成字符串类型的数据
     * <p>HISTORY: 2021/9/16 liuhao : Created.
     *
     * @param fieldName 字段名称
     * @return String 字符类型
     */
    protected abstract String makeString(String fieldName);

    /**
     * AbstractFakeDataMaker:: makeInteger
     * <p>TO:生成整型类型的数据
     * <p>HISTORY: 2021/9/16 liuhao : Created.
     *
     * @param fieldName 字段名称
     * @return Integer 整型
     */
    protected abstract Integer makeInteger(String fieldName);

    /**
     * AbstractFakeDataMaker:: makeFloat
     * <p>TO:生成单精度型的数据
     * <p>HISTORY: 2021/9/16 liuhao : Created.
     *
     * @param fieldName 字段名称
     * @return Float 单精度类型数据
     */
    protected abstract Float makeFloat(String fieldName);

    /**
     * AbstractFakeDataMaker:: makeDouble
     * <p>TO:生成双精度类型的数据
     * <p>HISTORY: 2021/9/16 liuhao : Created.
     *
     * @param fieldName 字段名称
     * @return Double 双精度类型
     */
    protected abstract Double makeDouble(String fieldName);

    /**
     * AbstractFakeDataMaker:: makeLong
     * <p>TO:生成长整型的测试数据
     * <p>HISTORY: 2021/9/16 liuhao : Created.
     *
     * @param fieldName 字段名称
     * @return Long 长整型的数据
     */
    protected abstract Long makeLong(String fieldName);

    /**
     * AbstractFakeDataMaker:: makeDate
     * <p>TO:生成日期类型的测试数据
     * <p>HISTORY: 2021/9/16 liuhao : Created.
     *
     * @param fieldName 字段名称
     * @return Date 日期类型
     */
    protected abstract Date makeDate(String fieldName);

    ;

    /**
     * AbstractFakeDataMaker:: makeBoolean
     * <p>TO:生成布尔类型的测试数据
     *
     * <p>HISTORY: 2021/9/16 liuhao : Created.
     *
     * @param fieldName 字段名称
     * @return Boolean boolean类型的数据
     */
    protected abstract Boolean makeBoolean(String fieldName);

    /**
     * AbstractFakeDataMaker:: makeBigDecimal
     * <p>TO:生成高精度类型的测试数据
     * <p>HISTORY: 2021/9/16 liuhao : Created.
     *
     * @param fieldName 字段名称
     * @return BigDecimal 高精度的BigDecimal数值数据
     */
    protected  abstract BigDecimal makeBigDecimal(String fieldName);

}
