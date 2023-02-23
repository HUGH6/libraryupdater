package core.validation.entity;

/**
 * 表示验证结果状态
 */
public enum ValidationResultStatusEnum {
    FAILING_FIRST,
    FAILING_SECOND,
    PASSING_FIRST,
    PASSING_REGRESSION,
    STOPPED_FIRST_VALIDATION,
    STOPPED_SECOND_VALIDATION;
}
