package com.em.test.callcenter.core;

public interface ICall {

    String getId();

    void hangout();

    void attend();

    boolean wasAttended();

    long getDuration();

}
