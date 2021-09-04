package com.data.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DealDetail {
    @Id
    private Long dealdetailid;
    private String BatchNo;
    private Date TranDate;
}
