# 说明
ingredient包是程序修复突变过程中与修复材料相关的代码

## 设计
*代码材料实体*

设计Ingredient类来表示代码材料实体

*代码材料搜索策略*

IngredientSearchStrategy接口表示代码材料的搜索策略，通过策略模式支持不同类型的搜索策略

*代码材料池*

IngredientPool接口，用于表示代码材料空间，内部通过聚合IngredientSearchStrategy来实现提取代码材料

*代码材料提取策略*

IngredientExtractStrategy接口，用于表示代码材料的提取策略，通过策略模式实现不同的提取策略

CallPointIngredientExtractStrategy类作为本工具的默认类，提取调用点所在的局部变量、字面量、表达式、方法参数变量、类成员字段等作为修复材料

对于CallPointIngredientExtractStrategy中涉及的每种类型的材料，分别使用一个XXXExtractor来提取

*代码材料范围*

IngredientScope枚举类表示代码材料的提取范围