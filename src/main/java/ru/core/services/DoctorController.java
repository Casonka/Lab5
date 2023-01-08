package ru.core.services;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.core.base.Doctor;
import ru.core.base.Health;
import ru.core.base.Patient;
import ru.core.base.Statistic;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController(value = "/doctor")
public class DoctorController {
    private static final List<Doctor> DoctorList = new ArrayList<Doctor>();
    private static final List<Patient> LocalList = new ArrayList<>();
    static
    {
        DoctorList.add(new Doctor(1,"Sergey","Bolshev","101"));
        DoctorList.add(new Doctor(2,"Andrey","Goncharov","102"));
        DoctorList.add(new Doctor(3,"Maksim","Nazarenko","201"));
        DoctorList.add(new Doctor(4,"Ivan","Ivanov","305"));
        DoctorList.add(new Doctor(5,"Sergey","Storoshkov","203"));
    }

    @PostMapping(value = "addtoDoc")
    public Response add(@RequestBody Patient patient) {
        LocalList.add(patient);
        return Response.ok().build();
    }

    /**
     * Список пациентов, которые были на приеме у докторов
     * @return
     */
    @GetMapping(value = "Doc/list")
    public @ResponseBody Response list() {
        GenericEntity<List<Patient>> entity = new GenericEntity<List<Patient>>(LocalList) {};
        return Response.ok(entity).build();
    }

    private static final Random rnd = new Random(System.currentTimeMillis());

    /**
     * прием врача
     * @param id номер врача по записи
     * @param patient сам пациент (имя, фамилия, возраст, номер врача)
     * @return
     */
    @PostMapping(value = "Doc/{id}")
    public @ResponseBody Response appointmentDoctor(@PathVariable String id, @RequestBody Patient patient) {
        if(Integer.parseInt(id) != patient.getId()) { Response.status(201).entity("ERR").build(); }
        double index = rnd.nextDouble();
        if(index > 0.7) patient.setHealth(Health.GOOD);
        else patient.setHealth(Health.BAD);
        LocalList.add(patient);

        if(patient.getHealth() == Health.GOOD) return Response.ok("GOOD").build();
        else return Response.ok("BAD").build();
    }

}
