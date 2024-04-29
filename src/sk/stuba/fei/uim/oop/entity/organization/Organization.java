package sk.stuba.fei.uim.oop.entity.organization;

import sk.stuba.fei.uim.oop.entity.grant.ProjectInterface;
import sk.stuba.fei.uim.oop.entity.people.PersonInterface;
import sk.stuba.fei.uim.oop.utility.Constants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

abstract public class Organization implements OrganizationInterface {

    private String name;
    private HashMap<PersonInterface, Integer> employment;
    private Set<ProjectInterface> projects;
    protected int totalBudget;
    protected HashMap<ProjectInterface, Integer> projectFunding;

    public Organization() {
        this.employment = new HashMap<>();
        this.projects = new HashSet<>();
        this.projectFunding = new HashMap<>();
        this.totalBudget = Constants.COMPANY_INIT_OWN_RESOURCES;
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
    public void addEmployee(PersonInterface p, int employment) {
        this.employment.put(p, employment);
    }

    @Override
    public Set<PersonInterface> getEmployees() {
        return employment.keySet();
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
            if (year < project.getStartingYear()) {
                continue;
            }
            if (year > project.getEndingYear()) {
                continue;
            }
            returnSet.add(project);
        }
        return returnSet;
    }

    @Override
    public void registerProjectInOrganization(ProjectInterface project) {

        if (project.getApplicant() == null) {
            project.setApplicant(this);
        }

        this.projects.add(project);
    }

    @Override
    public int getProjectBudget(ProjectInterface pi) {
        if (!this.projects.contains(pi)) {
            return 0;
        }

        return projectFunding.get(pi)+pi.getTotalBudget();
    }

    @Override
    public int getBudgetForAllProjects() {
        int total = 0;

        for (ProjectInterface project : this.projects) {
            total += project.getTotalBudget();
        }

        return total;
    }

    @Override
    abstract public void projectBudgetUpdateNotification(ProjectInterface pi, int year, int budgetForYear);

}
