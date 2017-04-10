/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.adamkrajcik.winecellars;


import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.DERBY;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 *
 * @author dmwm
 */
@Configuration  //je to konfigurace pro Spring
@EnableTransactionManagement //bude řídit transakce u metod označených @Transactional
public class SpringConfig {
    
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(DERBY)
                .addScript("classpath:createTables.sql")
                .addScript("classpath:testData.sql")
                .build();
    }

    @Bean 
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public CellarManager cellarManager() {
        CellarManagerImpl c = new CellarManagerImpl();
        c.setDataSource(dataSource());
        return c;
    }

    @Bean
    public WineManager wineManager() {
        WineManagerImpl w = new WineManagerImpl();
        w.setDataSource(new TransactionAwareDataSourceProxy(dataSource()));
        return w;
    }
    
    @Bean
    public WineCellarsManager wineCellarsManager() {
        WineCellarsManagerImpl wc = new WineCellarsManagerImpl();
        wc.setDataSource(dataSource());
        return wc;
    }
    
    
}
