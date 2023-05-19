package com.bezkoder.spring.thymeleaf.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Column
  private String code;

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

  public void setCode(String code) {
    this.code = code;
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
    return "Tutorial [id=" + id + ", uuid=" + code + ", vorname=" + vorname + ", nachname=" + nachname + ", start=" + start + ", stop=" + stop + ", fertig=" + fertig + "]";
  }

}
