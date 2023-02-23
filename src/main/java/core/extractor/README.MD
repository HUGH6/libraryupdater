# 软件库API提取器
用于从软件库的源码中提取软件库的API

## 输入
### 软件库源码
提取器会将软件库源码转换为AST结构并进行符号解析，并对AST进行遍历，访问其中的每个MethodDeclaration节点，从中提取软件库API的信息：
* 所在类
* 方法名称
* 返回值类型
* JavaDoc
* 方法签名
* 方法体
### 软件库依赖jar包
对于Maven项目的软件库，可以使用以下命令获取软件库的依赖jar包
```shell
mvn dependency:copy-dependencies -DoutputDirectory=lib
```

## 项目参数配置
运行项目前，需要在libraryupdater.properties文件中填写以下配置信息
```yaml
#########################
# api提取器配置
#########################

libraryIdentifier=Math-issue-280
libraryRoot=E:/projects/fastjson-1.2.21
librarySrcPath=E:/projects/fastjson-1.2.21/src/main/java
libraryDependencyPath=E:/projects/fastjson-1.2.21/lib
```

## 运行
```java
public static void main(String[] args) throws Exception {
        MethodExtractor extractor = new DefaultMethodExtractor();
        extractor.init();
        List<MethodInfo> res = extractor.extract();

        for (MethodInfo info : res) {
            System.out.println(MongoDao.toJson(info));
        }
    }
```