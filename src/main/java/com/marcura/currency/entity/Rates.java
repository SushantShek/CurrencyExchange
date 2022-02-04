package com.marcura.currency.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import java.util.Map;

@Entity
@Table(name = "rates")
@Getter
@Setter
@NoArgsConstructor
public class Rates {

    @Id
    @Column(name = "id")
    private String id;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "rates_value_mapping",
            joinColumns = {@JoinColumn(name = "rates_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "country_id")
    @Column(name = "rate")
    @BatchSize(size = 20)
    private Map<String, Double> itemPriceMap;


    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "country_counter_mapping",
            joinColumns = {@JoinColumn(name = "counter_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "country")
    @Column(name = "count")
    @BatchSize(size = 20)
    private Map<String, Integer> counter;

    public Rates(String date, Map<String, Double> itemPriceMap, Map<String, Integer> countermap) {
        this.id = date;
        this.itemPriceMap = itemPriceMap;
        this.counter = countermap;
    }
}