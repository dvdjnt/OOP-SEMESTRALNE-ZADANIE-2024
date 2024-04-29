package sk.stuba.fei.uim.oop.entity.organization;

import sk.stuba.fei.uim.oop.entity.grant.ProjectInterface;

public class Company extends Organization {

    @Override
    public void projectBudgetUpdateNotification(ProjectInterface pi, int year, int budgetForYear) {
        int projectYearlyBudget = pi.getBudgetForYear(year);

        if (this.totalBudget > projectYearlyBudget ) {
            this.projectFunding.put(pi, projectYearlyBudget*2);
            this.totalBudget -= projectYearlyBudget;
        } else {
            this.projectFunding.put(pi, this.totalBudget);
            this.totalBudget = 0;
        }
    }

    public Company() {
        super();
    }


}
