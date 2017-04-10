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
public class Cellar {

    private Long id;
    private String name;
    private String address;
    private int wineCapacity;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getWineCapacity() {
        return wineCapacity;
    }

    public void setWineCapacity(int wineCapacity) {
        this.wineCapacity = wineCapacity;
    }

    @Override
    public int hashCode() {
        int hash = 7;
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

        final Cellar other = (Cellar) obj;

        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        //return "Cellar{" + "id=" + id + ", name=" + name + ", address=" + address + ", wineCapacity=" + wineCapacity + '}';
        return name + ", " + address + ", Capacity: " + wineCapacity; 
    }
}
