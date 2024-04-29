package sk.stuba.fei.uim.oop.entity.grant;

import java.util.HashSet;
import java.util.Set;

public class Agency implements AgencyInterface{
    private String name;
    private Set<GrantInterface> grantSet;

    public Agency() {
        this.grantSet = new HashSet<>();
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
    public void addGrant(GrantInterface gi, int year) {
        gi.setYear(year);
        this.grantSet.add(gi);
    }

    @Override
    public Set<GrantInterface> getAllGrants() {
        return grantSet;
    }

    @Override
    public Set<GrantInterface> getGrantsIssuedInYear(int year) {

        Set<GrantInterface> returnSet = new HashSet<>();

        for (GrantInterface grant : this.grantSet) {
            if (grant.getYear() == year) {
                returnSet.add(grant);
            }
        }

        return returnSet;
    }
}
