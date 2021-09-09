package com.softserveinc.ita.homeproject.homedata.entity.polls.templates;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("advice")
public class AdviceChoiceQuestion extends PollQuestion {
}
