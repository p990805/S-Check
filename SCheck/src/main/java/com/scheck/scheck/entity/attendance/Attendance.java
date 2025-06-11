package com.scheck.scheck.entity.attendance;

import com.scheck.scheck.entity.studyGroup.StudyGroup;
import com.scheck.scheck.entity.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance",
        uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "user_id", "attendance_date"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long attendanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private StudyGroup studyGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AttendanceStatus status = AttendanceStatus.ABSENT;

    @Column(name = "checked_at")
    private LocalDateTime checkedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Attendance(StudyGroup studyGroup, User user, LocalDate attendanceDate) {
        this.studyGroup = studyGroup;
        this.user = user;
        this.attendanceDate = attendanceDate;
        this.status = AttendanceStatus.ABSENT;
    }

    // 비즈니스 메서드
    public void markPresent() {
        this.status = AttendanceStatus.PRESENT;
        this.checkedAt = LocalDateTime.now();
    }

    public void markLate() {
        this.status = AttendanceStatus.LATE;
        this.checkedAt = LocalDateTime.now();
    }

    public void markSubmitted() {
        this.status = AttendanceStatus.SUBMITTED;
        this.checkedAt = LocalDateTime.now();
    }

    public void markAbsent() {
        this.status = AttendanceStatus.ABSENT;
        this.checkedAt = null;
    }

    public boolean isChecked() {
        return this.status != AttendanceStatus.ABSENT;
    }

    public boolean canCheck(LocalDateTime currentTime) {
        // 출석 날짜가 오늘인지 확인
        if (!this.attendanceDate.equals(currentTime.toLocalDate())) {
            return false;
        }

        // 스터디 그룹의 출석 시간이 설정되어 있는지 확인
        if (this.studyGroup.getAttendanceTime() == null) {
            return true; // 시간 제한 없음
        }

        // 출석 시간 체크 (30분 전부터 2시간 후까지 가능)
        LocalDateTime attendanceDateTime = this.attendanceDate.atTime(this.studyGroup.getAttendanceTime());
        LocalDateTime startTime = attendanceDateTime.minusMinutes(30);
        LocalDateTime endTime = attendanceDateTime.plusHours(2);

        return currentTime.isAfter(startTime) && currentTime.isBefore(endTime);
    }

    public boolean isLate(LocalDateTime checkTime) {
        if (this.studyGroup.getAttendanceTime() == null) {
            return false; // 시간 제한 없으면 지각 없음
        }

        LocalDateTime attendanceDateTime = this.attendanceDate.atTime(this.studyGroup.getAttendanceTime());
        return checkTime.isAfter(attendanceDateTime);
    }

    public enum AttendanceStatus {
        PRESENT,    // 출석
        LATE,       // 지각
        ABSENT,     // 결석
        SUBMITTED   // 제출 (과제 등)
    }
}