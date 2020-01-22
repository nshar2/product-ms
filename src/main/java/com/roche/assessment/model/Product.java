package com.roche.assessment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "product")
public class Product {

    @Id
    @NotNull
    private String sku;

    @NotNull
    private String name;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Date date;

    @JsonIgnore
    @Builder.Default
    private boolean deletedFlag = false;
}
