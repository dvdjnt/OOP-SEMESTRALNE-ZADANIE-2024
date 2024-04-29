package sk.stuba.fei.uim.oop.entity.grant;

import sk.stuba.fei.uim.oop.entity.organization.OrganizationInterface;
import sk.stuba.fei.uim.oop.entity.people.PersonInterface;
import sk.stuba.fei.uim.oop.utility.Constants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Project implements ProjectInterface {
    private String name;
    private int id; // TODO IMPORTANT!!!
    private int startingYear;
    private int endingYear;
    private int yearBudget;
    private int duration;
    private int totalBudget;
    private Map<Integer, Integer> funding;
    private OrganizationInterface organization;
    private Set<PersonInterface> participants;

    @Override
    public String getProjectName() {
        return this.name;
    }

    @Override
    public void setProjectName(String name) {
        this.name = name;
    }

    @Override
    public int getStartingYear() {
        return this.startingYear;
    }

    @Override
    public void setStartingYear(int year) {
        this.startingYear = year;
        this.endingYear = this.startingYear + Constants.PROJECT_DURATION_IN_YEARS - 1;
        this.duration = this.endingYear - this.startingYear + 1;
    }

    @Override
    public int getEndingYear() {
        return this.endingYear;
    }

    @Override
    public int getBudgetForYear(int year) {
        return this.funding.get(year);
    }

    @Override
    public void setBudgetForYear(int year, int budget) {
        this.funding.put(year, budget);
        this.totalBudget += budget; // v pripade nerovnomerneho rozdelenia budgetu napriec rokmi
        this.organization.projectBudgetUpdateNotification(this, year, budget);
    }

    @Override
    public int getTotalBudget() {
        return this.totalBudget;
    }

    @Override
    public void addParticipant(PersonInterface participant) {
        Set<PersonInterface> employees = this.organization.getEmployees();

        if (employees.contains(participant)) {
            this.participants.add(participant);
        }
    }

    @Override
    public Set<PersonInterface> getAllParticipants() {
        return this.participants;
    }

    @Override
    public OrganizationInterface getApplicant() {
        return this.organization;
    }

    @Override
    public void setApplicant(OrganizationInterface applicant) {
        this.organization = applicant;
    }

    public Project(String name, OrganizationInterface organization, Set<PersonInterface> participants) {
        this.name = name;
        this.organization = organization;
        this.participants = participants;
    }

    public Project(String name, OrganizationInterface organization) {
        this.name = name;
        this.organization = organization;
        this.participants = new HashSet<>();
    }

    public Project(String name) {
        this.name = name;
        this.participants = new HashSet<>();
    }

    public Project() {
        this.participants = new HashSet<>();
        this.funding = new HashMap<>();
    }
}
