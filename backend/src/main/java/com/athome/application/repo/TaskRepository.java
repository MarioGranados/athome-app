package com.athome.application.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.athome.application.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>{

    List<Task> findTasksByHousehold(Long householdId);
}
