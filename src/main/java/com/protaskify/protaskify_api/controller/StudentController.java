package com.protaskify.protaskify_api.controller;

import com.protaskify.protaskify_api.model.enity.*;
import com.protaskify.protaskify_api.model.request.StudentSettingRequest;
import com.protaskify.protaskify_api.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("api/v1/student")
@RequiredArgsConstructor
@CrossOrigin
public class StudentController {
    private final FeatureService featureService;
    private final SprintService sprintService;
    private final StudentService studentService;
    private final TaskService taskService;
    private final GroupService groupService;


    //--------------------Sprint--------------------
    @GetMapping("/sprint/{studentId}")
    public ResponseEntity<List<Sprint>> getSprints(@PathVariable String studentId) {
        try {
            List<Sprint> sprint = sprintService.findSprintListByStudentId(studentId);
            return ResponseEntity.ok(sprint);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    //--------------------Feature--------------------
    @PostMapping("/create-feature")
    public ResponseEntity<Feature> createFeature(@RequestBody Feature feature) {
        try {
            Feature createdFeature = featureService.createFeature(feature);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFeature);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/update-feature/{featureId}")
    public ResponseEntity<Feature> updateFeature(@PathVariable Long featureId, @RequestBody Feature updatedFeature) {
        try {
            Feature editedFeature = featureService.updateFeature(featureId, updatedFeature);
            return ResponseEntity.ok(editedFeature);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/delete-feature/{featureId}")
    public ResponseEntity<Void> deleteFeature(@PathVariable Long featureId) {
        try {
            featureService.deleteFeature(featureId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    //--------------------Task--------------------
    //method này để lấy ra 2 danh sách cho sinh viên chọn feature và member làm task
    @GetMapping("/view-feature-member-list")
    public ResponseEntity<Object> getFeatureAndStudent() {
        try {
            List<Feature> groupFeatures = featureService.getGroupFeatures();
            List<Student> groupMembers = studentService.getGroupMembers();
            Map<String, Object> response = new HashMap<>();
            response.put("groupFeatures", groupFeatures);
            response.put("groupMembers", groupMembers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create-task/{studentId}/{featureId}")
    public ResponseEntity<Task> createTask(@RequestBody Task task, @PathVariable String studentId,
                                           @PathVariable Long featureId) {
        Task createTask = taskService.createTask(task, studentId, featureId);
        return ResponseEntity.ok(createTask);
    }

    @PutMapping("/update-task/{studentId}/{featureId}")
    public ResponseEntity<Task> updateTask(@RequestBody Task updatedTask, @PathVariable String studentId,
                                           @PathVariable Long featureId) {
        Task editedTask = taskService.updateTask(updatedTask, studentId, featureId);
        if (featureId != 0) {
            featureService.setStatusFeature(updatedTask, featureId);
        }
        return ResponseEntity.ok(editedTask);
    }

    @DeleteMapping("/delete-task/{taskId}/{studentId}/{featureId}")
    public ResponseEntity<Task> deleteTask(@PathVariable Long taskId, @PathVariable String studentId,
                                           @PathVariable Long featureId) {
        taskService.deleteTask(taskId, studentId, featureId);
        return ResponseEntity.noContent().build();
    }


    //--------------------Student--------------------
    @PutMapping("/update-student-info")
    public ResponseEntity<Student> updateStudentInfo(@RequestBody StudentSettingRequest request) {
        try {
            Student updatedStudent = studentService.updateStudentInfo(request.getStudentId(), request.getFacebook(), request.getGithub(), request.getSkill());
            return ResponseEntity.ok(updatedStudent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    //--------------------Group--------------------
    @PutMapping("/update-group-info/{oldLeaderId}/{newLeaderId}/{groupName}")
    public void updateGroupInfo(@PathVariable String oldLeaderId, @PathVariable String newLeaderId, @PathVariable String groupName) {
        groupService.updateGroupInfo(oldLeaderId, newLeaderId, groupName);
    }

    @PutMapping("/update-group-topic/{studentId}/{projectId}")
    public void updateGroupTopic(@PathVariable String studentId, @PathVariable Long projectId) {
        groupService.updateGroupProject(studentId, projectId);
    }
}
