package com.em.test.callcenter.domain;

import com.em.test.callcenter.core.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class Employee implements IAttendant, ICallCenterEmployee {

    protected final String uuid = UUID.randomUUID().toString();

    protected AtomicReference<ICall> call = new AtomicReference<>();

    protected Charge charge;

    protected List<IAttendantListener> listeners = new ArrayList<>();

    protected Consumer<ICall> callback;

    public Employee(Charge charge) {
        this.charge = charge;
    }

    @Override
    public String getId() {
        return uuid;
    }

    @Override
    public Boolean isAvailable() {
        return call.get() == null;
    }

    @Override
    public void assignCall(ICall call) {
        this.call.set(call);
        this.listeners.forEach(listener -> listener.onCallAssignation(this.call.get()));
    }

    @Override
    public void attendCall(Consumer<ICall> callback) {
        this.callback = callback;
    }

    @Override
    public int getLevel() {
        return this.charge.ordinal();
    }

    @Override
    public void finishCurrentCall() {
        if(call.get() != null) {
            ICall iCall = this.call.get();
            iCall.hangout();
            this.call.set(null);
            this.callback.accept(iCall);
            this.callback = null;
            this.listeners.forEach(listener -> listener.onCallFinished(iCall));
        }
    }

    @Override
    public void answerCurrentCall() {
        if(this.call.get() != null){
            this.call.get().attend();
            this.listeners.forEach(listener -> listener.onCallStarted(this.call.get()));
        }
    }

    @Override
    public void addListener(IAttendantListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(IAttendantListener listener) {
        this.listeners.remove(listener);
    }
}
