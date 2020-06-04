package com.pru.restapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

//	@MockBean
//	EventRepository eventRepository;

	@Test
	public void createEvent() throws Exception {
		EventDto event = EventDto.builder()
				.name("spring")
				.description("descrirpt")
				.beginEnrollmentDateTime(LocalDateTime.of(2020, 6, 3, 11, 0, 0))
				.closeEnrollmentDateTime(LocalDateTime.of(2020, 6, 4, 11, 0, 0))
				.beginEventDateTime(LocalDateTime.of(2020, 6, 3, 11, 0, 0))
				.endEventDateTime(LocalDateTime.of(2020, 6, 4, 11, 0, 0))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("강남")
				.build();

		// mockito에서 넘겨준 객체가 controller 내부에서 변환되면 전달될 수 없음
//		Mockito.when(eventRepository.save(event)).thenReturn(event);

		mockMvc.perform(post("/api/events/")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaTypes.HAL_JSON)
					.content(objectMapper.writeValueAsString(event)))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("id").exists())
				.andExpect(header().exists(HttpHeaders.LOCATION))
				.andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
				.andExpect(jsonPath("id").value(Matchers.not(100)))
				.andExpect(jsonPath("free").value(Matchers.not(true)))
		;
	}

	@Test
	public void createEvent_Bad_Request() throws Exception {
		Event event = Event.builder()
				.id(100)
				.name("spring")
				.description("descrirpt")
				.beginEnrollmentDateTime(LocalDateTime.of(2020, 6, 3, 11, 0, 0))
				.closeEnrollmentDateTime(LocalDateTime.of(2020, 6, 4, 11, 0, 0))
				.beginEventDateTime(LocalDateTime.of(2020, 6, 3, 11, 0, 0))
				.endEventDateTime(LocalDateTime.of(2020, 6, 4, 11, 0, 0))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("강남")
				.offline(true)
				.free(true)
				.eventStatus(EventStatus.BEGAN_ENROLLMENT)
				.build();

		// mockito에서 넘겨준 객체가 controller 내부에서 변환되면 전달될 수 없음
//		Mockito.when(eventRepository.save(event)).thenReturn(event);

		mockMvc.perform(post("/api/events/")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaTypes.HAL_JSON)
				.content(objectMapper.writeValueAsString(event)))
				.andDo(print())
				.andExpect(status().isBadRequest())
		;
	}
}
