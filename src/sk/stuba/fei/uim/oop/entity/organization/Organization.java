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
        // TODO prerobit na extrahovanie z mapy???
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
            // TODO test
            returnSet.add(project);
        }
        return returnSet;
    }

    @Override
    public void registerProjectInOrganization(ProjectInterface project) {

        if (project.getApplicant() == null) { // TODO test
            project.setApplicant(this);
        }

        this.projects.add(project);
    }

    @Override
    public int getProjectBudget(ProjectInterface pi) {
        return projectFunding.get(pi);  // TODO test if total or year
    }

    @Override
    public int getBudgetForAllProjects() {
        return this.totalBudget;
    }

    @Override
    abstract public void projectBudgetUpdateNotification(ProjectInterface pi, int year, int budgetForYear);
    // TODO navysit projektu yearBudget, ci totalBudget?



    public Organization() { // TODO prehodit navrch
        this.employment = new HashMap<>();
        this.projects = new HashSet<>();
        this.projectFunding = new HashMap<>();
        this.totalBudget = Constants.COMPANY_INIT_OWN_RESOURCES;
    }
}
