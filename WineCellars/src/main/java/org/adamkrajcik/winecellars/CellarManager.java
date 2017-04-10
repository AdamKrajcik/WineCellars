/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.adamkrajcik.winecellars;

import java.util.List;

/**
 *
 * @author Adam Krajcik(422636)
 */
public interface CellarManager {

    void createCellar(Cellar cellar);

    void updateCellar(Cellar cellar);

    void deleteCellar(Cellar cellar);

    Cellar findCellarById(Long id);

    List<Cellar> findAllCellars();
}
