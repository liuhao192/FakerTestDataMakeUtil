package ren.kura;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.github.javafaker.Faker;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * <p>文件名称: FakerTestDataMakeUtil.java
 * <p>描述: 单元测试中，经常涉及生成对象，并模拟数据进行赋值，使用javafaker虽然可以省去一部分工作，但是如果属性过多还是比较繁琐、工作量重复，
 * 于是使用javafaker和reflectasm，只要两行代码快速生成测试的数据
 *
 * <p>1.不建议传入基类的进行模拟，直接使用Javafaker效果会更好，虽然支持基类的数据的模拟（来自同事的第一次测试就使用string,异常的恐惧）
 * <p>2.不建议对内部class类型的数据的填充，觉得会缺乏数据的多样式，可以通过配置INNER_FLAG开启内部的检验和数据的填充(setInnerFlag(true))，
 * 建议传入内部的类进行数据的模拟，重写需要模拟的数据类型方法(后面对考虑对字符类型数据填充的更多随机性，不只是采用faker的name类进行填充)
 * TestData.TestData1 testData1 = (TestData.TestData1) util.makeData(TestData.TestData1.class);
 * 这里也会存在无限嵌套的风险，在代码中 添加!field.getType().equals(testClass) 避免出现嵌套数据填充
 * <p>3.不支持对集合类型数据的模拟和填充，对于集合类的数据的模拟，可以使用模拟对象的数据，再放入集合的方式，主要是模拟集合类型的数据的个数的问题
 * <p>
 * 下一步，
 * 2.
 *
 * @author liuhao
 * @Date: 2021/9/16 10:31 下午
 * @since 1.0
 */
public class FakeDataMaker extends AbstractFakeDataMaker {

    private static final Map<Class, MethodAccess> METHOD_ACCESS_MAP = new HashMap(64);

    private static final Map<Class, List<Field>> FIELDS_MAP = new HashMap(64);

    private static final Map<Class, ConstructorAccess> CONSTRUCTOR_ACCESS_MAP = new HashMap(16);

    private static final Map<String, Integer> INDEX_MAP = new HashMap(64);

    private static final String GET_METHOD = "set";

    private static Faker faker = null;

    private static final Map<String, Boolean> BASE_MAP = new HashMap(16);

    private static FakeDataMaker EMPTY_OBJECT_MAKER = null;

    private static Boolean WARNING_FLAG = true;

    private Boolean INNER_FLAG = false;


    /**
     * FakeDataMaker:: setInnerFlag
     * <p>TO:开启内部类的赋值的配置，默认是不对内部类进行赋值
     * <p>HISTORY: 2021/9/18 liuhao : Created.
     *
     * @param innerFlag true 开启
     */
    public void setInnerFlag(Boolean innerFlag) {
        this.INNER_FLAG = innerFlag;
    }

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
     * FakeDataMaker:: emptyObject
     * <p>返回一个是空值的对象
     * <p>DO:重写FakeDataMaker的构建值的方法，全部设置为空值，字符设置为""，数字为0,日期为当前的时间(默认当前北京时区)
     * 默认开启内部类的值的注入
     * <p>HISTORY: 2021/9/23 liuhao : Created
     *
     * @param testClass 需要生成对象的类
     * @param innerFlag 内部类的注入标识
     * @return Object  一个空值的对象
     */
    public final static Object initEmptyObject(Class testClass, Boolean innerFlag) {
        if (EMPTY_OBJECT_MAKER == null || !EMPTY_OBJECT_MAKER.INNER_FLAG.equals(innerFlag)) {
            EMPTY_OBJECT_MAKER = new FakeDataMaker() {
                @Override
                protected String makeString(String fieldName) {
                    return "";
                }

                @Override
                protected Integer makeInteger(String fieldName) {
                    return 0;
                }

                @Override
                protected Float makeFloat(String fieldName) {
                    return 0F;
                }

                @Override
                protected Double makeDouble(String fieldName) {
                    return 0D;
                }

                @Override
                protected Long makeLong(String fieldName) {
                    return 0L;
                }

                @Override
                protected Date makeDate(String fieldName) {
                    //这里没设置缓存
                    return new Date();
                }

                @Override
                protected Boolean makeBoolean(String fieldName) {
                    return Boolean.TRUE;
                }

                @Override
                protected BigDecimal makeBigDecimal(String fieldName) {
                    return BigDecimal.ZERO;
                }
            };
            EMPTY_OBJECT_MAKER.setInnerFlag(innerFlag);
        }
        return EMPTY_OBJECT_MAKER.makeData(testClass);
    }

    /**
     * FakeDataMaker:: emptyObject
     * <p>返回一个是空值的对象
     * <p>DO:重写FakeDataMaker的构建值的方法，全部设置为空值，字符设置为""，数字为0,日期为当前的时间(默认当前北京时区)
     * 默认开启内部类的值的注入
     * <p>HISTORY: 2021/9/23 liuhao : Created
     *
     * @param testClass 需要生成对象的类
     * @return Object  一个空值的对象
     */
    public final static Object initEmptyObject(Class testClass) {
        return initEmptyObject(testClass, true);
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
        if (faker == null) {
            faker = new Faker(locale);
        }
        //构建对象
        ConstructorAccess constructorAccess = CONSTRUCTOR_ACCESS_MAP.get(testClass);
        if (constructorAccess == null) {
            constructorAccess = ConstructorAccess.get(testClass);
            CONSTRUCTOR_ACCESS_MAP.put(testClass, constructorAccess);
        }
        Object testObject = constructorAccess.newInstance();
        //基础类型的参数构建
        if (baseClass(testClass)) {
            if (WARNING_FLAG) {
                System.err.println("建议直接使用Java—faker生成基础类数据");
                WARNING_FLAG = false;
            }
            return baseClassValue(testClass, testObject);
        }
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

        for (Field field : fields) {
            field.setAccessible(true);
            String get_key = testClass.getName() + field.getName();
            Integer set_index = INDEX_MAP.get(get_key);
            if (set_index == null) {
                set_index = testAccess.getIndex(GET_METHOD + StringUtils.capitalize(field.getName()));
                INDEX_MAP.put(get_key, set_index);
            }

            if (INNER_FLAG) {
                Boolean baseValue = BASE_MAP.get(get_key);
                if (baseValue == null) {
                    baseValue = !baseClass(field.getType()) && !field.getType().equals(testClass)
                            && Modifier.toString(field.getType().getModifiers()).contains("static");
                    BASE_MAP.put(get_key, baseValue);
                }
                if (baseValue) {
                    Object baseClassValue = makeData(field.getType(), locale);
                    testAccess.invoke(testObject, set_index, baseClassValue);
                }
            }

            if (field.getType() == String.class) {
                testAccess.invoke(testObject, set_index, makeString(field.getName()));
            }
            if (field.getType() == Integer.class || field.getType() == int.class) {
                testAccess.invoke(testObject, set_index, makeInteger(field.getName()));
            }
            if (field.getType() == Float.class || field.getType() == float.class) {
                testAccess.invoke(testObject, set_index, makeFloat(field.getName()));
            }
            if (field.getType() == Double.class || field.getType() == double.class) {
                testAccess.invoke(testObject, set_index, makeDouble(field.getName()));
            }
            if (field.getType() == Long.class || field.getType() == long.class) {
                testAccess.invoke(testObject, set_index, makeLong(field.getName()));
            }
            if (field.getType() == Date.class) {
                testAccess.invoke(testObject, set_index, makeDate(field.getName()));
            }
            if (field.getType() == Boolean.class || field.getType() == boolean.class) {
                testAccess.invoke(testObject, set_index, makeBoolean(field.getName()));
            }
            if (field.getType() == BigDecimal.class) {
                testAccess.invoke(testObject, set_index, makeBigDecimal(field.getName()));
            }
            field.setAccessible(false);
        }
        return testObject;
    }

    private Object baseClassValue(Class testClass, Object testObject) {
        if (testClass == String.class) {
            testObject = makeString(null);
        }
        if (testClass == Integer.class || testClass == int.class) {
            testObject = makeString(null);
        }
        if (testClass == Float.class || testClass == float.class) {
            testObject = makeFloat(null);
        }
        if (testClass == Double.class || testClass == double.class) {
            testObject = makeString(null);
        }
        if (testClass == Long.class || testClass == long.class) {
            testObject = makeString(null);
        }
        if (testClass == Date.class) {
            testObject = makeString(null);
        }
        if (testClass == Boolean.class || testClass == boolean.class) {
            testObject = makeString(null);
        }
        if (testClass == BigDecimal.class) {
            testObject = makeString(null);
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
     * FakerTestDataMakeUtil:: getAllFields
     * <p>TO:获取全部的字段属性
     * <p>HISTORY: 2021/9/17 liuhao : Created.
     *
     * @param clazz 类
     * @return List<Field>  字段的集合
     */
    private List<Field> getAllFields(Class clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        return fieldList;
    }

    private Boolean baseClass(Class clazz) {
        return clazz.isPrimitive() || clazz.getPackage().getName().startsWith("java") ||
                clazz.getPackage().getName().startsWith("javax");
    }

}
