package core.template.diff;

import core.template.diff.entity.ApiElement;
import org.junit.Test;

public class ApiElementBuilderTest {
    @Test
    public void testBuildApiElement() {
        String api1 = "com.my.package.ReturnType com.b.Class.search(com.d.Type1 param1, com.d.Type2 param2) throws AException, BException";
        String api2 = "com.my.package.ReturnType com.b.Class.search(com.d.Type1 param1, com.d.Type2 param2)";
        String api3 = "com.my.package.ReturnType com.b.Class.search() throws AException, BException";
        String api4 = "com.my.package.ReturnType com.b.Class.search()";

        ApiElement e1 = ApiElementBuilder.buildApiElement(api1);
        ApiElement e2 = ApiElementBuilder.buildApiElement(api2);
        ApiElement e3 = ApiElementBuilder.buildApiElement(api3);
        ApiElement e4 = ApiElementBuilder.buildApiElement(api4);

        System.out.println(e1);
        System.out.println(e2);
        System.out.println(e3);
        System.out.println(e4);
    }


}
