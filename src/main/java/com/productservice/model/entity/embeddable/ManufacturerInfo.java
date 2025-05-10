package com.productservice.model.entity.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ManufacturerInfo {

    @Column(name = "manufacturer_name")
    private String manufacturerName;

    @Column(name = "manufacturer_contact")
    private String manufacturerContact;
}
