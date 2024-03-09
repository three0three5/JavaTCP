package ru.hse.vectorizer.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetryExecutorTest {

    @Mock
    private ExceptionSupplier<Boolean> action;

    private final RetryExecutor retryExecutor = new RetryExecutor();

    @Test
    void givenActionSucceedsOnFirstAttempt_whenExecuteWithRetries_thenReturnTrue() throws Exception {
        // given
        when(action.get()).thenReturn(true);

        // when
        boolean result = retryExecutor.executeWithRetries(3, 1, action, "Retry failed");

        // then
        assertTrue(result);
        verify(action, times(1)).get();
    }

    @Test
    void givenActionFailsButSucceedsOnLastAttempt_whenExecuteWithRetries_thenReturnTrue() throws Exception {
        // given
        when(action.get())
                .thenThrow(new IOException("IO Error"))
                .thenThrow(new IOException("IO Error"))
                .thenReturn(true);

        // when
        boolean result = retryExecutor.executeWithRetries(3, 1, action, "Retry failed");

        // then
        assertTrue(result);
        verify(action, times(3)).get();
    }

    @Test
    void givenActionAlwaysFails_whenExecuteWithRetries_thenReturnFalse() throws Exception {
        // given
        when(action.get()).thenThrow(new IOException("IO Error"));

        // when
        boolean result = retryExecutor.executeWithRetries(3, 1, action, "Retry failed");

        // then
        assertFalse(result);
        verify(action, times(3)).get();
    }

    @Test
    void givenActionThrowsSocketTimeoutException_thenRetryAndLogMessage() throws Exception {
        // given
        when(action.get())
                .thenThrow(new SocketTimeoutException("Timeout"))
                .thenReturn(true);

        // when
        boolean result = retryExecutor.executeWithRetries(2, 1, action, "Retry failed");

        // then
        assertTrue(result);
        verify(action, times(2)).get();
    }

    @Test
    void givenActionThrowsUndefinedException_thenRethrowRuntimeException() throws Exception {
        // given
        when(action.get()).thenThrow(new RuntimeException("Runtime Error"));

        // when
        assertThrows(RuntimeException.class, () ->
                retryExecutor.executeWithRetries(3, 1, action, "Retry failed")
        );
        // then
        verify(action, times(1)).get();
    }
}
