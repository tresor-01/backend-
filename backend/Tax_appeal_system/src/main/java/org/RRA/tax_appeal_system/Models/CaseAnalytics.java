package org.RRA.tax_appeal_system.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "case_analytics")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    private String date;
    private int approved;
    private int rejected;
}
