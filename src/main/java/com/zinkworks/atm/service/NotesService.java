package com.zinkworks.atm.service;

import com.zinkworks.atm.common.IErrorMessages;
import com.zinkworks.atm.common.IErrorStatus;
import com.zinkworks.atm.dto.NoteDenomination;
import com.zinkworks.atm.entity.Notes;
import com.zinkworks.atm.exceptions.DataValidationException;
import com.zinkworks.atm.repository.NotesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotesService {

  private final NotesRepository notesRepository;

  public List<Notes> getAllNotesInATM() {
    return notesRepository.findAllByOrderByNoteDesc();
  }

  public List<Notes> verifyATMForNotes(Double amount) {
    List<Notes> notes = getAllNotesInATM();
    double totalAmount = notes.stream().mapToDouble(v -> v.getNote() * v.getCount()).sum();
    if (totalAmount < amount) {
      throw new DataValidationException(IErrorMessages.REQUESTED_AMOUNT_IS_NOT_AVAILABLE_IN_ATM, IErrorStatus.DATA_VALIDATION);
    }
    return notes;
  }

  public List<NoteDenomination> generateNotesDenomination(Double amount, List<Notes> notes) {
    List<NoteDenomination> noteDenominations = notes.stream().map(data -> new NoteDenomination(data.getNote(), NumberUtils.INTEGER_ZERO)).collect(Collectors.toList());
    double amountAvailable = notes.stream().mapToDouble(v -> v.getNote() * v.getCount()).sum();
    Double amountToDispense = amount;
    if (amountAvailable >= amountToDispense) {
      for (int i = 0; i < notes.size(); i++) {
        Integer exactCountOfNote = notes.get(i).getCount();
        if (amount >= notes.get(i).getNote() && exactCountOfNote != 0) {
          int count = (int) (amount / notes.get(i).getNote());
          int difference = notes.get(i).getCount() - count;
          if (difference >= 0) {
            noteDenominations.get(i).setCount(count);
          } else {
            noteDenominations.get(i).setCount(exactCountOfNote);
          }
          amount = amount - noteDenominations.get(i).getCount() * notes.get(i).getNote();
        }
      }
      Double total = noteDenominations.stream().mapToDouble(v -> v.getNote() * v.getCount()).sum();
      if (!total.equals(amountToDispense)) {
        throw new DataValidationException(IErrorMessages.UNAVAILABILITY_OF_DENOMINATION, IErrorStatus.DATA_VALIDATION);
      }
    }
    return noteDenominations;
  }

  public void updateNotes(List<NoteDenomination> noteDenominations, List<Notes> availableNotes) {
    for (int index = 0; index < availableNotes.size(); index++) {
      Notes note = availableNotes.get(index);
      availableNotes.get(index).setCount(note.getCount() - noteDenominations.get(index).getCount());
    }
    notesRepository.saveAll(availableNotes);
  }
}
