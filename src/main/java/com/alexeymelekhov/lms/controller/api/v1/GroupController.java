package com.alexeymelekhov.lms.controller.api.v1;

import com.alexeymelekhov.lms.dto.common.PaginatedResponseDTO;
import com.alexeymelekhov.lms.dto.group.GroupCreateDTO;
import com.alexeymelekhov.lms.dto.group.GroupDTO;
import com.alexeymelekhov.lms.dto.group.GroupUpdateDTO;
import com.alexeymelekhov.lms.dto.schedule.ScheduleCreateDTO;
import com.alexeymelekhov.lms.dto.schedule.ScheduleDTO;
import com.alexeymelekhov.lms.dto.schedule.ScheduleUpdateDTO;
import com.alexeymelekhov.lms.dto.student.StudentIdsDTO;
import com.alexeymelekhov.lms.service.GroupService;
import com.alexeymelekhov.lms.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
@Tag(name = "Groups", description = "Group management")
public class GroupController {

    private final GroupService groupService;
    private final ScheduleService scheduleService;

    @PostMapping
    @Operation(summary = "Create a group")
    public ResponseEntity<GroupDTO> createGroup(
            @RequestBody @Valid GroupCreateDTO dto
    ) {
        GroupDTO groupDTO = groupService.createGroup(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(groupDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a group")
    public GroupDTO updateGroup(
            @PathVariable Long id,
            @RequestBody @Valid GroupUpdateDTO dto
    ) {
        return groupService.updateGroup(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a group")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/students")
    @Operation(summary = "Add a students to a group")
    public ResponseEntity<Void> addStudentsToGroup(
            @PathVariable Long id,
            @RequestBody @Valid StudentIdsDTO dto
    ) {
        groupService.addStudentsToGroup(id, dto);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/schedules")
    @Operation(summary = "Get group schedule")
    public PaginatedResponseDTO<ScheduleDTO> getGroupSchedule(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return scheduleService.getSchedulesByGroupId(id, pageable);
    }

    @PostMapping("/{id}/schedules")
    @Operation(summary = "Create a schedule for a group")
    public ScheduleDTO createSchedule(
            @PathVariable Long id,
            @RequestBody @Valid ScheduleCreateDTO dto
    ) {
        return scheduleService.createSchedule(id, dto);
    }

    @PutMapping("/{groupId}/schedules/{scheduleId}")
    @Operation(summary = "Update a schedule for a group")
    public ScheduleDTO updateSchedule(
            @PathVariable Long groupId,
            @PathVariable Long scheduleId,
            @RequestBody @Valid ScheduleUpdateDTO dto
    ) {
        return scheduleService.updateSchedule(
                groupId,
                scheduleId,
                dto
        );
    }

    @DeleteMapping("/{groupId}/schedules/{scheduleId}")
    @Operation(summary = "Delete group schedule")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable Long groupId,
            @PathVariable Long scheduleId
    ) {
        scheduleService.deleteSchedule(groupId, scheduleId);

        return ResponseEntity.noContent().build();
    }
}
