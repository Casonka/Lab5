package ru.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
import ru.core.base.Patient;
import ru.core.base.Statistic;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@ExternalTaskSubscription("ServiceReg")
public class ProvideRegistrationHandler implements ExternalTaskHandler {

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        Logger.getLogger("ServiceReg").log(Level.INFO, "[Registration] Start registration service");
        Map<String, Object> resultSet = new HashMap<>();
        int id = externalTask.getVariable("id");

        final ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        final ObjectNode topic = mapper.createObjectNode();
        topic.put("topicName","ServiceReg");

        final ArrayNode topics = mapper.createArrayNode();
        topics.add(topic);

        /**
         * пациент новый, добавить в базу сначала
         */
        if(id == -1) {
            String name = externalTask.getVariable("name");
            String lastname = externalTask.getVariable("lastname");
            int age = externalTask.getVariable("age");
            String statistic = externalTask.getVariable("status");

            final ObjectNode root = mapper.createObjectNode();
            root.put("name",name);
            root.put("lastname", lastname);
            root.put("age", age);
            root.put("status", statistic);
            root.put("health", "NULL");
            root.put("id", "-1");
            root.put("doctor_id", "-1");
            root.set("topics", topics);

            /**
             *  Отправление запроса на регистрацию
             */
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            try {
                HttpEntity<String> entity = new HttpEntity<String>(mapper.writeValueAsString(root),headers);
                String result = new RestTemplate().postForObject("http://localhost:8080/register",
                        entity, String.class);

                JsonNode answer = mapper.readTree(result);
                String st = answer.get("entity").asText("-1");
                if(st.equals("-1")) externalTaskService.complete(externalTask);
                Logger.getLogger("ServiceReg").log(Level.INFO, "[Registration]" + " Created patient for ID: " + st);
                id = Integer.parseInt(st);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         *  Стадия распределения: берем карточку пациента и решаем куда ему идти
         */

        // Получить пациента из базы (свежая информация)
        Patient patient = new RestTemplate().getForObject("http://localhost:8080/fetch/" + String.valueOf(id), Patient.class);

        // попытка запись к доктору если не был
        assert patient != null;
        if(patient.getStatus().equals("REGISTERED")) {
            final ObjectMapper DocMapper = new ObjectMapper();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            try {
                HttpEntity<String> entity = new HttpEntity<>(DocMapper.writeValueAsString(id),headers);
                String result = new RestTemplate().postForObject("http://localhost:8080/appointmentDoc",
                        entity, String.class);

                JsonNode answer = DocMapper.readTree(result);
                String st = answer.get("entity").asText("-1");
                if(st.equals("-1")) externalTaskService.complete(externalTask);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        // попытка записи в лабораторию
        else if(patient.getStatus().equals("VISITED_DOCTOR")) {
            final ObjectMapper LabMapper = new ObjectMapper();

            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.APPLICATION_JSON);

            try {
                HttpEntity<String> entity = new HttpEntity<>(LabMapper.writeValueAsString(id),header);
                String result = new RestTemplate().postForObject("http://localhost:8080/appointmentLab", entity, String.class);

                JsonNode answer = LabMapper.readTree(result);
                String st = answer.get("entity").asText("-1");
                if(st.equals("-1")) externalTaskService.complete(externalTask);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        else externalTaskService.complete(externalTask);

        /**
         * распределение и отправка далее в bpm
         */
        // получение итогового варианта пациента
        patient = new RestTemplate().getForObject("http://localhost:8080/fetch/" + String.valueOf(id), Patient.class);
        assert patient != null;

        resultSet.put("name",patient.getName());
        resultSet.put("lastname", patient.getLastname());
        resultSet.put("age", patient.getAge());
        resultSet.put("docid", String.valueOf(patient.getDoctor_id()));
        resultSet.put("status", patient.getStatus());
        Logger.getLogger("ServiceReg").log(Level.INFO, "[Registration] Current patient was processed");
        externalTaskService.complete(externalTask, resultSet);
    }
}