package ru.core.base;

public class Analyse {

    /**
     * Коэффициенты для расчета состояния здоровья
     */
        private int PatientID;
        private double K_a;
        private double K_b;
        private double K_c;

        private Health result;

    public int getPatientID() {
        return PatientID;
    }

    public void setPatientID(int patientID) {
        PatientID = patientID;
    }

    public double getK_a() {
        return K_a;
    }

    public void setK_a(double k_a) {
        K_a = k_a;
    }

    public double getK_b() {
        return K_b;
    }

    public void setK_b(double k_b) {
        K_b = k_b;
    }

    public double getK_c() {
        return K_c;
    }

    public void setK_c(double k_c) {
        K_c = k_c;
    }

    public Health getResult() {
        return result;
    }

    public void setResult(Health result) {
        this.result = result;
    }
}
