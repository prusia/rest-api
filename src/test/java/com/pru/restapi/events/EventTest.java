package com.pru.restapi.events;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

	@Test
	public void builder() {
		Event event = Event.builder()
				.name("rest api")
				.description("rest api desc")
				.build();
		assertThat(event).isNotNull();
	}

	@Test
	public void javaBean() {
		// Given
		String name = "event";
		String descrption = "spring";

		// When
		Event event = new Event();
		event.setName(name);
		event.setDescription(descrption);

		// then
		assertThat(event.getName()).isEqualTo(name);
		assertThat(event.getDescription()).isEqualTo(descrption);



	}

}