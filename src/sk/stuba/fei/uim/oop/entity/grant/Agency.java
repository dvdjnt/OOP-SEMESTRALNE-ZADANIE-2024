package sk.stuba.fei.uim.oop.entity.grant;

import sk.stuba.fei.uim.oop.utility.Constants;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Agency implements AgencyInterface{
    private String name;

    private Set<GrantInterface> grantList;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void addGrant(GrantInterface gi, int year) {
        gi.setYear(year);
        this.grantList.add(gi);
    }

    @Override
    public Set<GrantInterface> getAllGrants() {
        return grantList;
    }

    @Override
    public Set<GrantInterface> getGrantsIssuedInYear(int year) {

        Set<GrantInterface> returnSet = new HashSet<>();

        for (GrantInterface grant : this.grantList) {
            if (grant.getYear() == year) {
                returnSet.add(grant);
            }
        }

        return returnSet;
    }

    public Agency(String name) {
        this.name = name;
        this.grantList = new HashSet<>();
    }

    public Agency() {
        this.grantList = new HashSet<>();
    }
}
