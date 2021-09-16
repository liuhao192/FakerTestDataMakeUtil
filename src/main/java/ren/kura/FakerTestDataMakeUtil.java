package ren.kura;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.github.javafaker.Faker;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * <p>文件名称: FakerTestDataMakeUtil.java
 * <p>描述: 使用javafakerhe和reflectasm实现测试的数据的快速生成
 *
 * @author liuhao
 * @Date: 2021/9/16 10:31 下午
 * @since 1.0
 */
public class FakerTestDataMakeUtil extends AbstractFakerTestDataMake {

    private static final Map<Class, MethodAccess> METHOD_ACCESS_MAP = new HashMap(64);

    private static final Map<Class, List<Field>> FIELDS_MAP = new HashMap(64);

    private static final Map<Class, ConstructorAccess> CONSTRUCTOR_ACCESS_MAP = new HashMap(16);

    private static final Map<String, Integer> INDEX_MAP = new HashMap(64);

    private static final String GET_METHOD = "set";

    private static Faker faker = null;

    /**
     * FakerTestDataMakeUtil:: makeDate
     * <p>TO:通过传入的类构建对象和数据，默认使用Locale.CHINA
     * <p>HISTORY: 2021/9/16 liuhao : Created.
     *
     * @param testClass 需要生成对象的类
     * @return Object  生成的对象
     */
    public final Object makeData(Class testClass) {
        return makeData(testClass, Locale.CHINA);
    }

    /**
     * FakerTestDataMakeUtil:: makeDate
     * <p>TO:通过传入的类和字符集构建对象和数据
     * <p>HISTORY: 2021/9/16 liuhao : Created.
     *
     * @param testClass 需要生成对象的类
     * @param locale    字符集
     * @return Object  生成的对象
     */
    public final Object makeData(Class testClass, Locale locale) {
        //获取到类的方法
        MethodAccess testAccess = METHOD_ACCESS_MAP.get(testClass);
        if (testAccess == null) {
            testAccess = MethodAccess.get(testClass);
            METHOD_ACCESS_MAP.put(testClass, testAccess);
        }
        //获取到字段属性
        List<Field> fields = FIELDS_MAP.get(testClass);
        if (fields == null) {
            fields = getAllFields(testClass);
            FIELDS_MAP.put(testClass, fields);
        }
        //构建对象
        ConstructorAccess constructorAccess = CONSTRUCTOR_ACCESS_MAP.get(testClass);
        if (constructorAccess == null) {
            constructorAccess = ConstructorAccess.get(testClass);
            CONSTRUCTOR_ACCESS_MAP.put(testClass, constructorAccess);
        }
        if (faker == null) {
            faker=new Faker(locale);
        }
        Object testObject = constructorAccess.newInstance();
        for (Field field : fields) {
            field.setAccessible(true);
            String get_key = testClass.getName() + field.getName();
            Integer set_index = INDEX_MAP.get(get_key);
            if (field.getType() == String.class) {
                if (set_index == null) {
                    set_index = testAccess.getIndex(GET_METHOD + StringUtils.capitalize(field.getName()), String.class);
                    testAccess.invoke(testObject, set_index, makeString(field.getName()));
                }
            }
            if (field.getType() == Integer.class || field.getType() == int.class) {
                if (set_index == null) {
                    set_index = testAccess.getIndex(GET_METHOD + StringUtils.capitalize(field.getName()));
                    testAccess.invoke(testObject, set_index, makeInteger(field.getName()));
                }
            }
            if (field.getType() == Float.class || field.getType() == float.class) {
                if (set_index == null) {
                    set_index = testAccess.getIndex(GET_METHOD + StringUtils.capitalize(field.getName()));
                    testAccess.invoke(testObject, set_index, makeFloat(field.getName()));
                }
            }
            if (field.getType() == Double.class || field.getType() == double.class) {
                if (set_index == null) {
                    set_index = testAccess.getIndex(GET_METHOD + StringUtils.capitalize(field.getName()));
                    testAccess.invoke(testObject, set_index, makeDouble(field.getName()));
                }
            }
            if (field.getType() == Long.class || field.getType() == long.class) {
                if (set_index == null) {
                    set_index = testAccess.getIndex(GET_METHOD + StringUtils.capitalize(field.getName()));
                    testAccess.invoke(testObject, set_index, makeLong(field.getName()));
                }
            }
            if (field.getType() == Date.class) {
                if (set_index == null) {
                    set_index = testAccess.getIndex(GET_METHOD + StringUtils.capitalize(field.getName()), Date.class);
                    testAccess.invoke(testObject, set_index, makeDate(field.getName()));
                }
            }
            if (field.getType() == Boolean.class || field.getType() == boolean.class) {
                if (set_index == null) {
                    set_index = testAccess.getIndex(GET_METHOD + StringUtils.capitalize(field.getName()));
                    testAccess.invoke(testObject, set_index, makeBoolean(field.getName()));
                }
            }

            if (field.getType() == BigDecimal.class) {
                if (set_index == null) {
                    set_index = testAccess.getIndex(GET_METHOD + StringUtils.capitalize(field.getName()), BigDecimal.class);
                    testAccess.invoke(testObject, set_index, makeBigDecimal(field.getName()));
                }
            }
            field.setAccessible(false);
        }
        return testObject;
    }



    @Override
    protected String makeString(String fieldName) {
        return faker.name().fullName();
    }

    @Override
    protected Integer makeInteger(String fieldName) {
        return faker.number().numberBetween(1, 100);
    }

    @Override
    protected Float makeFloat(String fieldName) {
        Double randomDouble = faker.number().randomDouble(2, 1, 100);
        return randomDouble.floatValue();
    }

    @Override
    protected Double makeDouble(String fieldName) {
        return faker.number().randomDouble(2, 1, 100);
    }

    @Override
    protected Long makeLong(String fieldName) {
        Double randomDouble = faker.number().randomDouble(2, 1, 100);
        return randomDouble.longValue();
    }

    @Override
    protected Date makeDate(String fieldName) {
        return faker.date().birthday();
    }

    @Override
    protected Boolean makeBoolean(String fieldName) {
        if (faker.number().numberBetween(0, 2) == 1) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    protected BigDecimal makeBigDecimal(String fieldName) {
        Double randomDouble = faker.number().randomDouble(2, 1, 1000);
        return new BigDecimal(String.valueOf(randomDouble));
    }

    /**
     *  FakerTestDataMakeUtil:: getAllFields
     *  <p>TO:获取全部的字段属性
     *  <p>HISTORY: 2021/9/17 liuhao : Created.
     *  @param    clazz 类
     *  @return    List<Field>  字段的集合
     */
    private List<Field> getAllFields(Class clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        return fieldList;
    }

}
