package core.extractor;

import java.util.List;

public interface MethodExtractor {
    List<MethodInfo> extract();
    void init() throws Exception;
}
