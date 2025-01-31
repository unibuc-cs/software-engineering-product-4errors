package com._errors.MovieMingle.service;

import com._errors.MovieMingle.model.SecureToken;
import com._errors.MovieMingle.repository.SecureTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DefaultSecureTokenServiceTest {

    @Mock
    private SecureTokenRepository secureTokenRepository;

    @InjectMocks
    private DefaultSecureTokenService secureTokenService;

    private SecureToken secureToken;

    @BeforeEach
    public void setUp() {
        secureToken = new SecureToken();
        secureToken.setToken("test-token");
        secureToken.setExpireAt(LocalDateTime.now().plusSeconds(3600)); // 1h

        // setam tokenValidityInSeconds folosind reflectie
        ReflectionTestUtils.setField(secureTokenService, "tokenValidityInSeconds", 3600);
    }

    @Test
    public void testCreateSecureToken() {
        
        when(secureTokenRepository.save(any(SecureToken.class))).thenReturn(secureToken);

       
        SecureToken result = secureTokenService.createSecureToken();

        
        assertNotNull(result);
        assertNotNull(result.getToken());
        assertTrue(result.getToken().length() > 0);
        assertNotNull(result.getExpireAt());
        assertTrue(result.getExpireAt().isAfter(LocalDateTime.now()));

        verify(secureTokenRepository, times(1)).save(any(SecureToken.class));
    }

    @Test
    public void testSaveSecureToken() {
        
        when(secureTokenRepository.save(secureToken)).thenReturn(secureToken);

       
        secureTokenService.saveSecureToken(secureToken);

        
        verify(secureTokenRepository, times(1)).save(secureToken);
    }


    @Test
    public void testFindByToken() {
        
        when(secureTokenRepository.findByToken("test-token")).thenReturn(secureToken);

       
        SecureToken result = secureTokenService.findByToken("test-token");

        
        assertNotNull(result);
        assertEquals("test-token", result.getToken());
        verify(secureTokenRepository, times(1)).findByToken("test-token");
    }

    @Test
    public void testFindByToken_NotFound() {
        
        when(secureTokenRepository.findByToken("invalid-token")).thenReturn(null);

       
        SecureToken result = secureTokenService.findByToken("invalid-token");

        
        assertNull(result);
        verify(secureTokenRepository, times(1)).findByToken("invalid-token");
    }

    @Test
    public void testRemoveToken() {
        
        doNothing().when(secureTokenRepository).delete(secureToken);

       
        secureTokenService.removeToken(secureToken);

        
        verify(secureTokenRepository, times(1)).delete(secureToken);
    }

    @Test
    public void testRemoveTokenByToken() {
        
        when(secureTokenRepository.removeByToken("test-token")).thenReturn(1L); 

       
        secureTokenService.removeTokenByToken("test-token");

        
        verify(secureTokenRepository, times(1)).removeByToken("test-token");
    }

    @Test
    public void testGetTokenValidityInSeconds() {
       
        int result = secureTokenService.getTokenValidityInSeconds();

        
        assertEquals(3600, result);
    }
}