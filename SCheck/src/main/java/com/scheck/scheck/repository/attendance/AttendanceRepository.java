package com.scheck.scheck.repository.attendance;

import com.scheck.scheck.entity.attendance.Attendance;
import com.scheck.scheck.entity.studyGroup.StudyGroup;
import com.scheck.scheck.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // 특정 그룹, 특정 날짜의 출석 기록 조회
    Optional<Attendance> findByStudyGroupAndUserAndAttendanceDate(StudyGroup studyGroup, User user, LocalDate date);

    // 특정 그룹의 특정 날짜 출석 현황 전체 조회
    List<Attendance> findByStudyGroupAndAttendanceDate(StudyGroup studyGroup, LocalDate date);

    // 특정 사용자의 특정 그룹 출석 기록 조회 (기간별)
    @Query("SELECT a FROM Attendance a WHERE a.user.userId = :userId AND a.studyGroup.groupId = :groupId " +
            "AND a.attendanceDate BETWEEN :startDate AND :endDate ORDER BY a.attendanceDate DESC")
    List<Attendance> findUserAttendanceInPeriod(@Param("userId") Long userId,
                                                @Param("groupId") Long groupId,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    // 특정 그룹의 출석 기록 조회 (기간별)
    @Query("SELECT a FROM Attendance a WHERE a.studyGroup.groupId = :groupId " +
            "AND a.attendanceDate BETWEEN :startDate AND :endDate ORDER BY a.attendanceDate DESC, a.user.nickname ASC")
    List<Attendance> findGroupAttendanceInPeriod(@Param("groupId") Long groupId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    // 특정 사용자의 특정 그룹 출석 통계
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.user.userId = :userId AND a.studyGroup.groupId = :groupId AND a.status = :status")
    Long countByUserAndGroupAndStatus(@Param("userId") Long userId,
                                      @Param("groupId") Long groupId,
                                      @Param("status") Attendance.AttendanceStatus status);

    // 특정 그룹의 오늘 출석 현황
    @Query("SELECT a FROM Attendance a WHERE a.studyGroup.groupId = :groupId AND a.attendanceDate = :today")
    List<Attendance> findTodayAttendance(@Param("groupId") Long groupId, @Param("today") LocalDate today);

    // 특정 사용자의 오늘 출석해야 할 그룹들
    @Query("SELECT a FROM Attendance a WHERE a.user.userId = :userId AND a.attendanceDate = :today")
    List<Attendance> findUserTodayAttendance(@Param("userId") Long userId, @Param("today") LocalDate today);

    // 출석률 계산용 - 전체 출석 기록 수
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.user.userId = :userId AND a.studyGroup.groupId = :groupId")
    Long countTotalAttendance(@Param("userId") Long userId, @Param("groupId") Long groupId);

    // 특정 그룹의 모든 사용자 출석률 계산용
    @Query("SELECT a.user.userId, COUNT(a), " +
            "SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN a.status = 'LATE' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN a.status = 'SUBMITTED' THEN 1 ELSE 0 END) " +
            "FROM Attendance a WHERE a.studyGroup.groupId = :groupId " +
            "GROUP BY a.user.userId")
    List<Object[]> getGroupAttendanceStats(@Param("groupId") Long groupId);

    // 아직 출석 체크를 안 한 사람들 조회
    @Query("SELECT a FROM Attendance a WHERE a.studyGroup.groupId = :groupId AND a.attendanceDate = :date AND a.status = 'ABSENT'")
    List<Attendance> findNotCheckedAttendance(@Param("groupId") Long groupId, @Param("date") LocalDate date);
}