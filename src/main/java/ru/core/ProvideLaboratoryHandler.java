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
import org.camunda.feel.syntaxtree.In;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.core.base.Analyse;
import ru.core.base.Patient;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@ExternalTaskSubscription("ServiceLab")
public class ProvideLaboratoryHandler implements ExternalTaskHandler {

    private int id = -1;
    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        Logger.getLogger("ServiceLab").log(Level.INFO, "[Laboratory] Start laboratory service");
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
            Logger.getLogger("ServiceLab").log(Level.INFO, "[Laboratory] Add new analyses");
            HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(root), headers);
            String result = new RestTemplate().postForObject("http://localhost:8080/lab/add",
                    entity, String.class);

            JsonNode answer = mapper.readTree(result);
            String st = answer.get("entity").asText("-1");
            id = Integer.parseInt(st);
            if(st.equals("-1")) externalTaskService.complete(externalTask);

        } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            String result = new RestTemplate().postForObject("http://localhost:8080/lab/process","", String.class);
        try {
            JsonNode answer = mapper.readTree(result);
            String st = answer.get("entity").asText("ERR");
            if(st.equals("ERR")) externalTaskService.complete(externalTask);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Logger.getLogger("ServiceLab").log(Level.INFO, "[Laboratory] Process of analyses was be completed");

        Analyse analyse = new RestTemplate().getForObject("http://localhost:8080/lab/fetch/" + String.valueOf(id - 1), Analyse.class);
        assert analyse != null;
        Logger.getLogger("ServiceLab").log(Level.INFO, "[Laboratory] K_a - " + analyse.getK_a());
        Logger.getLogger("ServiceLab").log(Level.INFO, "[Laboratory] K_b - " + analyse.getK_b());
        Logger.getLogger("ServiceLab").log(Level.INFO, "[Laboratory] K_c - " + analyse.getK_c());
        Logger.getLogger("ServiceLab").log(Level.INFO, "[Laboratory] Result - " + analyse.getResult());
        externalTaskService.complete(externalTask);
    }
}
