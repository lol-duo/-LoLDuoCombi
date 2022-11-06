package com.lolduo.duo.v2.entity.detail;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "rune_comb",indexes = {
        @Index(name="rune_search_index",columnList = "main_rune_concept, main_rune0, sub_rune_concept")})
public class RuneCombEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "main_rune_concept")
    private Long mainRuneConcept;
    @Column(name = "main_rune0")
    private Long mainRune0;
    @Column(name = "sub_rune_concept")
    private Long subRuneConcept;

    @Column(name = "main_rune1")
    private Long mainRune1;
    @Column(name = "main_rune2")
    private Long mainRune2;
    @Column(name = "main_rune3")
    private Long mainRune3;

    @Column(name = "sub_rune1")
    private Long subRune1;
    @Column(name = "sub_rune2")
    private Long subRune2;

    public RuneCombEntity(Long mainRuneConcept, Long mainRune0, Long subRuneConcept, Long mainRune1, Long mainRune2, Long mainRune3, Long subRune1, Long subRune2) {
        this.mainRuneConcept = mainRuneConcept;
        this.mainRune0 = mainRune0;
        this.subRuneConcept = subRuneConcept;
        this.mainRune1 = mainRune1;
        this.mainRune2 = mainRune2;
        this.mainRune3 = mainRune3;
        this.subRune1 = subRune1;
        this.subRune2 = subRune2;
    }
}
