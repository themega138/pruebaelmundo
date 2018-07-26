package com.em.test.callcenter.core;

import java.util.function.Consumer;

public interface IAttendant {

    String getId();

    Boolean isAvailable();

    void assignCall(ICall call);

    void attendCall(Consumer<ICall> callback);

    int getLevel();

}
