package sk.stuba.fei.uim.oop.entity.organization;

import sk.stuba.fei.uim.oop.entity.grant.ProjectInterface;

public class Company extends Organization {

    public Company() {
        super();
    }

    @Override
    public void projectBudgetUpdateNotification(ProjectInterface pi, int year, int budgetForYear) {
        // pamatame si aj funding pre projekty ktore dostali od grantu 0 v ramci kompletnosti zaznamu
        projectFunding.putIfAbsent(pi, 0);

        int projectYearlyBudget = pi.getBudgetForYear(year);
        int addedValueFromCompany = 0;

        if (this.totalBudget == 0) {
            addedValueFromCompany = 0;
        } else if (this.totalBudget > projectYearlyBudget ) {
            addedValueFromCompany = projectYearlyBudget;
            this.totalBudget -= addedValueFromCompany;
        } else {
            addedValueFromCompany = this.totalBudget;
            this.totalBudget = 0;
        }

        this.projectFunding.put(pi, projectFunding.get(pi) + addedValueFromCompany);
    }




}
