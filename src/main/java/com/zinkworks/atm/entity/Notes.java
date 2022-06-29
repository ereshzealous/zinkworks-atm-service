package com.zinkworks.atm.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notes")
@Data
public class Notes {

  @Id
  @GeneratedValue(generator="system-uuid")
  @GenericGenerator(name="system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
  private UUID id;

  @Column(name = "note")
  private Integer note;

  @Column(name = "count")
  private Integer count;

  @Column(name = "created_at", updatable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(name = "updated_at", insertable = false)
  @UpdateTimestamp
  private LocalDateTime updatedAt;

}
