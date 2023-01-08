package ru.core.services;

import org.camunda.bpm.engine.exception.NullValueException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.core.base.Doctor;
import ru.core.base.Health;
import ru.core.base.Patient;
import ru.core.base.Statistic;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 *  Сервис контроля за регистратурой
 */
@RestController(value = "/registration")
public class RegistrationController {

    private static final List<Patient> RegistrationList = new ArrayList<Patient>();
    private static final List<Doctor> DoctorList = new ArrayList<Doctor>();
    static
    {
        DoctorList.add(new Doctor(1,"Sergey","Bolshev","101"));
        DoctorList.add(new Doctor(2,"Andrey","Goncharov","102"));
        DoctorList.add(new Doctor(3,"Maksim","Nazarenko","201"));
        DoctorList.add(new Doctor(4,"Ivan","Ivanov","305"));
        DoctorList.add(new Doctor(5,"Sergey","Storoshkov","203"));
    }

    /**
     * Поиск пациента в базе регистратуры
     * @param input new or old patient
     * @return Если его нет, то добавляет новую запись, если есть, то обновляет статус
     */
    @PostMapping(value="/register")
    public Response RegistrationForm(@RequestBody Patient input) {
        Patient result = FindPatient(input);
        if(result != null) {
            result.setStatus(input.getStatus());
            RegistrationList.get(result.getId()).setStatus(input.getStatus());
            return Response.ok(result.getId()).build();
        }
        input.setId(RegistrationList.size() + 1);
        input.setStatus(Statistic.REGISTERED);
        RegistrationList.add(input);
        return Response.ok(input.getId()).build();
    }

    /**
     *
     * @param id Уникальный номер пациента
     * @return Найденный пациент
     */
    @GetMapping(value = "/fetch/{id}", produces = MediaType.APPLICATION_JSON)
    public @ResponseBody ResponseEntity<Patient> FetchPatient(@PathVariable String id) {
        int number = Integer.parseInt(id);
            return new ResponseEntity<Patient>(RegistrationList.get(number - 1), HttpStatus.OK);
    }

    @PostMapping(value = "/appointmentLab")
    public Response AppointmentPatientToLab(@RequestBody int id) {
        if(RegistrationList.get(id).getStatus() == Statistic.VISITED_DOCTOR && RegistrationList.get(id).getHealth() == Health.BAD) {
            RegistrationList.get(id).setStatus(Statistic.RECEIVED_REFERRAL);
            return Response.ok(id).build();
        } else return Response.status(201).entity(-1).build();
    }

    @PostMapping(value = "/appointmentDoc", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public Response AppointmentPatientToDoc(@RequestBody String id) {
        try {
            int ide = Integer.parseInt(id) - 1;
            /**
             * Проверка статуса пациента перед записью
             */
            if(RegistrationList.get(ide).getStatus() == Statistic.REGISTERED) {
                // нужно направление к доктору
                if (RegistrationList.get(ide).getDoctor_id() != -1) {
                    return Response.ok(RegistrationList.get(ide).getDoctor_id()).build();
                }
                int identificator = -1;
                if (RegistrationList.get(ide).getDoctor_id() == -1) {
                    int load = 100;
                    for (Doctor doc : DoctorList) {
                        if (doc.getPatients_counts() < load) load = doc.getPatients_counts();
                        identificator = doc.getId();
                    }
                }
                RegistrationList.get(ide).setDoctor_id(identificator);
                return Response.ok(String.valueOf(identificator)).build();
            } else {RegistrationList.get(ide).setStatus(Statistic.ABORTED);return Response.status(201).entity("-1").build();}
        } catch(NullPointerException | NullValueException e) {return Response.status(201).entity("-1").build();}
    }

    private Patient FindPatient(Patient patient) {
            for(Patient pat:RegistrationList) {
                try {
                if(pat.getName().equals(patient.getName()) && pat.getLastname().equals(patient.getLastname()) &&
                        pat.getAge() == patient.getAge()) {
                    return pat;
                }
                } catch(NullPointerException e) {return null;}
            }
        return null;
    }
}
