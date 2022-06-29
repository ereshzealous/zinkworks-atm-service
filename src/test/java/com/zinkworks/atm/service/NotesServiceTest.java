package com.zinkworks.atm.service;

import com.zinkworks.atm.common.IErrorMessages;
import com.zinkworks.atm.common.IErrorStatus;
import com.zinkworks.atm.dto.NoteDenomination;
import com.zinkworks.atm.entity.Notes;
import com.zinkworks.atm.exceptions.DataValidationException;
import com.zinkworks.atm.repository.NotesRepository;
import com.zinkworks.atm.util.TestDataGenerator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotesServiceTest {

  @InjectMocks
  private NotesService notesService;

  @Mock
  private NotesRepository mockNotesRepository;

  @Test
  public void should_return_all_available_notes_successfully() {
    List<Notes> expectedNotes = TestDataGenerator.generateNotes();
    when(mockNotesRepository.findAllByOrderByNoteDesc()).thenReturn(expectedNotes);
    List<Notes> actualNotes = notesService.getAllNotesInATM();
    assertNotNull(actualNotes);
    assertEquals(expectedNotes.size(), actualNotes.size());
    assertEquals(expectedNotes, actualNotes);
  }

  @Test
  public void should_return_success_when_verified_for_notes_available_in_atm() {
    List<Notes> expectedNotes = TestDataGenerator.generateNotes();
    when(mockNotesRepository.findAllByOrderByNoteDesc()).thenReturn(expectedNotes);
    List<Notes> actualNotes = notesService.verifyATMForNotes(100d);
    assertNotNull(actualNotes);
    assertEquals(expectedNotes.size(), actualNotes.size());
    assertEquals(expectedNotes, actualNotes);
  }

  @Test
  public void should_throw_error_when_verified_for_notes_available_in_atm() {
    List<Notes> expectedNotes = TestDataGenerator.generateNotes();
    when(mockNotesRepository.findAllByOrderByNoteDesc()).thenReturn(expectedNotes);
    DataValidationException exception = assertThrows(DataValidationException.class,
        () ->  notesService.verifyATMForNotes(10000d));
    assertEquals("ATM balance is low. Can't dispense the requested amount.", exception.getMessage());
  }

  @Test
  public void should_return_notes_denomination_successfully() {
    List<Notes> notes = TestDataGenerator.generateNotes();
    Double amount = 175d;
    List<NoteDenomination> noteDenominations = notesService.generateNotesDenomination(amount, notes);
    assertNotNull(noteDenominations);
    assertEquals(notes.size(), noteDenominations.size());
    noteDenominations.forEach(data -> {
      if (data.getNote().equals(50)) {
        assertEquals(3, data.getCount());
      } else if (data.getNote().equals(20)) {
        assertEquals(1, data.getCount());
      } else if (data.getNote().equals(5)) {
        assertEquals(1, data.getCount());
      }
    });
  }

  @Test
  public void should_return_error_for_invalid_denominations() {
    List<Notes> notes = TestDataGenerator.generateNotes();
    Double amount = 201d;
    DataValidationException exception = assertThrows(DataValidationException.class, () -> notesService.generateNotesDenomination(amount, notes));
    assertEquals("ATM Doesn't had requested amount in denominations", exception.getMessage());
  }

  @Test
  public void should_be_successful_on_saving_notes() {
    List<Notes> notes = TestDataGenerator.generateNotes();
    List<NoteDenomination> noteDenominations = TestDataGenerator.generateNoteDenominations();
    notesService.updateNotes(noteDenominations, notes);
    verify(mockNotesRepository, times(1)).saveAll(any());
  }
}
