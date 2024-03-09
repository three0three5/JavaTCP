package ru.hse.vectorizer.tcp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hse.vectorizer.utils.ExceptionSupplier;
import ru.hse.vectorizer.utils.RetryExecutor;

import java.io.OutputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResponseCreatorTest {

    @InjectMocks
    private ResponseCreator responseCreator;

    @Mock
    private RetryExecutor retryExecutor;

    @Test
    void testPerformResponse() {
        OutputStream outputStream = mock(OutputStream.class);
        String response = "Couldn't write response.";
        int delay = 100;
        int attempts = 3;

        when(retryExecutor.executeWithRetries(eq(attempts), eq(delay),
                any(ExceptionSupplier.class), eq(response))).thenReturn(true);

        responseCreator.performResponse(outputStream, response, delay, attempts);
        verify(retryExecutor).executeWithRetries(eq(attempts), eq(delay), any(ExceptionSupplier.class), eq(response));
    }
}
