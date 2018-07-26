package com.em.test.callcenter.core;

import com.em.test.callcenter.bots.EmployeeBot;
import com.em.test.callcenter.domain.Call;
import com.em.test.callcenter.domain.Charge;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CallCenterTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(CallCenterTests.class);

    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    private AtomicInteger total = new AtomicInteger(0);

    @Autowired
    private CallCenter callCenter;

    @Test
    public void contextLoads() {
    }

    @Test
    public void callCenterWith10Concurrent(){

        List<IAttendant> attendants = Arrays.asList(
                new EmployeeBot(Charge.OPERATOR),
                new EmployeeBot(Charge.OPERATOR),
                new EmployeeBot(Charge.OPERATOR),
                new EmployeeBot(Charge.SUPERVISOR),
                new EmployeeBot(Charge.DIRECTOR)
        );

        List<ICall> calls = Arrays.asList(
                new Call(),
                new Call(),
                new Call(),
                new Call(),
                new Call(),
                new Call(),
                new Call(),
                new Call(),
                new Call(),
                new Call()
        );

        callCenter.addListener(new ICallCenterListener() {
            @Override
            public void onCallReceived(ICall call) {
                LOGGER.info(String.format("Call %s dispactched.",call.getId()));
            }

            @Override
            public void onCallAssigned(IAttendant attendant, ICall call) {
                LOGGER.info(String.format("Call %s attended for employee %s with level %s.",call.getId(), attendant.getId(), attendant.getLevel()));
            }

            @Override
            public void onCallFinished(ICall call) {
                LOGGER.info(String.format("Call %s finished.",call.getId()));
            }

            @Override
            public void onAttendantRegistered(IAttendant attendant) {
                LOGGER.info(String.format("Attendant %s registered.",attendant.getId()));
            }

            @Override
            public void onAttendantUnregistered(IAttendant attendant) {
                LOGGER.info(String.format("Attendant %s registered.",attendant.getId()));
            }
        });

        attendants.forEach(attendant -> callCenter.registerAttendant(attendant));
        calls.forEach(iCall -> executorService.execute(() -> {
            try {
                Thread.currentThread().sleep(new Random().nextInt(2000));
                callCenter.dispatchCall(iCall);
                total.getAndAdd(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

        while (callCenter.hasPendingCalls() || total.get() != 10){}

    }

}
