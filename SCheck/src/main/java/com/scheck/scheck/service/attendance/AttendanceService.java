package com.scheck.scheck.service.attendance;

import com.scheck.scheck.dto.attendance.*;
import com.scheck.scheck.entity.attendance.Attendance;
import com.scheck.scheck.entity.groupMember.GroupMember;
import com.scheck.scheck.entity.studyGroup.StudyGroup;
import com.scheck.scheck.entity.user.User;
import com.scheck.scheck.repository.attendance.AttendanceRepository;
import com.scheck.scheck.repository.groupMember.GroupMemberRepository;
import com.scheck.scheck.repository.studyGroup.StudyGroupRepository;
import com.scheck.scheck.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;

    // 출석 기록 생성 (매일 자동 실행될 스케줄러용)
    @Transactional
    public void createDailyAttendance(LocalDate date) {
        List<StudyGroup> allGroups = studyGroupRepository.findAll();

        for (StudyGroup group : allGroups) {
            // 해당 요일에 출석해야 하는 그룹인지 확인
            if (isAttendanceDay(group, date)) {
                List<GroupMember> activeMembers = groupMemberRepository.findActiveMembers(group.getGroupId());

                for (GroupMember member : activeMembers) {
                    // 이미 출석 기록이 있는지 확인
                    Optional<Attendance> existing = attendanceRepository
                            .findByStudyGroupAndUserAndAttendanceDate(group, member.getUser(), date);

                    if (existing.isEmpty()) {
                        Attendance attendance = Attendance.builder()
                                .studyGroup(group)
                                .user(member.getUser())
                                .attendanceDate(date)
                                .build();

                        attendanceRepository.save(attendance);
                    }
                }
            }
        }
    }

    // 출석 체크
    @Transactional
    public AttendanceResponseDto checkAttendance(Long groupId, Long userId, AttendanceCheckRequestDto requestDto) {
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 그룹 멤버인지 확인
        if (!groupMemberRepository.isActiveMember(groupId, userId)) {
            throw new IllegalArgumentException("해당 그룹의 활성 멤버가 아닙니다.");
        }

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // 오늘 출석 기록 조회 또는 생성
        Attendance attendance = attendanceRepository
                .findByStudyGroupAndUserAndAttendanceDate(studyGroup, user, today)
                .orElseGet(() -> {
                    Attendance newAttendance = Attendance.builder()
                            .studyGroup(studyGroup)
                            .user(user)
                            .attendanceDate(today)
                            .build();
                    return attendanceRepository.save(newAttendance);
                });

        // 출석 체크 가능 시간인지 확인
        if (!attendance.canCheck(now)) {
            throw new IllegalArgumentException("현재 시간에는 출석 체크를 할 수 없습니다.");
        }

        // 이미 체크했는지 확인
        if (attendance.isChecked()) {
            throw new IllegalArgumentException("이미 출석 체크를 완료했습니다.");
        }

        // 출석 상태 업데이트
        switch (requestDto.getType().toUpperCase()) {
            case "PRESENT":
                if (attendance.isLate(now)) {
                    attendance.markLate();
                } else {
                    attendance.markPresent();
                }
                break;
            case "SUBMITTED":
                attendance.markSubmitted();
                break;
            default:
                throw new IllegalArgumentException("잘못된 출석 타입입니다.");
        }

        return AttendanceResponseDto.from(attendance);
    }

    // 특정 그룹의 특정 날짜 출석 현황 조회
    public GroupAttendanceStatusDto getGroupAttendanceStatus(Long groupId, LocalDate date, Long requesterId) {
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));

        // 그룹 멤버인지 확인 (멤버만 조회 가능)
        if (!groupMemberRepository.isActiveMember(groupId, requesterId)) {
            throw new IllegalArgumentException("해당 그룹의 활성 멤버만 출석 현황을 조회할 수 있습니다.");
        }

        List<Attendance> attendanceList = attendanceRepository.findByStudyGroupAndAttendanceDate(studyGroup, date);

        List<AttendanceResponseDto> attendanceDtos = attendanceList.stream()
                .map(AttendanceResponseDto::from)
                .collect(Collectors.toList());

        return GroupAttendanceStatusDto.of(date, studyGroup.getGroupName(), attendanceDtos);
    }

    // 오늘 내 출석 현황 조회
    public MyAttendanceStatusDto getMyTodayAttendance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LocalDate today = LocalDate.now();
        List<Attendance> todayAttendance = attendanceRepository.findUserTodayAttendance(userId, today);

        List<AttendanceResponseDto> attendanceDtos = todayAttendance.stream()
                .map(AttendanceResponseDto::from)
                .collect(Collectors.toList());

        return MyAttendanceStatusDto.of(today, attendanceDtos);
    }

    // 특정 사용자의 특정 그룹 출석 통계 조회
    public AttendanceStatsDto getUserAttendanceStats(Long groupId, Long userId, LocalDate startDate, LocalDate endDate) {
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Long totalDays = attendanceRepository.countTotalAttendance(userId, groupId);
        Long presentDays = attendanceRepository.countByUserAndGroupAndStatus(userId, groupId, Attendance.AttendanceStatus.PRESENT);
        Long lateDays = attendanceRepository.countByUserAndGroupAndStatus(userId, groupId, Attendance.AttendanceStatus.LATE);
        Long submittedDays = attendanceRepository.countByUserAndGroupAndStatus(userId, groupId, Attendance.AttendanceStatus.SUBMITTED);

        return AttendanceStatsDto.of(userId, user.getNickname(), totalDays, presentDays, lateDays, submittedDays);
    }

    // 특정 그룹의 모든 멤버 출석 통계 조회 (리더용)
    public List<AttendanceStatsDto> getGroupAttendanceStats(Long groupId, Long requesterId) {
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 리더만 전체 통계 조회 가능
        if (!studyGroup.isLeader(requester)) {
            throw new IllegalArgumentException("스터디 그룹 리더만 전체 출석 통계를 조회할 수 있습니다.");
        }

        List<Object[]> statsData = attendanceRepository.getGroupAttendanceStats(groupId);

        return statsData.stream()
                .map(data -> {
                    Long userId = (Long) data[0];
                    Long totalDays = (Long) data[1];
                    Long presentDays = (Long) data[2];
                    Long lateDays = (Long) data[3];
                    Long submittedDays = (Long) data[4];

                    User user = userRepository.findById(userId).orElse(null);
                    String nickname = user != null ? user.getNickname() : "알 수 없음";

                    return AttendanceStatsDto.of(userId, nickname, totalDays, presentDays, lateDays, submittedDays);
                })
                .collect(Collectors.toList());
    }

    // 출석 기록 삭제 (리더용 - 잘못 생성된 기록 삭제)
    @Transactional
    public void deleteAttendance(Long attendanceId, Long requesterId) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new IllegalArgumentException("출석 기록을 찾을 수 없습니다."));

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 리더만 삭제 가능
        if (!attendance.getStudyGroup().isLeader(requester)) {
            throw new IllegalArgumentException("스터디 그룹 리더만 출석 기록을 삭제할 수 있습니다.");
        }

        attendanceRepository.delete(attendance);
    }

    // 출석 요일 확인 헬퍼 메서드
    private boolean isAttendanceDay(StudyGroup group, LocalDate date) {
        String attendanceDays = group.getAttendanceDays();
        if (attendanceDays == null || attendanceDays.trim().isEmpty()) {
            return true; // 설정된 요일이 없으면 매일
        }

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int dayValue = dayOfWeek.getValue(); // 1=월요일, 7=일요일

        List<String> days = Arrays.asList(attendanceDays.split(","));
        return days.contains(String.valueOf(dayValue));
    }
}