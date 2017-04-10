/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.adamkrajcik.winecellars;

/**
 *
 * @author Adam Krajcik(422636)
 */
public class Wine {

    private Long id;
    private String name;
    private String country;
    private short year;
    private int quantity;
    private WineType type;

    public Wine() {

    }

    public WineType getType() {
        return type;
    }

    public void setType(WineType type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public short getYear() {
        return year;
    }

    public void setYear(short year) {
        this.year = year;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public int hashCode() {

        int hash = 11;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Wine other = (Wine) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Wine{" + "id=" + id + ", name=" + name + ", country=" + country + ", year=" + year + ", quantity=" + quantity + '}';
    }
}
