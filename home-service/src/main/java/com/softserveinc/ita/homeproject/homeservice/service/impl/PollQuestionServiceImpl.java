package com.softserveinc.ita.homeproject.homeservice.service.impl;

import com.softserveinc.ita.homeproject.homedata.entity.AdviceChoiceQuestion;
import com.softserveinc.ita.homeproject.homedata.entity.MultipleChoiceQuestion;
import com.softserveinc.ita.homeproject.homedata.entity.Poll;
import com.softserveinc.ita.homeproject.homedata.entity.PollQuestion;
import com.softserveinc.ita.homeproject.homedata.entity.PollQuestionType;
import com.softserveinc.ita.homeproject.homedata.entity.PollStatus;
import com.softserveinc.ita.homeproject.homedata.repository.PollQuestionRepository;
import com.softserveinc.ita.homeproject.homedata.repository.PollRepository;
import com.softserveinc.ita.homeproject.homeservice.dto.PollQuestionDto;
import com.softserveinc.ita.homeproject.homeservice.dto.PollQuestionTypeDto;
import com.softserveinc.ita.homeproject.homeservice.exception.NotFoundHomeException;
import com.softserveinc.ita.homeproject.homeservice.exception.TypeOfTheContactDoesntMatchHomeException;
import com.softserveinc.ita.homeproject.homeservice.mapper.ServiceMapper;
import com.softserveinc.ita.homeproject.homeservice.service.PollQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PollQuestionServiceImpl implements PollQuestionService {

    private final PollQuestionRepository pollQuestionRepository;

    private final PollRepository pollRepository;

    private final ServiceMapper mapper;

    private static final String POLL_WITH_ID_NOT_FOUND = "Poll with 'id: %d' is not found";

    private static final String QUESTION_WITH_ID_NOT_FOUND = "Question with 'id: %d' is not found";

    @Transactional
    @Override
    public PollQuestionDto createPollQuestion(Long pollId, PollQuestionDto pollQuestionDto) {
        var poll = pollRepository.findById(pollId)
                .filter(Poll::getEnabled)
                .orElseThrow(() -> new NotFoundHomeException(
                        String.format(POLL_WITH_ID_NOT_FOUND, pollId)));


        PollQuestion question = fillMultipleQuestionType(pollQuestionDto);

        question.setPoll(poll);
        question.setEnabled(true);

        pollQuestionRepository.save(question);
        return mapper.convert(question, PollQuestionDto.class);
    }

    @Transactional
    @Override
    public PollQuestionDto updatePollQuestion(Long pollId, Long id, PollQuestionDto updatePollQuestionDto) {
        var poll = pollRepository.findById(pollId)
                .filter(Poll::getEnabled)
                .filter(poll1 -> poll1.getStatus().equals(PollStatus.DRAFT))
                .orElseThrow(() ->
                        new NotFoundHomeException(
                                String.format(POLL_WITH_ID_NOT_FOUND, pollId)));

        PollQuestion toUpdate = poll.getPollQuestions().stream()
                .filter(question -> question.getId().equals(id)).findFirst()
                .orElseThrow(() ->
                        new NotFoundHomeException(
                                String.format(QUESTION_WITH_ID_NOT_FOUND, id)));


        if (updatePollQuestionDto.getQuestion() != null) {
            toUpdate.setQuestion(updatePollQuestionDto.getQuestion());
        }

        PollQuestionTypeDto existingQuestionType = mapper.convert(toUpdate.getType(), PollQuestionTypeDto.class);

        if (existingQuestionType == updatePollQuestionDto.getType()) {
            return updateQuestion(toUpdate, updatePollQuestionDto);
        } else {
            throw new TypeOfTheContactDoesntMatchHomeException("Type of the question doesn't match");
        }
    }

    private PollQuestionDto updateQuestion(PollQuestion pollQuestion, PollQuestionDto updatePollQuestionDto) {
        if (pollQuestion.getType().equals(PollQuestionType.MULTIPLE_CHOICE)) {
            return updateMultiChoiceQuestion((MultipleChoiceQuestion) pollQuestion, updatePollQuestionDto);
        } else if (pollQuestion.getType().equals(PollQuestionType.ADVICE)) {
            return updateAdviceChoiceQuestion((AdviceChoiceQuestion) pollQuestion);
        } else {
            throw new NotFoundHomeException("Type of the question is not found");
        }
    }

    private PollQuestionDto updateMultiChoiceQuestion(MultipleChoiceQuestion multipleChoiceQuestion,
                                                      PollQuestionDto pollQuestionDto) {
        if (pollQuestionDto.getAnswerVariants() != null) {
            for (var i = 0; i < multipleChoiceQuestion.getAnswerVariants().size(); i++) {
                multipleChoiceQuestion.getAnswerVariants().get(i).setAnswer(
                        pollQuestionDto.getAnswerVariants().get(i).getAnswer());
            }
        }

        if (pollQuestionDto.getMaxAnswerCount() != null) {
            multipleChoiceQuestion.setMaxAnswerCount(pollQuestionDto.getMaxAnswerCount());
        }

        pollQuestionRepository.save(multipleChoiceQuestion);

        return mapper.convert(multipleChoiceQuestion, PollQuestionDto.class);
    }

    private PollQuestionDto updateAdviceChoiceQuestion(AdviceChoiceQuestion adviceChoiceQuestion) {

        pollQuestionRepository.save(adviceChoiceQuestion);

        return mapper.convert(adviceChoiceQuestion, PollQuestionDto.class);
    }

    private PollQuestion fillMultipleQuestionType(PollQuestionDto pollQuestionDto) {
        if (pollQuestionDto.getType().equals(PollQuestionTypeDto.MULTIPLE_CHOICE)) {
            var multipleChoiceQuestion = mapper.convert(pollQuestionDto, MultipleChoiceQuestion.class);
            multipleChoiceQuestion.getAnswerVariants().forEach(element ->
                    element.setMultipleChoiceQuestion(multipleChoiceQuestion));
            return multipleChoiceQuestion;
        }
        return mapper.convert(pollQuestionDto, AdviceChoiceQuestion.class);
    }

    @Override
    public void deactivatePollQuestion(Long pollId, Long pollQuestionId) {
        PollQuestion toDelete = pollQuestionRepository.findById(pollQuestionId)
                .filter(PollQuestion::getEnabled)
                .filter(question -> question.getPoll().getId().equals(pollId))
                .orElseThrow(() ->
                        new NotFoundHomeException(
                                String.format(POLL_WITH_ID_NOT_FOUND, pollQuestionId)));

        toDelete.setEnabled(false);
        pollQuestionRepository.save(toDelete);
    }

    @Override
    public Page<PollQuestionDto> findAll(Integer pageNumber,
                                         Integer pageSize,
                                         Specification<PollQuestion> specification) {
        specification = specification
                .and((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
                        .equal(root.get("poll").get("enabled"), true));
        return pollQuestionRepository.findAll(specification, PageRequest.of(pageNumber - 1, pageSize))
                .map(pollQuestion -> mapper.convert(pollQuestion, PollQuestionDto.class));
    }
}
