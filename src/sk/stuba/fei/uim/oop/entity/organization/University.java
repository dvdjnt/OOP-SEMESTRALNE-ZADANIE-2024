package sk.stuba.fei.uim.oop.entity.organization;

import sk.stuba.fei.uim.oop.entity.grant.ProjectInterface;

public class University extends Organization {

    @Override
    public void projectBudgetUpdateNotification(ProjectInterface pi, int year, int budgetForYear) {
        int projectYearlyBudget = pi.getBudgetForYear(year);
        this.projectFunding.put(pi, projectYearlyBudget);   // no additional funding
    }

    public University() {
        super();
    }


}

