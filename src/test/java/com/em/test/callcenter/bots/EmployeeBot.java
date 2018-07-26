package com.em.test.callcenter.bots;

import com.em.test.callcenter.core.ICall;
import com.em.test.callcenter.domain.Charge;
import com.em.test.callcenter.domain.Employee;

import java.util.Random;
import java.util.function.Consumer;

public class EmployeeBot extends Employee {

    private int watingTime = 2;

    private int min = 5000;

    private int max = 10000;

    private Random random = new Random();

    public EmployeeBot(Charge charge) {
        super(charge);
    }

    @Override
    public void attendCall(Consumer<ICall> callback) {
        super.attendCall(callback);
        try {
            Thread.currentThread().sleep(watingTime);
            this.answerCurrentCall();
            Thread.currentThread().sleep(min + random.nextInt(max - min + 1));
            this.finishCurrentCall();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
