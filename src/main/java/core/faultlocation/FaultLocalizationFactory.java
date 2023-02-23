package core.faultlocation;

import core.FaultLocalizationMain;
import core.faultlocation.flacoco.FlacocoFaultLocalization;

public class FaultLocalizationFactory {
    public static FaultLocalizationStrategy getFaultLocalization(FaultLocalizationMain.FaultLocalizationTypeEnum type) {
        switch (type) {
            case FLACOCO:
                return new FlacocoFaultLocalization();
            default:
                return new FlacocoFaultLocalization();
        }
    }
}
