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

@Component
@ExternalTaskSubscription("ServiceLab")
public class ProvideLaboratoryHandler implements ExternalTaskHandler {

    private static int COUNTS = 0;
    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {

        String name = externalTask.getVariable("name");
        String lastname = externalTask.getVariable("lastname");
        String age = externalTask.getVariable("age");

        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode topic = mapper.createObjectNode();
        topic.put("topicName", "ServiceLab");
        final ArrayNode topics = mapper.createArrayNode();
        topics.add(topic);

        /**
         *  Лаборатория ждет когда будут собраны анализы в кучу, тогда приступит к обработке
         */
            final ObjectNode root = mapper.createObjectNode();
            root.put("name", name);
            root.put("lastname", lastname);
            root.put("age", age);

            // остальные параметры не понадобятся
            root.put("statistic", "NULL");
            root.put("health", "NULL");
            root.put("id", "-1");
            root.put("docid", "-1");
            root.set("topics",topics);

        /**
         *  Отправление запроса в лабораторию на добавление анализов
         */
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(root), headers);
            String result = new RestTemplate().postForObject("http://localhost:8080/lab/add",
                    entity, String.class);

            JsonNode answer = mapper.readTree(result);
            String st = answer.get("entity").asText("ERR");
            if(st.equals("ERR")) externalTaskService.complete(externalTask);

        } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        if(COUNTS == 3) {
            String result = new RestTemplate().getForObject("http://localhost:8080/lab/process", String.class);
        }

        externalTaskService.complete(externalTask);
    }
}
