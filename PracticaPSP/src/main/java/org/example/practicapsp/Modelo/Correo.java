package org.example.practicapsp.Modelo;

public class Correo {
    private String subject;
    private String labels;

    public Correo(String subject, String labels) {
        this.subject = subject;
        this.labels = labels;
    }

    public String getSubject() {
        return subject;
    }

    public String getLabels() {
        return labels;
    }
}
