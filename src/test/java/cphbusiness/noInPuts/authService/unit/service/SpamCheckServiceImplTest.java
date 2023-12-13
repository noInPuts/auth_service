package cphbusiness.noInPuts.authService.service;

import cphbusiness.noInPuts.authService.model.SpamCheck;
import cphbusiness.noInPuts.authService.repository.SpamCheckRepository;
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
class SpamCheckServiceImplTest {

    @Autowired
    private SpamCheckServiceImpl spamCheckServiceImpl;

    @MockBean
    private SpamCheckRepository spamCheckRepository;

    @Test
    public void getIpWhenUserAttemptsLogin() {
        //test if ip is returned when user attempts login
        HttpServletRequest loginRequest = mock(HttpServletRequest.class);
        assertEquals(any(String.class), spamCheckServiceImpl.getIp(loginRequest));
    }

    @Test
    public void ipShouldBeBlockedAfterWrongAttempts() {
        //test if ip is blocked after 5 wrong attempts
        HttpServletRequest loginRequest = mock(HttpServletRequest.class);
        List<SpamCheck> spamCheckList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            spamCheckList.add(new SpamCheck(null));
        }
        when(spamCheckRepository.findAllByIpAndTimestampIsGreaterThan(any(), any(Date.class))).thenReturn(spamCheckList);

        assertTrue(spamCheckServiceImpl.isBlocked(spamCheckServiceImpl.getIp(loginRequest)));
    }

    @Test
    public void ipShouldNotBeBlockedAfterSingleAttempt(){
        //test if ip is not blocked after single attempt
        HttpServletRequest loginRequest = mock(HttpServletRequest.class);
        List<SpamCheck> spamCheckList = new ArrayList<>();
        spamCheckList.add(new SpamCheck(null));
        when(spamCheckRepository.findAllByIpAndTimestampIsGreaterThan(any(), any(Date.class))).thenReturn(spamCheckList);

        assertFalse(spamCheckServiceImpl.isBlocked(spamCheckServiceImpl.getIp(loginRequest)));
    }
}