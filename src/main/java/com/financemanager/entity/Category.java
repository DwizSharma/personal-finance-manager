package com.financemanager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "categories")
public class Category {

    public enum Type { INCOME, EXPENSE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name; // e.g. Salary, Groceries, Rent, Entertainment

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    /** Small icon/emoji key used by the UI to visually tag this category. */
    @Column(length = 30)
    private String icon = "tag";

    public Category() {}

    public Category(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public Category(String name, Type type, String icon) {
        this.name = name;
        this.type = type;
        this.icon = icon;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
}