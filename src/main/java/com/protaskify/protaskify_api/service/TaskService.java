package com.protaskify.protaskify_api.service;

import com.protaskify.protaskify_api.model.enity.*;
import com.protaskify.protaskify_api.repository.FeatureRepository;
import com.protaskify.protaskify_api.repository.GroupRepository;
import com.protaskify.protaskify_api.repository.StudentRepository;
import com.protaskify.protaskify_api.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final FeatureRepository featureRepository;
    private final TaskRepository taskRepository;
    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;


    public Task createTask(Task task, String studentId, Long featureId) {
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        if (studentOptional.isPresent()) {
            Student student = studentOptional.get();
            if (student != null) {
                Feature feature = featureRepository.getSpecialFeature(featureId);
                task.setFeature(feature);
                task.setStudent(student);
                List<Task> taskList = taskRepository.getTasksByStatus(featureId, student.getGroup().getId(), task.getStatus());
                task.setTaskIndex(taskList.size() + 1);
                return taskRepository.save(task);
            }
        }
        return null;
    }
    public Task updateTask(Task updatedTask, String studentId, Long featureId) {
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        if (studentOptional.isPresent()) {
            Student student = studentOptional.get();
            if (student != null) {
                Feature feature = featureRepository.getSpecialFeature(featureId);
                updatedTask.setFeature(feature);
                updatedTask.setStudent(student);
                Task existingTask = taskRepository.getTask(updatedTask.getId());
                if (updatedTask.getStatus().equals(existingTask.getStatus())) {

                    List<Task> taskList = taskRepository.getTasksByStatus(
                            featureId, student.getGroup().getId(), updatedTask.getStatus());
                    Iterator<Task> iterator = taskList.iterator();
                    while (iterator.hasNext()) {
                        Task task = iterator.next();
//                        task.setFeature(feature);
                        if (task.equals(existingTask)) {
                            iterator.remove();
                        } else if (task.getTaskIndex() < existingTask.getTaskIndex() && task.getTaskIndex() >= updatedTask.getTaskIndex()) {
                            task.setTaskIndex(task.getTaskIndex() + 1);
                        } else if (task.getTaskIndex() <= updatedTask.getTaskIndex() && task.getTaskIndex() > 1) {
                            task.setTaskIndex(task.getTaskIndex() - 1);
                        }
                    }
                    taskList.add(updatedTask);
                    taskRepository.saveAll(taskList);
                } else {
                    List<Task> newStatusTaskList = taskRepository.getTasksByStatus(
                            featureId, student.getGroup().getId(), updatedTask.getStatus());
//                    updatedTask.setTaskIndex(newStatusTaskList.size() + 1);

                    Iterator<Task> iterator1 = newStatusTaskList.iterator();
                    while (iterator1.hasNext()) {
                        Task task = iterator1.next();
                        if (task.getTaskIndex() >= updatedTask.getTaskIndex()) {
                            task.setTaskIndex(task.getTaskIndex() + 1);
                        }
                    }
                    newStatusTaskList.add(updatedTask);

                    List<Task> taskList = taskRepository.getTasksByStatus(
                            featureId, student.getGroup().getId(), existingTask.getStatus());
                    Task oldTask = taskRepository.getTask(existingTask.getId());
                    taskList.remove(oldTask);
                    Iterator<Task> iterator = taskList.iterator();
                    while (iterator.hasNext()) {
                        Task task = iterator.next();
                        if (task.getTaskIndex() > oldTask.getTaskIndex()) {
                            task.setTaskIndex(task.getTaskIndex() - 1);
                        }
                    }
                    taskRepository.saveAll(taskList);
                    taskRepository.saveAll(newStatusTaskList);
                }
            }
        }
        return null;
    }

    public void deleteTask(Long taskId, String studentId, Long featureId) {
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        if (studentOptional.isPresent()) {
            Student student = studentOptional.get();
            if (student != null) {
                Feature feature = featureRepository.getSpecialFeature(featureId);
                Task existingTask = taskRepository.findById(taskId).orElse(null);
                existingTask.setFeature(feature);
                if (existingTask != null) {
                    List<Task> taskList = taskRepository.getTasksByStatus(
                            featureId, student.getGroup().getId(), existingTask.getStatus());
                    Task oldTask = taskRepository.getTask(existingTask.getId());
                    taskList.remove(oldTask);
                    Iterator<Task> iterator = taskList.iterator();
                    while (iterator.hasNext()) {
                        Task task = iterator.next();

                        if (task.getTaskIndex() > existingTask.getTaskIndex()) {
                            task.setTaskIndex(task.getTaskIndex() - 1);
                        }
                    }
                    taskRepository.delete(existingTask);
                }
            }
        }
    }

    public List<Task> getAllTasksOfGroup (Long classId, Long groupId) {
        return taskRepository.findAllTasksOfGroup(classId, groupId);
    }

}
