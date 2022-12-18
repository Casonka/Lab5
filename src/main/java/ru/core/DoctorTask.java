package ru.core;

import org.camunda.bpm.client.ExternalTaskClient;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Logger;

@Component
public class DoctorTask {

    public static final String WORKER_ID = "DoctorService";
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(DoctorTask.class);

    @Scheduled(fixedRate = 30000)
    public void fetchExternalTask() throws IOException, InterruptedException {
        LOGGER.info("Coming patient to Doctor");
        ExternalTaskClient client = ExternalTaskClient.create().
                baseUrl("http:localhost:8080/engine-rest")
                .asyncResponseTimeout(10000)
                .build();

        client.subscribe("ServiceDoc")
                .lockDuration(1000)
                .handler((externalTask, externalTaskService) -> {
                    /*
                     * Логика процесса регистрации
                     */
                    externalTask.getVariable("");


                    externalTaskService.complete(externalTask);
                }).open();
    }



}
