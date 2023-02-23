package core.migration.entity;

import core.entity.ProgramVariant;

import java.util.ArrayList;
import java.util.List;

/**
 * 保存迁移的结果（成功的代码变体）
 */
public class MigrationResult {
    // 正确的迁移结果代码变体
    private List<ProgramVariant> solutions = new ArrayList<>();

    public List<ProgramVariant> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<ProgramVariant> solutions) {
        this.solutions = solutions;
    }
}
