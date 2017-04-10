/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.adamkrajcik.winecellars;

import java.util.List;
import java.util.logging.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author dmwm
 */
public class Main {
        final static Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        log.info("zaciname");
        ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
        CellarManager cellarManager = ctx.getBean("cellarManager", CellarManager.class);

        List<Cellar> allCellars = cellarManager.findAllCellars();
        System.out.println("all Cellars = " + allCellars);

    }
}
