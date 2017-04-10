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
public interface WineManager {

    void createWine(Wine wine);

    void updateWine(Wine wine);

    void deleteWine(Wine wine);

    Wine findWineById(Long id);

    List<Wine> findAllWines();
}
