package ru.core.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.core.base.Analyse;
import ru.core.base.Health;
import ru.core.base.Patient;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
public class LaboratoryController {

    private static final List<Analyse> AnalyseList = new ArrayList<Analyse>();

    /**
     * Список обработанных анализов
     * @return
     */
    @GetMapping(value = "/lab/fetch/{id}")
    public @ResponseBody ResponseEntity<Analyse> getAnalyses(@PathVariable String id) {
        return new ResponseEntity<Analyse>(AnalyseList.get(Integer.parseInt(id)), HttpStatus.OK);
    }

    /**
     * Добавление анализов и сбор
     * @param patient
     * @return
     */
    @PostMapping(value = "/lab/add")
    public Response addAnalyse(@RequestBody Patient patient) {
        double K_a = new Random().nextDouble();
        double K_b = new Random().nextDouble();
        double K_c = new Random().nextDouble();

        Analyse analyse = new Analyse();
        analyse.setK_a(K_a);
        analyse.setK_b(K_b);
        analyse.setK_c(K_c);

        AnalyseList.add(analyse);
        return Response.ok(AnalyseList.size()).build();
    }

    /**
     * День обработки, все анализы обрабатываются
     * @return
     */
    @PostMapping(value = "/lab/process")
    public Response Process() {
        if(AnalyseList.size() < 1) return Response.status(201).entity("ERR").build();

        for(Analyse analyse : AnalyseList) {
            if(analyse.getResult() != null) continue;
            double result = analyse.getK_a() * 0.01 + analyse.getK_b() * 0.001 + analyse.getK_c() * 0.03;
            if(result > 0.2) analyse.setResult(Health.BAD);
            else analyse.setResult(Health.GOOD);
        }
        return Response.ok("OK").build();
    }
}
