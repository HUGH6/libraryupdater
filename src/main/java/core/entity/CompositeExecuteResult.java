package core.entity;

import java.util.ArrayList;
import java.util.List;

public class CompositeExecuteResult {
    private List<ExecutionResult> results = new ArrayList<>();

    public void addResult(ExecutionResult result) {
        results.add(result);
    }
}
