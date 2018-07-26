package com.em.test.callcenter.core;

public interface ICallCenterListener {

    void onCallReceived(ICall call);

    void onCallAssigned(IAttendant attendant, ICall call);

    void onCallFinished(ICall call);

    void onAttendantRegistered(IAttendant attendant);

    void onAttendantUnregistered(IAttendant attendant);

}
