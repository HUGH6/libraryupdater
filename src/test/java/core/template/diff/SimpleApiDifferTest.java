package core.template.diff;

import core.template.diff.entity.ApiElement;
import core.template.diff.entity.Diff;
import org.junit.Test;

import java.util.List;

public class SimpleApiDifferTest {
    @Test
    public void testDiff() {
        String api1 = "com.my.package.ReturnType4 com.b.Class.search()";
        ApiElement e1 = ApiElementBuilder.buildApiElement(api1);
        String api2 = "com.my.package.ReturnType4 com.b.Class.search1()";
        ApiElement e2 = ApiElementBuilder.buildApiElement(api2);
        List<Diff> diff1 = SimpleApiDiffer.diff(e1, e2);
        diff1.stream().forEach(System.out::println);
        System.out.println("=========================");

        String api3 = "com.my.package.ReturnType4 com.b.Class.search()";
        ApiElement e3 = ApiElementBuilder.buildApiElement(api3);
        String api4 = "com.my.package.ReturnType4 com.b.Class.search(Type1 param1, Type2 param2)";
        ApiElement e4 = ApiElementBuilder.buildApiElement(api4);
        List<Diff> diff2 = SimpleApiDiffer.diff(e3, e4);
        diff2.stream().forEach(System.out::println);
        System.out.println("=========================");

        String api5 = "com.my.package.ReturnType3 com.b.Class.search(Type3 param3, Type2 param2)";
        ApiElement e5 = ApiElementBuilder.buildApiElement(api5);
        String api6 = "com.my.package.ReturnType4 com.b.Class.search2(Type1 param1, Type2 param2) throws Aexceptions";
        ApiElement e6 = ApiElementBuilder.buildApiElement(api6);
        List<Diff> diff3 = SimpleApiDiffer.diff(e5, e6);
        diff3.stream().forEach(System.out::println);
        System.out.println("=========================");
    }
}
