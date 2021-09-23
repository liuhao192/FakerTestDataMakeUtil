# FakerTestDataMakeUtil
>  单元测试中，经常涉及生成对象，并模拟数据进行赋值，使用javafaker虽然可以省去一部分工作，但是如果属性过多还是比较繁琐、工作量重复       
>  于是使用javafaker和reflectasm，只要两行代码快速生成测试的数据

1.不建议对基类的进行模拟，直接使用Javafaker效果会更好，虽然支持基类的数据的模拟（来自同事的第一次测试就使用string,异常的恐惧）
2.不建议对内部class类型的数据的填充，觉得会缺乏数据的多样式，可以通过配置INNER_FLAG开启内部的检验和数据的填充(setInnerFlag(true))，
建议传入内部的类进行数据的模拟，重写需要模拟的数据类型方法(后面对考虑对字符类型数据填充的更多随机性，不只是采用faker的name类进行填充)

```java
TestData.TestData1 testData1 = (TestData.TestData1) util.makeData(TestData.TestData1.class);
```

这里也会存在无限嵌套的风险，在代码中 添加!field.getType().equals(testClass) 避免出现嵌套数据填充

<p>3.不支持对集合类型数据的模拟和填充，对于集合类的数据的模拟，可以使用模拟对象的数据，再放入集合的方式，主要是模拟集合类型的数据的个数的问题
```java
public static void main(String[] args) {
    FakeDataMaker util = new FakeDataMaker();
    //随机值
    TestData testData = (TestData) util.makeData(TestData.class);
    System.out.println(testData.toString());
    //字符值只能是1
    FakeDataMaker stringOnlyOne = new FakeDataMaker(){
        @Override
        protected String makeString(String fieldName) {
            return "1";
        }
    };
    TestData stringOnlyOneData = (TestData) stringOnlyOne.makeData(TestData.class);
    System.out.println(stringOnlyOneData.toString());

    //固定某个字段是唯一
    FakeDataMaker onlyFieldOne = new FakeDataMaker(){
        @Override
        protected String makeString(String fieldName) {
            if("data3".equals(fieldName)){
                return "zjjdjd";
            }
            return super.makeString(fieldName);
        }
    };
    TestData onlyFieldOneData = (TestData) onlyFieldOne.makeData(TestData.class);
    System.out.println(onlyFieldOneData.toString());
}
```

6.其他功能的实现-替换构造方法，初始化对象
我们在实际开发中，遇到需要初始化对象，并且给对象赋值为空值，如果使用构造方法或者静态方法得到新对象，都会代码量比较多，尤其是构建方法的赋值，会有多个入参，
所以工具的提供一个构建空值对象的方法initEmptyObject，该方法中，字符类型为空字符串，数字类型默认为0，时间则为默认当前时间
```java
 TestData testData = (TestData) FakeDataMaker.initEmptyObject(TestData.class);
 打印
 TestData{data1=0, data2=0, data3='', data4=0.0, data5=0.0, data6=0, data7=Thu Sep 23 23:15:10 CST 2021, data8=true, data9=TestData1{data1=true}}
```

默认是开启对内部class的类的值的填充的，也可以配置关闭对内部class的填充

```java
TestData testData2 = (TestData)FakeDataMaker.initEmptyObject(TestData.class,false);
打印
TestData{data1=0, data2=0, data3='', data4=0.0, data5=0.0, data6=0, data7=Thu Sep 23 23:15:12 CST 2021, data8=true, data9=null}
```



https://github.com/liuhao192/FakerTestDataMakeUtil
