package core.solutionsearch.spaces.operators.searchbased;

import conf.ConfigurationProperties;
import core.solutionsearch.spaces.operators.OperatorSpace;
import core.template.diff.ApiElementBuilder;
import core.template.diff.entity.ApiElement;

/**
 * 表示工具中的基于搜索的方法
 */
public class SearchBasedOperatorSpace extends OperatorSpace {
    public SearchBasedOperatorSpace() {
        String originApiStr = ConfigurationProperties.getProperty(ConfigurationProperties.ORIGIN_API);
        String targetApiStr = ConfigurationProperties.getProperty(ConfigurationProperties.TARGET_API);
        ApiElement originApi = ApiElementBuilder.buildApiElement(originApiStr);
        ApiElement targetApi = ApiElementBuilder.buildApiElement(targetApiStr);
        register(new ParamReplaceOperator(originApi, targetApi));
//        register(new AddNullPointCheckOperator());
//        register(new AddObjectInitOperator());
//        register(new AddTypeConvertCheckOperator());
//        register(new AddArrayCheckOperator());
    }
}
