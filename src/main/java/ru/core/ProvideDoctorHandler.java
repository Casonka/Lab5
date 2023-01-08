package ru.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@ExternalTaskSubscription("ServiceDoc")
public class ProvideDoctorHandler implements ExternalTaskHandler {

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        Logger.getLogger("ServiceDoc").log(Level.INFO, "[Doctor] Start doctor service for coming patient");

        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode topic = mapper.createObjectNode();
        topic.put("topicName", "ServiceDoc");

        final ArrayNode topics = mapper.createArrayNode();
        topics.add(topic);

        /**
         * сбор параметров
         */
        String name = externalTask.getVariable("name");
        String lastname = externalTask.getVariable("lastname");
        int age = externalTask.getVariable("age");
        String docid = externalTask.getVariable("docid");

        final ObjectNode root = mapper.createObjectNode();
        root.put("name", name);
        root.put("lastname", lastname);
        root.put("age", age);

        // остальные параметры пустые, они не требуются
        root.put("statistic", "NULL");
        root.put("health", "NULL");
        root.put("id", "-1");
        root.put("docid" , docid );
        root.set("topics", topics);

        /**
         *  Отправление запроса на прием к доктору
         */
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(root),headers);
            String result = new RestTemplate().postForObject("http://localhost:8080/Doc/" + docid,
                    entity, String.class);

            JsonNode answer = mapper.readTree(result);
            String st = answer.get("entity").asText("ERR");
            if(st.equals("ERR")) externalTaskService.complete(externalTask);

            /**
             * Результат посещения
             */
            Logger.getLogger("ServiceDoc").log(Level.INFO, "[Doctor]" + " Doctor " + docid + " complete appointment");
            Logger.getLogger("ServiceDoc").log(Level.INFO, "[Doctor]" + " Patient health is  " + st);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    externalTaskService.complete(externalTask);
    }
}
