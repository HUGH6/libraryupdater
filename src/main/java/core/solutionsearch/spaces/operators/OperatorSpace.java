package core.solutionsearch.spaces.operators;

import java.util.ArrayList;
import java.util.List;

/**
 * 包含一个方法所有的操作
 */
public class OperatorSpace {
    List<Operator> operators = new ArrayList<>();

    /**
     * 想操作空间中注册操作
     * @param op
     */
    public void register(Operator op) {
        this.operators.add(op);
    }

    /**
     * 返回所有的操作列表
     * @return
     */
    public List<Operator> getOperators() {
        return this.operators;
    }

    /**
     * 返回所有的操作数组
     * @return
     */
    public Operator[] values() {
        return this.operators.toArray(new Operator[0]);
    }

    /**
     * 操作空间大小
     * @return
     */
    public int size() {
        return this.operators.size();
    }
}
