package com.lolduo.duo.v2.entity.detail;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "spell_comb")
public class SpellCombEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "spell1")
    private Long spell1;

    @Column(name = "spell2")
    private Long spell2;

    public SpellCombEntity(Long spell1, Long spell2) {
        this.spell1 = spell1;
        this.spell2 = spell2;
    }
}
