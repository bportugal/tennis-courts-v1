package com.tenniscourts.guests;

import com.tenniscourts.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GuestService {

    @Autowired
    private GuestRepository guestRepository;
    @Autowired
    private GuestMapper guestMapper;

    public GuestDTO addGuest(GuestDTO guestDTO) {
        return guestMapper.map(guestRepository.saveAndFlush(guestMapper.map(guestDTO)));
    }

    public GuestDTO findGuestById(Long id) {
        return guestRepository.findById(id).map(guestMapper::map).<EntityNotFoundException>orElseThrow(() ->
                new EntityNotFoundException("Guest not found.")
        );
    }

    public List<GuestDTO> findGuestByName(String name) {
        return guestMapper.map(guestRepository.findByName(name));
    }

    public List<GuestDTO> findAllGuests() {
        return guestMapper.map(guestRepository.findAll());
    }

    public GuestDTO updateGuest(Long id, String name) {
        return guestRepository.findById(id).map(guest -> {

            guest.setName(name);
            return guestMapper.map(guestRepository.save(guest));

        }).<EntityNotFoundException>orElseThrow(() ->
                new EntityNotFoundException("Guest not found.")
        );
    }

    public void deleteGuest(Long guestId) {
        guestRepository.deleteById(guestId);
    }
}
