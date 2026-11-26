package com.athome.application.model;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long id;

    String taskName;

    LocalDate dateCreated;

    LocalDate dueDate;

    boolean isCompleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "household_id")
    private Household household;

    public void setDateCreated() {
        this.dateCreated = LocalDate.now();
    }

    
}
