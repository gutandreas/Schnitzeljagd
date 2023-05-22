package com.bezkoder.spring.thymeleaf.entity;

import javax.persistence.*;
import java.time.Duration;
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
  private Integer postennummer;

  @Column(nullable = false)
  private boolean fertig;

  @Column(nullable = true)
  private LocalDateTime start;

  @Column(nullable = true)
  private LocalDateTime stop;

  @Column
  private Duration duration;



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

  public void setStop(LocalDateTime stop) {
    this.stop = stop;
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

  public int getPostennummer() {
    return postennummer;
  }

  public void setPostennummer(int postennummer) {
    this.postennummer = postennummer;
  }

  @Override
  public String toString() {
    return "User [id=" + id + ", uuid=" + code + ", vorname=" + vorname + ", nachname=" + nachname + ", postennummer=" + postennummer + ", start=" + start + ", stop=" + stop + ", fertig=" + fertig + "]";
  }

}
