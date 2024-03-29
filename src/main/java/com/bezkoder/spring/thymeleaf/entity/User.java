package com.bezkoder.spring.thymeleaf.entity;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Access(AccessType.FIELD)
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

  @ElementCollection
  private List<Integer> postenListMixed;

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

  public void calculateDuration(){
    duration = Duration.between(start, stop);
  }


  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getCode() {
    return code;
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

  public void setPostenListMixed(List<Integer> postenListMixed) {
    this.postenListMixed = postenListMixed;
  }

  public List<Integer> getPostenListMixed() {
    return postenListMixed;
  }

  public int getPostennummer() {
    return postennummer;
  }

  public void setPostennummer(int postennummer) {
    this.postennummer = postennummer;
  }

  public Duration getDuration() {
    return duration;
  }

  public LocalDateTime getStart() {
    return start;
  }



  public String getDurationAsFormattedString(){
    long durationInSeconds = duration.getSeconds();
    return String.format("%d:%02d:%02d", durationInSeconds / 3600, (durationInSeconds % 3600) / 60, (durationInSeconds % 60));
  }

  public int getNextPostenNumber(){
    return postenListMixed.get(postennummer);
  }

  @Override
  public String toString() {
    return "User [id=" + id + ", uuid=" + code + ", vorname=" + vorname + ", nachname=" + nachname + ", postennummer=" + postennummer + ", start=" + start + ", stop=" + stop + ", fertig=" + fertig + "]";
  }

}
