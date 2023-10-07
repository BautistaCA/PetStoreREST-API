package pet.store.controller.error;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalErrorHandler {
	//there's probably a cleaner or simpler way to create this error handler,
	//but I tried to follow the specific type of handler that we were asked
	//to create.
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ResponseBody
	public Map<String, String> handleNoSuchElementException(NoSuchElementException ex){
		log.error("NoSuchElementException", ex);

        Map<String, String> response = new HashMap<>();
        response.put("Message", ex.toString());

        return response;
	}
}
