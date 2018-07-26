package com.em.test.callcenter.core;

public interface ICallCenterEmployee {

    void finishCurrentCall();

    void answerCurrentCall();

    void addListener(IAttendantListener listener);

    void removeListener(IAttendantListener listener);

}
