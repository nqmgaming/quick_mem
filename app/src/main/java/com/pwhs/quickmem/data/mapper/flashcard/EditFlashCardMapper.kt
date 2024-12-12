package com.pwhs.quickmem.data.mapper.flashcard

import com.pwhs.quickmem.data.dto.flashcard.EditFlashCardDto
import com.pwhs.quickmem.domain.model.flashcard.EditFlashCardModel

fun EditFlashCardDto.toModel() = EditFlashCardModel(
    term = term.trim(),
    definition = definition.trim(),
    definitionImageURL = definitionImageURL,
    hint = hint?.trim(),
    explanation = explanation?.trim(),
)

fun EditFlashCardModel.toDto() = EditFlashCardDto(
    term = term.trim(),
    definition = definition.trim(),
    definitionImageURL = definitionImageURL,
    hint = hint?.trim(),
    explanation = explanation?.trim(),
)