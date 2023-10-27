package com.protaskify.protaskify_api.controller;

import com.protaskify.protaskify_api.model.enity.*;
import com.protaskify.protaskify_api.service.FeatureService;
import com.protaskify.protaskify_api.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.protaskify.protaskify_api.repository.MessagesRepository;
import com.protaskify.protaskify_api.repository.StudentRepository;
import com.protaskify.protaskify_api.service.MessageService;
import com.protaskify.protaskify_api.service.SemesterService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/v1/common")
public class CommonController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageService messageService;
    private final SemesterService semesterService;
    private final FeatureService featureService;
    private final TaskService taskService;

    private final MessagesRepository messagesRepository;
    private final StudentRepository studentRepository;


    //--------------------Common--------------------
    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public Messages receiveMessage(@Payload Messages message){
        return message;
    }

    @MessageMapping("/private-message")
    public Messages recMessage(@Payload Messages messages) {
        String toId = messages.getLecturerId();
        if (messages.getFromId().equals(messages.getLecturerId())) {
            toId = messages.getStudentId();
        }
        simpMessagingTemplate.convertAndSendToUser(toId, "/private", messages);
        simpMessagingTemplate.convertAndSend(messageService.saveMessageFromJSON(messages), messages);
        return messages;
    }


    @GetMapping("/get-active-semester")
    public ResponseEntity<Semester> getActiveSemester() {
        return ResponseEntity.ok(semesterService.getActiveSemester());
    }


    //--------------------Message--------------------
    @MessageMapping("/room")
    public void sendMessage(@RequestBody Messages messages) {
        String toId = messages.getLecturerId();
        if (messages.getFromId().equals(messages.getLecturerId())) {
            toId = messages.getStudentId();
        }
        simpMessagingTemplate.convertAndSendToUser(toId, "/topic/room", messages);
        simpMessagingTemplate.convertAndSend(messageService.saveMessageFromJSON(messages), messages);
    }

    @GetMapping("/message-detail")
    public ResponseEntity<List<Messages>> getMessage(@RequestParam("pageNo") int pageNo, @RequestParam("pageSize") int pageSize,
                                                     @RequestParam("studentId") String studentId, @RequestParam("lecturerId") String lecturerId) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("date").descending());
        Page<Messages> messagesList = messagesRepository.findByStudentIdAndLecturerId(studentId, lecturerId, pageable);
        return ResponseEntity.ok(messagesList.getContent());
    }

    @GetMapping("/message-list")
    public ResponseEntity<List<?>> getMessagesInfo(@RequestParam("lecturerId") String lecturerId) {
        String semesterId = semesterService.getActiveSemester().getId();
        return ResponseEntity.ok(studentRepository.getMessagesInfo(semesterId, lecturerId));
    }


    //--------------------Feature--------------------
    @GetMapping("/view-features")
    public ResponseEntity<List<Feature>> getAllFeatures(@RequestParam("userId") String userId, @RequestParam("role") String role,
                                                        @RequestParam(name = "classId", required = false) Long classId,
                                                        @RequestParam(name = "groupId", required = false) Long groupId) {
        try {
            List<Feature> groupFeatures;
            if (role.equals("STUDENT")) {
                Student student = studentRepository.findStudentById(userId);
                groupFeatures = featureService.getAllFeatures(student.getClasses().getId(), student.getGroup().getId());
            } else {
                groupFeatures = featureService.getAllFeatures(classId, groupId);
            }
            return ResponseEntity.ok(groupFeatures);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    //--------------------Task--------------------
    @GetMapping("/view-all-task-of-group")
    public ResponseEntity<List<Task>> getTasksByGroup(@RequestParam("userId") String userId, @RequestParam("role") String role,
                                                      @RequestParam(name = "classId", required = false) Long classId,
                                                      @RequestParam(name = "groupId", required = false) Long groupId) {
        try {
            List<Task> tasks;
            if (role.equals("STUDENT")) {
                Student student = studentRepository.findStudentById(userId);
                tasks = taskService.getAllTasksOfGroup(student.getClasses().getId(), student.getGroup().getId());
            } else {
                tasks = taskService.getAllTasksOfGroup(classId, groupId);
            }
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}

