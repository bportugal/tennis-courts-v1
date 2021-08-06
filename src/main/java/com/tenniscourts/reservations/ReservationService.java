package com.tenniscourts.reservations;

import com.tenniscourts.exceptions.AlreadyExistsEntityException;
import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.guests.GuestService;
import com.tenniscourts.schedules.ScheduleDTO;
import com.tenniscourts.schedules.ScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReservationMapper reservationMapper;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private GuestService guestService;

    public ReservationDTO bookReservation(CreateReservationRequestDTO createReservationRequestDTO, LocalDateTime reservationDateAndTime) {
        try {
            if (scheduleService.isScheduleAlreadyBeingUsed(createReservationRequestDTO.getScheduleId(), reservationDateAndTime)) {
                throw new AlreadyExistsEntityException("Reservation already exists");
            }
            ReservationDTO reservationDTO = new ReservationDTO();
            reservationDTO.setReservationStatus(ReservationStatus.READY_TO_PLAY.name());
            reservationDTO.setScheduledId(createReservationRequestDTO.getScheduleId());
            reservationDTO.setSchedule(scheduleService.findSchedule(createReservationRequestDTO.getScheduleId()));
            reservationDTO.setGuestId(createReservationRequestDTO.getGuestId());
            reservationDTO.setGuest(guestService.findGuestById(createReservationRequestDTO.getGuestId()));
            reservationDTO.setStartDateTime(reservationDateAndTime);
            reservationDTO.setEndDateTime(reservationDateAndTime.plusHours(1));
            reservationDTO.setValue(BigDecimal.valueOf(10)); //amount charged per booking
            ReservationDTO savedDTO = reservationMapper.map(reservationRepository.saveAndFlush(reservationMapper.map(reservationDTO)));
            scheduleService.addReservationIntoSchedule(createReservationRequestDTO.getScheduleId(), reservationMapper.map(savedDTO));
            return savedDTO;
        } catch (Exception e) {
            throw new UnsupportedOperationException("UnsupportedOperation");
        }
    }

    public ReservationDTO findReservation(Long reservationId) {
        return reservationRepository.findById(reservationId).map(reservationMapper::map).<EntityNotFoundException>orElseThrow(() ->
            new EntityNotFoundException("Reservation not found.")
        );
    }

    public ReservationDTO cancelReservation(Long reservationId) {
        return reservationMapper.map(this.cancel(reservationId));
    }

    private Reservation cancel(Long reservationId) {
        return reservationRepository.findById(reservationId).map(reservation -> {

            this.validateCancellation(reservation);

            BigDecimal refundValue = getRefundValue(reservation);
            return this.updateReservation(reservation, refundValue, ReservationStatus.CANCELLED);

        }).<EntityNotFoundException>orElseThrow(() ->
            new EntityNotFoundException("Reservation not found.")
        );
    }

    private Reservation updateReservation(Reservation reservation, BigDecimal refundValue, ReservationStatus status) {
        reservation.setReservationStatus(status);
        reservation.setValue(reservation.getValue().subtract(refundValue));
        reservation.setRefundValue(refundValue);
        scheduleService.deleteReservationFromSchedule(reservation.getSchedule(), reservation);

        return reservationRepository.save(reservation);
    }

    private void validateCancellation(Reservation reservation) {
        if (!ReservationStatus.READY_TO_PLAY.equals(reservation.getReservationStatus())) {
            throw new IllegalArgumentException("Cannot cancel/reschedule because it's not in ready to play status.");
        }

        if (reservation.getSchedule().getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Can cancel/reschedule only future dates.");
        }
    }

    public BigDecimal getRefundValue(Reservation reservation) {
        long hours = ChronoUnit.HOURS.between(LocalDateTime.now(), reservation.getSchedule().getStartDateTime());
        long minutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), reservation.getSchedule().getStartDateTime());

        if (hours >= 24) {
            return reservation.getValue();
        } else if (hours >= 12) {
            return reservation.getValue().multiply(new BigDecimal("0.75"));
        } else if (hours >= 2) {
            return reservation.getValue().multiply(new BigDecimal("0.5"));
        } else if (minutes >= 1 && minutes <= 119) {
            return reservation.getValue().multiply(new BigDecimal("0.25"));
        }

        return BigDecimal.ZERO;
    }

    public ReservationDTO rescheduleReservation(Long previousReservationId, Long scheduleId, LocalDateTime reservationDateAndTime) {
        ReservationDTO reservationDTO = findReservation(previousReservationId);

        if (scheduleId.equals(reservationDTO.getSchedule().getId()) &&
                reservationDTO.getStartDateTime().equals(reservationDateAndTime)) {
            throw new IllegalArgumentException("Cannot reschedule to the same slot.");
        }

        Reservation previousReservation = cancel(previousReservationId);

        previousReservation.setReservationStatus(ReservationStatus.RESCHEDULED);
        reservationRepository.save(previousReservation);

        ReservationDTO newReservation = bookReservation(CreateReservationRequestDTO.builder()
                .guestId(previousReservation.getGuest().getId())
                .scheduleId(scheduleId)
                .build(), reservationDateAndTime);

        newReservation.setPreviousReservation(reservationMapper.map(previousReservation));
        return newReservation;
    }
}
