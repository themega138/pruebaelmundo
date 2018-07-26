package com.em.test.callcenter.core;

public interface IAttendantListener {

    void onCallAssignation(ICall call);

    void onCallStarted(ICall call);

    void onCallFinished(ICall call);

}
