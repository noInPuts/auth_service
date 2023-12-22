package cphbusiness.noInPuts.authService.unit.service;

import cphbusiness.noInPuts.authService.model.SpamCheck;
import cphbusiness.noInPuts.authService.repository.SpamCheckRepository;
import cphbusiness.noInPuts.authService.service.SpamCheckServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class SpamCheckServiceTests {

    @Autowired
    private SpamCheckServiceImpl spamCheckServiceImpl;

    @MockBean
    private SpamCheckRepository spamCheckRepository;

    @Test
    public void getIpWhenUserAttemptsLogin() {
        // Arrange
        //test if ip is returned when user attempts login
        HttpServletRequest loginRequest = mock(HttpServletRequest.class);

        // Act and Assert
        assertEquals(any(String.class), spamCheckServiceImpl.getIp(loginRequest));
    }

    @Test
    public void ipShouldBeBlockedAfterWrongAttempts() {
        // Arrange
        //test if ip is blocked after 5 wrong attempts
        HttpServletRequest loginRequest = mock(HttpServletRequest.class);
        List<SpamCheck> spamCheckList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            spamCheckList.add(new SpamCheck(null));
        }
        when(spamCheckRepository.findAllByIpAndTimestampIsGreaterThan(any(), any(Date.class))).thenReturn(spamCheckList);

        // Act and Assert
        assertTrue(spamCheckServiceImpl.isBlocked(spamCheckServiceImpl.getIp(loginRequest)));
    }

    @Test
    public void ipShouldNotBeBlockedAfterSingleAttempt(){
        // Arrange
        //test if ip is not blocked after single attempt
        HttpServletRequest loginRequest = mock(HttpServletRequest.class);
        List<SpamCheck> spamCheckList = new ArrayList<>();
        spamCheckList.add(new SpamCheck(null));
        when(spamCheckRepository.findAllByIpAndTimestampIsGreaterThan(any(), any(Date.class))).thenReturn(spamCheckList);

        // Act and Assert
        assertFalse(spamCheckServiceImpl.isBlocked(spamCheckServiceImpl.getIp(loginRequest)));
    }
}