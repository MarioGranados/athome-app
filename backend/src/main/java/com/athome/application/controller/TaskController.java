package com.athome.application.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.athome.application.model.Household;
import com.athome.application.model.Task;
import com.athome.application.model.User;
import com.athome.application.repo.HouseholdRepository;
import com.athome.application.repo.TaskRepository;
import com.athome.application.services.UserService;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private TaskRepository taskRepository;
    private final HouseholdRepository householdRepository;
    private final UserService userService;

    public TaskController(TaskRepository taskRepository, HouseholdRepository householdRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.householdRepository = householdRepository;
        this.userService = userService;
    }

    //helper to get household from the current user logged in as we need to find this home and add tasks
    public Household getCurrentHousehold(Authentication authentication) {
        User currUser = userService.getAuthenticatedUser(authentication);
        Household household = currUser.getHousehold();

        if(household == null) {
            throw new RuntimeException("household not found!");
        }
        return household;
    }


    // get all houshold tasks
    @GetMapping("/all")
    public ResponseEntity<List<Task>> getHouseholdTasks(Authentication authentication) {
        Household currentHousehold = getCurrentHousehold(authentication);

        return ResponseEntity.ok(currentHousehold.getTasks());

    }

    // create a task
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Task> createTask(@RequestBody Task savedTask, Authentication authentication) {
        Household curentHousehold = getCurrentHousehold(authentication);
        if(savedTask == null) {
            throw new RuntimeException("no tasks found");
        }
        savedTask.setHousehold(curentHousehold);
        taskRepository.save(savedTask);
    return ResponseEntity.status(201).body(savedTask);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        return taskRepository.findById(id)
                .map(existingTask -> {
                    // 1. Update the fields from the request body
                    existingTask.setTaskName(task.getTaskName());
                    existingTask.setDueDate(task.getDueDate());
                    existingTask.setCompleted(task.isCompleted());

                    // 2. Save the updated entity and return 200 OK
                    Task updatedTask = taskRepository.save(existingTask);
                    return ResponseEntity.ok(updatedTask);
                })
                // 3. Return 404 Not Found if the ID doesn't exist
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (!taskRepository.existsById(id)) {
            return ResponseEntity.notFound().build(); // Returns 404 Not Found
        }

        taskRepository.deleteById(id);
        return ResponseEntity.noContent().build(); // Returns 204 No Content
    }

}
