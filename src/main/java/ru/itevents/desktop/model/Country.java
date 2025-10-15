package ru.itevents.desktop.model;

public class Country {
    private Long id;
    private String name;
    private String englishName;
    private String isoAlpha2;
    private Integer isoNumeric;

    public Country() {
    }

    public Country(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Country(Long id, String name, String englishName, String isoAlpha2, Integer isoNumeric) {
        this.id = id;
        this.name = name;
        this.englishName = englishName;
        this.isoAlpha2 = isoAlpha2;
        this.isoNumeric = isoNumeric;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getIsoAlpha2() {
        return isoAlpha2;
    }

    public void setIsoAlpha2(String isoAlpha2) {
        this.isoAlpha2 = isoAlpha2;
    }

    public Integer getIsoNumeric() {
        return isoNumeric;
    }

    public void setIsoNumeric(Integer isoNumeric) {
        this.isoNumeric = isoNumeric;
    }

    @Override
    public String toString() {
        return name;
    }
}
