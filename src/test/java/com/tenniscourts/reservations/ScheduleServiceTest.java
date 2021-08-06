package com.tenniscourts.reservations;

import com.tenniscourts.schedules.*;
import com.tenniscourts.tenniscourts.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = ScheduleService.class)
public class ScheduleServiceTest {

    @Mock
    ScheduleMapper scheduleMapper;

    @Mock
    ScheduleRepository scheduleRepository;

    @InjectMocks
    ScheduleService scheduleService;

    @InjectMocks
    TennisCourtService tennisCourtService;

    @Mock
    TennisCourtMapper tennisCourtMapper;

    @Mock
    TennisCourtRepository tennisCourtRepository;

    private TennisCourtDTO tennisCourtDTO;
    private CreateScheduleRequestDTO createScheduleRequestDTO;
    private ScheduleDTO dto;

    @Before
    public void initTests() {
        tennisCourtDTO = new TennisCourtDTO();
        tennisCourtDTO.setTennisCourtSchedules(null);
        tennisCourtDTO.setName("Test court");
        tennisCourtDTO.setId(1L);

        createScheduleRequestDTO = new CreateScheduleRequestDTO();
        createScheduleRequestDTO.setStartDateTime(LocalDateTime.now());
        createScheduleRequestDTO.setTennisCourtId(1L);

        dto = new ScheduleDTO();
        dto.setTennisCourt(tennisCourtDTO);
        dto.setTennisCourtId(tennisCourtDTO.getId());
        dto.setStartDateTime(LocalDateTime.now());
        dto.setEndDateTime(LocalDateTime.now().plusDays(1));

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindSchedulesByDates() {
        TennisCourt tennisCourt = TennisCourt.builder().name("Test").build();
        Mockito.when(tennisCourtRepository.save(Mockito.any(TennisCourt.class))).thenReturn(tennisCourt);
        TennisCourtDTO tennisCourtDTO = tennisCourtService.addTennisCourt(new TennisCourtDTO());

        Schedule schedule = Schedule.builder().startDateTime(LocalDateTime.now()).endDateTime(LocalDateTime.now().plusDays(1)).build();
        Mockito.when(scheduleRepository.save(Mockito.any(Schedule.class))).thenReturn(schedule);
        ScheduleDTO scheduleDTO = scheduleService.addSchedule(1L, createScheduleRequestDTO);

        List<ScheduleDTO> list = scheduleService.findSchedulesByDates(LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        Assert.assertEquals(list.get(0).getStartDateTime(), scheduleDTO.getStartDateTime());
    }

    @Test
    public void testFindSchedulesById() {
        Schedule schedule = Schedule.builder().tennisCourt(new TennisCourt()).startDateTime(LocalDateTime.now()).endDateTime(LocalDateTime.now().plusHours(2)).build();
        scheduleRepository.save(schedule);
        Mockito.when(scheduleRepository.save(Mockito.any(Schedule.class))).thenReturn(schedule);
        Assert.assertEquals(scheduleService.findSchedulesByDates(LocalDateTime.now(), LocalDateTime.now().plusHours(1)),
                schedule);
    }
}
