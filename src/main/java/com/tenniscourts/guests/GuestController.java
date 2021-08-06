package com.tenniscourts.guests;

import com.tenniscourts.config.BaseRestController;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/guest")
public class GuestController extends BaseRestController {

    private final GuestService guestService;

    @PostMapping(path = "/add")
    public ResponseEntity<Void> addGuest(@RequestBody GuestDTO guestDTO) {
        return ResponseEntity.created(locationByEntity(guestService.addGuest(guestDTO).getId())).build();
    }

    @GetMapping(path = "/findById/{guestId}", produces = {"application/json"})
    public ResponseEntity<GuestDTO> findGuestById(@PathVariable Long guestId) {
        return ResponseEntity.ok(guestService.findGuestById(guestId));
    }

    @GetMapping(path = "/findByName/{guestName}", produces = {"application/json"})
    public ResponseEntity<List<GuestDTO>> findGuestByName(@PathVariable String guestName) {
        return ResponseEntity.ok(guestService.findGuestByName(guestName));
    }

    @GetMapping(path = "/findAllGuests", produces = {"application/json"})
    public ResponseEntity<List<GuestDTO>> findAllGuests() {
        return ResponseEntity.ok(guestService.findAllGuests());
    }

    @PutMapping(path = "/updateGuest", produces = {"application/json"})
    public ResponseEntity<GuestDTO> updateGuest(@RequestParam (value = "guestId") Long guestId,
                                                      @RequestParam (value = "name") String name) {
        return ResponseEntity.ok(guestService.updateGuest(guestId, name));
    }

    @DeleteMapping(path = "/delete")
    public void deleteGuest(@PathVariable Long guestId) {
        guestService.deleteGuest(guestId);
    }
}
