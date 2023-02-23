# 说明
detector包用于从客户端程序中检测API调用点。
## 代码组织结构说明
- MethodCallDetector：检测API调用点的核心类
- entity：用于保存实体对象
  - MethodCallPoint：表示API调用点，存储了调用点信息
## 使用示例
```java
public static void main(String[] args) {
    // 指定客户端项目路径
    String path = "E:\\projects\\libraryupdater\\src\\main\\resources\\test\\Demo.java";
    // 指定目标API签名
    String api = "boolean test.Demo.test2(java.lang.String)";
    // 提取客户端项目中的所有调用点信息
    List<MethodCallPoint> callPoints = MethodCallDetector.detectMethodCall(path, api);
    // 输出查看
    callPoints.stream().forEach(System.out::println);
}
```