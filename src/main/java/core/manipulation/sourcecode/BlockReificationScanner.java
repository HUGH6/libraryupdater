package core.manipulation.sourcecode;

import core.migration.util.MigrationSupporter;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.visitor.CtScanner;

public class BlockReificationScanner extends CtScanner {
    @Override
    public void visitCtIf(CtIf element) {
        super.visitCtIf(element);
        if(!(element.getThenStatement() instanceof CtBlock)){
            CtStatement c = element.getThenStatement() ;
            CtBlock nBlock = MigrationSupporter.getFactory().Core().createBlock();
            nBlock.addStatement(c);
            element.setThenStatement(nBlock);
        }

        if( element.getElseStatement() != null && !(element.getElseStatement() instanceof CtBlock)){
            CtStatement c = element.getElseStatement() ;
            CtBlock nBlock = MigrationSupporter.getFactory().Core().createBlock();
            nBlock.addStatement(c);
            element.setElseStatement(nBlock);
        }
    }
}
