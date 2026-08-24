package com.alexeymelekhov.lms.repository;

import com.alexeymelekhov.lms.model.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Page<Schedule> findAllByGroupId(Long groupId, Pageable pageable);

    Page<Schedule> findAllByTeacherId(Long teacherId, Pageable pageable);

    Optional<Schedule> findByIdAndGroupId(Long id, Long groupId);

    @Modifying
    @Query("delete from Schedule s where s.startDate < :date")
    void deleteSchedulesOlderThan(LocalDateTime date);

    @Modifying
    @Query("delete from Schedule s where s.group.id = :id")
    void deleteByGroupId(Long id);

    @Modifying
    @Query("delete from Schedule s where s.course.id = :id")
    void deleteByCourseId(Long id);
}
