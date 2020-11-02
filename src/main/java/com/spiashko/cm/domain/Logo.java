package com.spiashko.cm.domain;


import com.spiashko.cm.crudbase.entity.BaseJournalEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

/**
 * A Logo.
 */
@Getter
@Setter
@Entity
@Table(name = "logo")
public class Logo extends BaseJournalEntity<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Lob
    @Column(name = "data", nullable = false)
    private byte[] data;

    @Column(name = "data_content_type", nullable = false)
    private String dataContentType;

}
