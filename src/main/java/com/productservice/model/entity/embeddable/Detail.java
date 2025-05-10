package com.productservice.model.entity.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Detail {

    @Column(name = "specifications")
    private String specifications;

    @Column(name = "support_info")
    private String supportInfo;

    @Embedded
    private ManufacturerInfo manufacturerInfo;
}
