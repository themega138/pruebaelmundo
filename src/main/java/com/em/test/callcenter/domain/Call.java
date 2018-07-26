package com.em.test.callcenter.domain;

import com.em.test.callcenter.core.CallState;
import com.em.test.callcenter.core.ICall;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class Call implements ICall {

    private final String uuid = UUID.randomUUID().toString();

    private Date start;

    private Date end;

    private CallState state;

    public Call() {
        this.state = CallState.RINGING_UP;
    }

    @Override
    public String getId() {
        return uuid;
    }

    @Override
    public void attend() {
        this.start = Date.from(Instant.now());
        this.state = CallState.TALKING;
    }

    @Override
    public void hangout() {
        this.end = Date.from(Instant.now());
        this.state = CallState.HANGED;
    }

    @Override
    public boolean wasAttended() {
        return (start != null && end != null && state.equals(CallState.HANGED));
    }

    @Override
    public long getDuration() {
        return (start != null && end != null) ? end.getTime() - start.getTime():0L;
    }
}
