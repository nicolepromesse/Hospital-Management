package org.example.hospitol.model;

import java.time.LocalDateTime;

public class MedicalRecords<T> {
    private String id;
    private T data;
    private LocalDateTime createdAt;

    public MedicalRecords(String id, T data) {
        this.id = id;
        this.data = data;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public T getData() { return data; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
