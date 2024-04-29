package sk.stuba.fei.uim.oop.entity.organization;

import sk.stuba.fei.uim.oop.entity.grant.ProjectInterface;
import sk.stuba.fei.uim.oop.entity.people.PersonInterface;
import sk.stuba.fei.uim.oop.utility.Constants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

abstract public class Organization implements OrganizationInterface {

    private String name;
    private Set<PersonInterface> employees;
    private HashMap<PersonInterface, Integer> employment;
    private Set<ProjectInterface> projects;
    private Set<ProjectInterface> projectsRunning;
    private int budget;
    private HashMap<ProjectInterface, Integer> projectFunding;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void addEmployee(PersonInterface p, int employment) {
        this.employment.put(p, employment);
        this.employees.add(p);
    }

    @Override
    public Set<PersonInterface> getEmployees() {
        return employees;
    }

    @Override
    public int getEmploymentForEmployee(PersonInterface p) {
        return employment.get(p);
    }

    @Override
    public Set<ProjectInterface> getAllProjects() {
        return projects;
    }

    @Override
    public Set<ProjectInterface> getRunningProjects(int year) {
        Set<ProjectInterface> returnSet = new HashSet<>();
        for (ProjectInterface project : projects) {
            if (year >= project.getStartingYear() || year <= project.getEndingYear()) {
                returnSet.add(project);
            }
        }
        return returnSet;
    }

    @Override
    public void registerProjectInOrganization(ProjectInterface project) {

        // Každý projekt musí obsahovať informáciu o organizácii, ktorá projekt podáva
        if (project.getApplicant() == null) {
            project.setApplicant(this);
        }

        this.projects.add(project);
    }

    @Override
    public int getProjectBudget(ProjectInterface pi) {
        return projectFunding.get(pi);
    }

    @Override
    public int getBudgetForAllProjects() {
        return this.budget;
    }

    @Override
    public void projectBudgetUpdateNotification(ProjectInterface pi, int year, int budgetForYear) {
        // TODO navysit projektu yearBudget, ci totalBudget?

        int projectYearlyBudget = pi.getBudgetForYear(year);

        // v tom pripade prerobit set projektov na mapu a pridat own funding

        if (this.budget > projectYearlyBudget ) {
            this.projectFunding.put(pi, projectYearlyBudget*2);
            this.budget -= projectYearlyBudget;
        } else {
            this.projectFunding.put(pi, this.budget);
            this.budget = 0;
        }
    }

    public Organization() {
        this.employment = new HashMap<>();
        this.employees = new HashSet<>();
        this.projects = new HashSet<>();
        this.projectFunding = new HashMap<>();
        this.budget = Constants.COMPANY_INIT_OWN_RESOURCES;
    }
}
