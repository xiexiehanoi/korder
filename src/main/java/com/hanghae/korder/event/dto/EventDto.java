package com.hanghae.korder.event.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventDto {

    private Long id;
    private String name;
    private String description;
    private String place;

    public EventDto(Long id, String name, String description, String place) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.place = place;
    }
}
