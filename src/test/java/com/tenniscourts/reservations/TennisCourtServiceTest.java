package com.tenniscourts.reservations;

import com.tenniscourts.schedules.ScheduleService;
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

import java.util.Optional;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = {TennisCourtService.class, TennisCourtMapper.class})
public class TennisCourtServiceTest {

    @Mock
    TennisCourtMapper tennisCourtMapper;

    @Mock
    TennisCourtRepository tennisCourtRepository;

    @InjectMocks
    private TennisCourtService tennisCourtService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddTennisCourt() {

        TennisCourtDTO tennisCourtDTOTest = new TennisCourtDTO();
        tennisCourtDTOTest.setTennisCourtSchedules(null);
        tennisCourtDTOTest.setName("Test court");
        tennisCourtDTOTest.setId(1L);
        Mockito.when(tennisCourtMapper.map(tennisCourtRepository.saveAndFlush(Mockito.any(TennisCourt.class)))).thenReturn(tennisCourtDTOTest);

        TennisCourtDTO tennisCourtDTO = tennisCourtService.addTennisCourt(tennisCourtDTOTest);

        Assert.assertEquals("Test court", tennisCourtDTO.getName());
    }

    @Test
    public void testFindTennisCourtById() {
        TennisCourt tennisCourt = TennisCourt.builder().name("Test 123").build();

        TennisCourtDTO tennisCourtDTOTest = new TennisCourtDTO();
        tennisCourtDTOTest.setTennisCourtSchedules(null);
        tennisCourtDTOTest.setName("Test court");
        tennisCourtDTOTest.setId(1L);
        Mockito.when(tennisCourtRepository.findById(Mockito.any(Long.class)).map(tennisCourtMapper::map)).thenReturn(Optional.of(tennisCourtDTOTest));

        TennisCourtDTO tennisCourtDTO = tennisCourtService.findTennisCourtById(1L);

        Assert.assertEquals("Test court", tennisCourtDTO.getName());
    }

    @Test
    public void testFindTennisCourtWithSchedulesById() {
        TennisCourt tennisCourt = TennisCourt.builder().name("Test 123").build();

        TennisCourtDTO tennisCourtDTOTest = new TennisCourtDTO();
        tennisCourtDTOTest.setTennisCourtSchedules(null);
        tennisCourtDTOTest.setName("Test court");
        tennisCourtDTOTest.setId(1L);
        Mockito.when(tennisCourtRepository.findById(Mockito.any(Long.class)).map(tennisCourtMapper::map)).thenReturn(Optional.of(tennisCourtDTOTest));

        TennisCourtDTO tennisCourtDTO = tennisCourtService.findTennisCourtWithSchedulesById(1L);

        Assert.assertEquals("Test court", tennisCourtDTO.getName());
    }
}
