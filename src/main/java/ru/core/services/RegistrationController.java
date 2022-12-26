package ru.core.services;

import org.camunda.bpm.engine.exception.NullValueException;
import org.springframework.web.bind.annotation.*;
import ru.core.base.Doctor;
import ru.core.base.Health;
import ru.core.base.Patient;
import ru.core.base.Status;

import java.util.ArrayList;
import java.util.List;

/**
 *  Сервис контроля за регистратурой
 */
@RestController
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
    public Patient RegistrationForm(@RequestBody Patient input) {
        Patient result = FindPatient(input);
        if(result != null) {
            result.setStatus(input.getStatus());
            return result;
        }
        RegistrationList.add(input);
        input.setId(RegistrationList.size());
        return input;
    }

    /**
     *
     * @param id Уникальный номер пациента
     * @return Найденный пациент
     */
    @GetMapping(value = "/fetch/{id}")
    public Patient FetchPatient(@PathVariable String id) {
        try {
            return RegistrationList.get(Integer.parseInt(id));
        } catch(NullPointerException | NullValueException e) {return null;}
    }

    @PostMapping(value = "/appointmentLab")
    public int AppointmentPatientToLab(@RequestBody int id) {
        if(RegistrationList.get(id).getStatus() == Status.VISITED_DOCTOR && RegistrationList.get(id))
    }

    @PostMapping(value = "/appointmentDoc")
    public int AppointmentPatientToDoc(@RequestBody int id) {
        try {
            /**
             * Проверка статуса пациента перед записью
             */
            if(RegistrationList.get(id).getStatus() == Status.REGISTERED) {
                // нужно направление к доктору
                if (RegistrationList.get(id).getDoctor_id() != -1) {
                    return RegistrationList.get(id).getDoctor_id();
                }
                int identificator = -1;
                if (RegistrationList.get(id).getDoctor_id() == -1) {
                    int load = 100;
                    for (Doctor doc : DoctorList) {
                        if (doc.getPatients_counts() < load) load = doc.getPatients_counts();
                        identificator = doc.getId();
                    }
                }
                return identificator;
            } else {RegistrationList.get(id).setStatus(Status.ABORTED);return -1;}
        } catch(NullPointerException | NullValueException e) {return -1;}
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
