package com.zinkworks.atm.repository;


import com.zinkworks.atm.entity.Notes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotesRepository extends JpaRepository<Notes, String> {
  List<Notes> findAllByOrderByNoteDesc();
}
