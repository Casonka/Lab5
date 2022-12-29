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
import org.camunda.bpm.engine.exception.NullValueException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.core.base.Patient;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@ExternalTaskSubscription("ServiceReg")
public class ProvideRegistrationHandler implements ExternalTaskHandler {
    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        Logger.getLogger("ServiceReg").log(Level.INFO, "[Registration] Start new task");
        String name = externalTask.getVariable("name");
        String lastname = externalTask.getVariable("lastname");
        String age = externalTask.getVariable("age");
        String status = externalTask.getVariable("status");
        Logger.getLogger("ServiceReg").log(Level.INFO, "[Registration] Patient " + name + " " + lastname + " comes to registration");

        Map<String, Object> resultSet = new HashMap<>();
        if(status.equals("NULL")) {
            final ObjectMapper mapper = new ObjectMapper();
            final ObjectNode topic = mapper.createObjectNode();
            topic.put("topicName","ServiceReg");

            final ArrayNode topics = mapper.createArrayNode();
            topics.add(topic);

            final ObjectNode root = mapper.createObjectNode();
            root.put("name",name);
            root.put("lastname", lastname);
            root.put("age", age);
            root.put("status", status);
            root.put("health", "NULL");
            root.put("id", "-1");
            root.put("doctor_id", "-1");
            root.set("topics", topics);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Logger.getLogger("ServiceReg").log(Level.INFO, "[Registration] start service parsing");
            try {
                HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(root),headers);
                String result = new RestTemplate().postForObject("http://localhost:8080/register",
                        entity, String.class);

                JsonNode answer = mapper.readTree(result);
                for(JsonNode lockedTask : answer) {
                    String st = lockedTask.get("entity").asText("-1");
                    if(st.equals("-1")) externalTaskService.complete(externalTask);
                    Logger.getLogger("ServiceReg").log(Level.INFO, "[Registration]" + " Created patient for ID - " + st);
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            String id = externalTask.getVariable("id");
            if(id.equals("-1")) externalTaskService.complete(externalTask);

            try {
                Patient result = new RestTemplate().getForObject("http:localhost:8080/fetch/" + id, Patient.class);
                assert result != null;
                resultSet.put("name", result.getName());
                resultSet.put("lastname", result.getLastname());
                resultSet.put("age", result.getAge());
                resultSet.put("status", result.getStatus());
            } catch(NullPointerException | NullValueException e) {
                externalTaskService.complete(externalTask);
            }
        }

        /* Appointment to Doctor/Lab */
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode topic = mapper.createObjectNode();
        Logger.getLogger("ServiceReg").log(Level.INFO, "[Registration] Current patient was be processed");
        externalTaskService.complete(externalTask,resultSet);
    }
}
