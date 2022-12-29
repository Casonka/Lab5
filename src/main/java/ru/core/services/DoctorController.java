package ru.core.services;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.core.base.Doctor;
import ru.core.base.Patient;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

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

//    @GetMapping(value = "fetch/{id}")
//    public Response add(@RequestBody Patient patient) {
//        for(Patient pat:LocalList) {
//            if(pat.getId() == patient.getId())
//        }
//        return Response.ok().build();
//    }


}
