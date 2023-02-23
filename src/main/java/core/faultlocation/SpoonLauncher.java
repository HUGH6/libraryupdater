package core.faultlocation;

import spoon.Launcher;
import spoon.processing.ProcessingManager;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.support.QueueProcessingManager;

public abstract class SpoonLauncher extends Launcher {

    Factory factory = null;

    public SpoonLauncher(Factory factory) throws Exception {
        this.factory = factory;
    }

    protected void process(CtElement element) {
        ProcessingManager processing = new QueueProcessingManager(factory);
        for (String processorName : getProcessorTypes()) {
            processing.addProcessor(processorName);
        }

        processing.process(element);
    }

}
