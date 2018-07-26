package com.em.test.callcenter.core;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CallCenter implements Dispatcher, IAttendantsHolder{

    private Queue<ICall> calls = new ConcurrentLinkedQueue<>();

    private List<IAttendant> attendants = Collections.synchronizedList(new ArrayList<>());

    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    private List<ICall> attendedCalls = Collections.synchronizedList(new ArrayList<>());

    private List<ICallCenterListener> listeners = new ArrayList<>();

    public boolean hasPendingCalls(){
        return !calls.isEmpty() || !attendedCalls.isEmpty();
    }

    @Override
    public void dispatchCall(ICall call) {
        try {
            calls.offer(call);
            this.listeners.forEach(listener -> listener.onCallReceived(call));
            runAssignation();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error on call reception.", e);
        }
    }

    @Override
    public void registerAttendant(IAttendant attendant) {
        attendants.add(attendant);
        this.listeners.forEach(listener -> listener.onAttendantRegistered(attendant));
        if(!this.calls.isEmpty())runAssignation();
    }

    @Override
    public void unregisterAttendant(IAttendant attendant) {
        attendants.remove(attendant);
    }

    private void runAssignation(){
        Optional<Map.Entry<Integer, List<IAttendant>>> attendants = this.attendants.stream()
                .filter(attendant -> attendant.isAvailable())
                .collect(Collectors.groupingBy(o -> o.getLevel()))
                .entrySet().stream()
                .sorted(Comparator.comparingInt(value -> value.getKey()))
                .findFirst();
        if(attendants.isPresent()){
            Optional<IAttendant> first = attendants.get().getValue().stream().findFirst();
            if(first.isPresent())runAssignation(first.get());
        }
    }

    private void runAssignation(IAttendant attendant){
        ICall call = calls.poll();
        if(call != null) {
            attendedCalls.add(call);
            attendant.assignCall(call);
            executorService.execute(() -> {
                attendant.attendCall(attendable -> {
                    this.listeners.forEach(listener -> listener.onCallFinished(call));
                    if (!attendable.wasAttended()) {
                        calls.offer(attendable);
                    } else {
                        if (attendant.isAvailable()) {
                            runAssignation(attendant);
                        } else {
                            unregisterAttendant(attendant);
                        }
                    }
                    attendedCalls.remove(call);
                });

            });
            this.listeners.forEach(listener -> listener.onCallAssigned(attendant,call));
        }
    }

    public void addListener(ICallCenterListener listener){
        if(listener != null) this.listeners.add(listener);
    }

    public void removeListener(ICallCenterListener listener){
        if(listener != null) this.listeners.remove(listener);
    }
}
