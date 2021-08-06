package com.tenniscourts.schedules;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.reservations.Reservation;
import com.tenniscourts.tenniscourts.TennisCourtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    ScheduleRepository scheduleRepository;
    @Autowired
    ScheduleMapper scheduleMapper;
    @Autowired
    TennisCourtService tennisCourtService;

    public ScheduleDTO addSchedule(Long tennisCourtId, CreateScheduleRequestDTO createScheduleRequestDTO) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setTennisCourt(tennisCourtService.findTennisCourtById(tennisCourtId));
        dto.setTennisCourtId(tennisCourtId);
        dto.setStartDateTime(createScheduleRequestDTO.getStartDateTime());
        dto.setEndDateTime(createScheduleRequestDTO.getStartDateTime().plusDays(1));
        return scheduleMapper.map(scheduleRepository.saveAndFlush(scheduleMapper.map(dto)));
    }

    public List<ScheduleDTO> findSchedulesByDates(LocalDateTime startDate, LocalDateTime endDate) {
        return scheduleMapper.map(scheduleRepository.findByStartDateTime_GreaterThanEqualAndEndDateTime_LessThanEqual(startDate, endDate));

    }

    public ScheduleDTO findSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).map(scheduleMapper::map).<EntityNotFoundException>orElseThrow(() ->
                new EntityNotFoundException("Schedule not found.")
        );
    }

    public List<ScheduleDTO> findSchedulesByTennisCourtId(Long tennisCourtId) {
        return scheduleMapper.map(scheduleRepository.findByTennisCourt_IdOrderByStartDateTime(tennisCourtId));
    }

    public void addReservationIntoSchedule(Long id, Reservation reservation) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Schedule not found."));

        schedule.addReservation(reservation);
        scheduleRepository.save(schedule);
    }

    public void deleteReservationFromSchedule(Schedule schedule, Reservation reservation) {
        List<Reservation> reservations = schedule.getReservations();
        for (int i = 0; i < reservations.size(); i++) {
            if (reservations.get(i).getStartDateTime().equals(reservation.getStartDateTime())) {
                reservations.remove(i);
                break;
            }
        }
        scheduleRepository.save(schedule);
    }

    public Boolean isScheduleAlreadyBeingUsed(Long id, LocalDateTime reservationDateAndTime) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Schedule not found."));
        List<Reservation> reservations = schedule.getReservations();
        if (reservations.size() > 0) {
            for (Reservation reservation : reservations) {
                if (reservation.getStartDateTime().equals(reservationDateAndTime)) {
                    return true;
                }
            }
        }
        return false;
    }
}
