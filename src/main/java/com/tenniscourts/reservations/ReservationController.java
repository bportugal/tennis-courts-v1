package com.tenniscourts.reservations;

import com.tenniscourts.config.BaseRestController;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@RestController
@RequestMapping("/reservation")
public class ReservationController extends BaseRestController {

    private final ReservationService reservationService;

    @PostMapping("/bookReservation")
    public ResponseEntity<Void> bookReservation(@RequestBody CreateReservationRequestDTO createReservationRequestDTO,
                                                @RequestParam (value = "reservationDateAndTime")
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                LocalDateTime reservationDateAndTime) {
        return ResponseEntity.created(locationByEntity(reservationService.bookReservation(createReservationRequestDTO, reservationDateAndTime).getId())).build();
    }

    @GetMapping(path = "/findReservationById/{reservationId}", produces = {"application/json"})
    public ResponseEntity<ReservationDTO> findReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(reservationService.findReservation(reservationId));
    }

    @DeleteMapping("/cancelReservation/{reservationId}")
    public ResponseEntity<ReservationDTO> cancelReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(reservationService.cancelReservation(reservationId));
    }

    @PutMapping(path = "/rescheduleReservation", produces = {"application/json"})
    public ResponseEntity<ReservationDTO> rescheduleReservation(@RequestParam (value = "reservationId") Long reservationId,
                                                                @RequestParam (value = "scheduleId") Long scheduleId,
                                                                @RequestParam (value = "reservationDateAndTime")
                                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                                LocalDateTime reservationDateAndTime) {
        return ResponseEntity.ok(reservationService.rescheduleReservation(reservationId, scheduleId, reservationDateAndTime));
    }
}
