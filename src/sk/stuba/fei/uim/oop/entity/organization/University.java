package sk.stuba.fei.uim.oop.entity.organization;

import sk.stuba.fei.uim.oop.entity.grant.ProjectInterface;

public class University extends Organization {

    public University() {
        super();
    }

    @Override
    public void projectBudgetUpdateNotification(ProjectInterface pi, int year, int budgetForYear) {
        this.projectFunding.put(pi, pi.getBudgetForYear(year) );   // ziaden funding od univerzity
    }



}

