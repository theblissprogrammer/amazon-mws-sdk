package com.theblissprogrammer.common.processor;

/**
 * Created by ahmed.saad on 2018-12-06.
 * Copyright © 2018. All rights reserved.
 */
import java.util.*;

import javax.annotation.processing.*;
import javax.lang.model.element.TypeElement;

@SupportedAnnotationTypes({
        "com.theblissprogrammer.common.annotations.RoomQuery"
})
public final class RoomProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        return false;
    }
}
