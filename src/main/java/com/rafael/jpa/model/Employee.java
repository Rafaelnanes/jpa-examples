package com.rafael.jpa.model;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employee")
public class Employee {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;
  private String name;

  /**
   * Using it as lazy it will be load according the transactions,
   * if put it to EAGER every time this entity is loaded the entity manager will query for company too
   */
  @ManyToOne(fetch = FetchType.LAZY)
  private CompanyLazy company;

  public Employee(String name, CompanyLazy company) {
    this.name = name;
    this.company = company;
  }

}
