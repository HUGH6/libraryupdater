package core.template.diff;

import core.template.diff.entity.ApiElement;
import core.template.diff.entity.ParamElement;

public class ApiElementBuilder {
    /**
     * 根据api签名信息创建ApiElement对象
     * @param api 形如 com.my.package.ReturnType com.b.Class.search(com.d.Type1 param1, com.d.Type2 param2) throws AException, BException
     * @return
     */
    public static ApiElement buildApiElement(String api) {
        ApiElement apiElement = new ApiElement();

        String fullApiSignature = api.trim();
        int throwsKeyWordIdx = fullApiSignature.indexOf("throws");
        if (throwsKeyWordIdx != -1) {
            String throwsElements = fullApiSignature.substring(throwsKeyWordIdx + 7);
            String[] exceptions = throwsElements.split(",");
            for (String e : exceptions) {
                e = e.trim();
                if (!"".equals(e)) {
                    apiElement.exceptions.add(e);
                }
            }

            fullApiSignature = fullApiSignature.substring(0, throwsKeyWordIdx);
        }

        int returnTypeSeparatorIdx = fullApiSignature.indexOf(" ");
        if (returnTypeSeparatorIdx == -1) {
            return null;
        }
        apiElement.qualifiedReturnType = fullApiSignature.substring(0, returnTypeSeparatorIdx).trim();

        int leftParenthesesIdx = fullApiSignature.indexOf("(", returnTypeSeparatorIdx);
        if (leftParenthesesIdx == -1) {
            return null;
        }
        int rightParenthesesIdx = fullApiSignature.indexOf(")", leftParenthesesIdx);
        if (rightParenthesesIdx == -1) {
            return null;
        }

        apiElement.name = fullApiSignature.substring(returnTypeSeparatorIdx+1, leftParenthesesIdx).trim();
        if (leftParenthesesIdx + 1 < rightParenthesesIdx) {
            String paramsStr = fullApiSignature.substring(leftParenthesesIdx+1, rightParenthesesIdx).trim();
            String[] paramsItems = paramsStr.split(",");

            int paramIdx = 0;
            for (String param : paramsItems) {
                param = param.trim();
                String[] paramInfo = param.split(" ");
                ParamElement p = new ParamElement();
                p.qualifiedType = paramInfo[0];
                p.name = paramInfo[1];
                p.position = paramIdx++;
                apiElement.params.add(p);
            }
        }

        return apiElement;
    }
}
