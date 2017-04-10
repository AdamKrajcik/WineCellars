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
public interface WineCellarsManager {

    void putWineInCellar(Wine wine, Cellar cellar);

    void removeWineFromCellar(Wine wine, Cellar cellar);

    Cellar findCellarWithWine(Wine wine);

    List<Wine> findWinesInCellar(Cellar cellar);
}
