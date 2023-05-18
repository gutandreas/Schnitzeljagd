package com.bezkoder.spring.thymeleaf.entity;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Column
  private UUID uuid;

  @Column(length = 20, nullable = false)
  private String vorname;

  @Column(length = 20, nullable = false)
  private String nachname;

  @Column(nullable = false)
  private boolean fertig;

  @Column(nullable = true)
  private LocalDateTime start;

  @Column(nullable = true)
  private LocalDateTime stop;



  public User() {

  }



  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public void setStart(LocalDateTime start) {
    this.start = start;
  }

  public String getVorname() {
    return vorname;
  }

  public void setVorname(String vorname) {
    this.vorname = vorname;
  }

  public String getNachname() {
    return nachname;
  }

  public void setNachname(String nachname) {
    this.nachname = nachname;
  }

  public boolean isFertig() {
    return fertig;
  }

  public void setFertig(boolean fertig) {
    this.fertig = fertig;
  }

  @Override
  public String toString() {
    return "Tutorial [id=" + id + ", uuid=" + uuid + ", vorname=" + vorname + ", nachname=" + nachname + ", start=" + start + ", stop=" + stop + ", fertig=" + fertig + "]";
  }

}
