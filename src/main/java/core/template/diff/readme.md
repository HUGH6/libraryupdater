# 说明
diff包用于对比两个API之间的差异，用于推测代码转换操作
## 类说明
* entity：实体类
    * ApiElement：表示api的签名信息
    * ParamElement：表示参数信息
    * Diff：表示api差异的抽象接口
    * TransferAction：表示代码转换操作的抽象接口
    * diff包：Diff接口的各种实现，用于表示方法重命名、参数变更、类型变更等差异类型
    * action包：TransferAction的各种实现，于各类diff差异对应
* SimpleApiDiffer：用于对比两个Api的签名，以获得差异