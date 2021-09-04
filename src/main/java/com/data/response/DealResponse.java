package com.data.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DealResponse {
    private int id;
    private String dealid;
    private String fromISOCODE;//currency code
    private String toISOCODE;//Currency Code
    private String dealtime;
    private String dealamount;
}
