package org.apache.tuscany.databinding.jaxb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBean {
    private int age;
    private String name;
    private float[] rates = new float[] {1.0f, 2.0f};
    private List<String> notes = new ArrayList<String>();
    private Map<String, Integer> map = new HashMap<String, Integer>();

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getNotes() {
        return notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }

    public float[] getRates() {
        return rates;
    }

    public void setRates(float[] rates) {
        this.rates = rates;
    }

    public Map<String, Integer> getMap() {
        return map;
    }

    public void setMap(Map<String, Integer> map) {
        this.map = map;
    }
}
