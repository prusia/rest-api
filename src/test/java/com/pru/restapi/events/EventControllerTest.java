package com.pru.restapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pru.restapi.common.TestDesription;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
	@TestDesription("정상 등록")
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
	@TestDesription("입력 받을 수 없는 필드가 존재하여 에러 발생")
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

	@Test
	@TestDesription("입력값이 비어있는 경우 에러가 발생하는 테스트")
	public void createEvent_Bad_Request_Empty_Input() throws Exception {
		EventDto eventDto = EventDto.builder().build();

		this.mockMvc.perform(post("/api/events")
					.contentType(MediaType.APPLICATION_JSON)
					.content(this.objectMapper.writeValueAsString(eventDto))
					)
				.andExpect(status().isBadRequest());
	}


	@Test
	@TestDesription("입력값이 잘못된 경우 경우 에러가 발생하는 테스트")
	public void createEvent_Bad_Request_Wrong_Input() throws Exception {
		EventDto eventDto = EventDto.builder()
				.name("spring")
				.description("descrirpt")
				.beginEnrollmentDateTime(LocalDateTime.of(2020, 6, 3, 11, 0, 0))
				.closeEnrollmentDateTime(LocalDateTime.of(2020, 6, 2, 11, 0, 0))
				.beginEventDateTime(LocalDateTime.of(2020, 6, 3, 11, 0, 0))
				.endEventDateTime(LocalDateTime.of(2020, 6, 4, 11, 0, 0))
				.basePrice(10000)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("강남")
				.build();

		this.mockMvc.perform(post("/api/events")
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.objectMapper.writeValueAsString(eventDto)))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].objectName").exists())
				.andExpect(jsonPath("$[0].field").exists())
				.andExpect(jsonPath("$[0].defaultMessage").exists())
				.andExpect(jsonPath("$[0].code").exists())
				.andExpect(jsonPath("$[0].rejectedValue").exists())
		;
	}
}
