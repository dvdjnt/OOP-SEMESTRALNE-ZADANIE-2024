package sk.stuba.fei.uim.oop.entity.people;

import sk.stuba.fei.uim.oop.entity.organization.OrganizationInterface;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Person implements PersonInterface{
    private String name;
    private String address;
    private Set<OrganizationInterface> employerList;

    public Person() {
        this.employerList = new HashSet<>();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getAddress() {
        return this.address;
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public void addEmployer(OrganizationInterface organization) {
        this.employerList.add(organization);
    }

    @Override
    public Set<OrganizationInterface> getEmployers() {
        return this.employerList;
    }

}
