package ru.core;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@ExternalTaskSubscription("ServiceReg")
public class ProvideRegistrationHandler implements ExternalTaskHandler {

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        String name = externalTask.getVariable("name");
        Logger.getLogger("ServiceReg").log(Level.INFO, "Start Task !!!!");
        externalTaskService.complete(externalTask);
    }
}
