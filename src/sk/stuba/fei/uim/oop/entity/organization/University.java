package sk.stuba.fei.uim.oop.entity.organization;

import sk.stuba.fei.uim.oop.entity.grant.ProjectInterface;
import sk.stuba.fei.uim.oop.entity.people.PersonInterface;
import sk.stuba.fei.uim.oop.utility.Constants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class University extends Organization {
//    private String name;
//    private Set<PersonInterface> employees;
//    private HashMap<PersonInterface, Integer> employment;
//    private Set<ProjectInterface> projects;
//    private Set<ProjectInterface> projectsRunning;
//
//    @Override
//    public String getName() {
//        return this.name;
//    }
//
//    @Override
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    @Override
//    public void addEmployee(PersonInterface p, int employment) {
//        this.employment.put(p, employment);
//        this.employees.add(p);
//    }
//
//    @Override
//    public Set<PersonInterface> getEmployees() {
//        return employees;
//    }
//
//    @Override
//    public int getEmploymentForEmployee(PersonInterface p) {
//        return employment.get(p);
//    }
//
//    @Override
//    public Set<ProjectInterface> getAllProjects() {
//        return projects;
//    }
//
//    @Override
//    public Set<ProjectInterface> getRunningProjects(int year) {
//        Set<ProjectInterface> returnSet = new HashSet<>();
//        for (ProjectInterface project : projects) {
//            if (year >= project.getStartingYear() || year <= project.getEndingYear()) {
//                returnSet.add(project);
//            }
//        }
//        return returnSet;
//    }
//
//    @Override
//    public void registerProjectInOrganization(ProjectInterface project) {
//        this.projects.add(project);
//    }
//
//    @Override
//    public int getProjectBudget(ProjectInterface pi) {
//        return pi.getTotalBudget();
//    }
//
//    @Override
//    public int getBudgetForAllProjects() {
//        return 0;
//    }
//
//    @Override
//    public void projectBudgetUpdateNotification(ProjectInterface pi, int year, int budgetForYear) {
//    }
//
//    public University() {
//        this.employment = new HashMap<>();
//        this.employees = new HashSet<>();
//        this.projects = new HashSet<>();
//    }

    public University() {
        super();
    }
}

