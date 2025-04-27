package com.driver.repository;

import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WebSeriesRepository extends JpaRepository<WebSeries,Integer> {

    WebSeries findBySeriesName(String seriesName);
    List<WebSeries> findByProductionHouse(ProductionHouse productionHouse);
}
