package sk.stuba.fei.uim.oop.entity.grant;

import sk.stuba.fei.uim.oop.entity.organization.OrganizationInterface;
import sk.stuba.fei.uim.oop.entity.people.PersonInterface;
import sk.stuba.fei.uim.oop.utility.Constants;

import java.util.*;

public class Grant implements GrantInterface {
    private int year;
    private String id;
    private AgencyInterface agency;
    private int totalBudget;
    private int remainingBudget;
    private GrantState state;
    private Set<ProjectInterface> registeredProjects;
    private Map<ProjectInterface, Boolean> projectFundingBool;    // mapa projektov na urcenie fundingu
    private Map<ProjectInterface, Integer> projectFundingMap;
    private Map<PersonInterface, Integer> applicantMap;    // mapa aplikantov na vyratanie zavazku

    public Grant() {
        this.registeredProjects = new LinkedHashSet<>();
        this.projectFundingBool = new HashMap<>();
        this.projectFundingMap = new HashMap<>();
        this.applicantMap = new HashMap<>();
    }

    @Override
    public String getIdentifier() {
        return this.id;
    }

    @Override
    public void setIdentifier(String identifier) {
        this.id = identifier;
    }

    @Override
    public int getYear() {
        return this.year;
    }

    @Override
    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public AgencyInterface getAgency() {
        return this.agency;
    }

    @Override
    public void setAgency(AgencyInterface agency) {
        this.agency = agency;
    }

    @Override
    public int getBudget() {
        return totalBudget;
    }

    @Override
    public void setBudget(int budget) {
        totalBudget = budget;
        remainingBudget = totalBudget;
    }

    @Override
    public int getRemainingBudget() {
        return remainingBudget;
    }

    @Override
    public int getBudgetForProject(ProjectInterface project) {
        return project.getTotalBudget();

        // budget sa nezhoduje s projectFundingMap pri floatoch - pretoze je budget definovany ako int
//        return this.projectFundingMap.get(project); -> nemozno pouzit
    }

    @Override
    public boolean registerProject(ProjectInterface project) {

        if (this.state != GrantState.STARTED) {
            return false;
        }

        // bez organizacie
        if (project.getApplicant() == null) {
            return false;
        }

        // nesedi rok
        if (project.getStartingYear() != this.year) {
            return false;
        }

        // prazdny riesitelsky kolektiv
        if (project.getAllParticipants().isEmpty()) {
            return false;
        }

        this.registeredProjects.add(project);

        return true;
    }

    @Override
    public Set<ProjectInterface> getRegisteredProjects() {
        return registeredProjects;
    }

    @Override
    public GrantState getState() {
        return this.state;
    }

    @Override
    public void callForProjects() {
        if (this.state != null) {
            // grant is in progress
            return;
        }
        if (!projectFundingMap.isEmpty()) {
            // grant is closed;
            return;
        }
        this.state = GrantState.STARTED;
    }

    @Override
    public void evaluateProjects() {
        if (this.state != GrantState.STARTED) {
            // grant has not started
            return;
        }
        this.state = GrantState.EVALUATING;

        this.projectFundingBool = new LinkedHashMap<>();
        this.applicantMap = new HashMap<>();

        // inicializacia map
        for (ProjectInterface project : this.registeredProjects) {
            projectFundingBool.put(project, Boolean.FALSE); // defaultne bez fundingu
            Set<PersonInterface> projectParticipants = project.getAllParticipants();

            for (PersonInterface participant : projectParticipants) {
                applicantMap.put(participant, 0);
            }
        }

        // nacitavame projekty z grantov za poslednych PROJECT_DURATION_IN_YEARS rokov
        Set<ProjectInterface> allPreviousProjects = getAllPreviousProjectsFromAgency(this.agency, this.year, Constants.PROJECT_DURATION_IN_YEARS);

        // filtracia projektov
        Set<ProjectInterface> interferingProjects = filterProjects(allPreviousProjects, this.year);

        // z interfering projektov naplnime mapu aplikantov (vycerpanych zavazkov vysetrovanych ludi)
        applicantMap = addEmploymentToMapFromProjects(interferingProjects, applicantMap);

        // ideme projekt po projekte a pozerame, ci mozu byt projekty uznane
        projectFundingBool = checkSolvingCapacity(projectFundingBool, applicantMap);

        // v tomto bode mame vyfiltrovane projekty - zostali nam iba projekty fit for funding - urcovanie fundingu projektom
        projectFundingMap = assignFunding(projectFundingBool);

    }

    @Override
    public void closeGrant() {
        if (this.state != GrantState.EVALUATING) {
            // grant has not evaluated projects
            return;
        }

        // notifikacia kazdeho projektu, po roku, priradenie fundingu
        for (ProjectInterface project : this.projectFundingMap.keySet()) {
            double duration = project.getEndingYear() - project.getStartingYear() + 1;
            int funding = (int)(Math.floor(projectFundingMap.get(project) / duration));

            for (int i = project.getStartingYear(); i <= project.getEndingYear(); i++) {
                project.setBudgetForYear(i,funding);
                this.remainingBudget -= funding;
            }
        }

        this.state = GrantState.CLOSED;
    }

    public Set<ProjectInterface> getAllPreviousProjectsFromAgency(AgencyInterface agency, int currentYear, int searchAmountInYears) {

        Set<ProjectInterface> returnSet = new HashSet<>();
        for (int i = currentYear-1; i > (currentYear - searchAmountInYears); i--) {
            Set<GrantInterface> grants = agency.getGrantsIssuedInYear(i);

            for (GrantInterface grant : grants) {
                returnSet.addAll(grant.getRegisteredProjects());
            }
        }

        return returnSet;
    }

    public Set<ProjectInterface> filterProjects(Set<ProjectInterface> projects, int year) {
        // filtrujeme projekty na aktivne a s rovnakym rokom
        Set<ProjectInterface> returnSet = new HashSet<>();

        for (ProjectInterface project : projects) {
            if (project.getBudgetForYear(year) == 0) {    // not-funded projekty
                continue;
            }
            if (project.getEndingYear() < year) {  // inaktivne projekty
                continue;
            }
            returnSet.add(project);
        }
        return returnSet;
    }

    public Map<PersonInterface, Integer> addEmploymentToMapFromProjects(Set<ProjectInterface> projects,
                                                                        Map<PersonInterface, Integer> map) {
        Map<PersonInterface, Integer> returnMap = new LinkedHashMap<>(map);

        for (ProjectInterface project : projects) {
            Set<PersonInterface> people = project.getAllParticipants();

            for (PersonInterface person : people) {
                if (returnMap.containsKey(person)) {
                    // pridame employment k existujucemu zaznamu v mape podla uvazku v organizacii
                    int newEmploymentValue = returnMap.get(person) + project.getApplicant().getEmploymentForEmployee(person);
                    returnMap.replace(person, newEmploymentValue);
                }
            }
        }
        return returnMap;
    }

    public Map<ProjectInterface, Boolean> checkSolvingCapacity(Map<ProjectInterface, Boolean> originalProjectMap,
                                                                         Map<PersonInterface, Integer> people) {

        LinkedHashMap<ProjectInterface, Boolean> returnMap = new LinkedHashMap<>(originalProjectMap);

        for (ProjectInterface project : returnMap.keySet() ) {
            Set<PersonInterface> applicants = project.getAllParticipants();
            OrganizationInterface org = project.getApplicant();

            for (PersonInterface person : applicants) {
                // fit for funding
                if (people.get(person) + org.getEmploymentForEmployee(person) <= Constants.MAX_EMPLOYMENT_PER_AGENCY) {
                    returnMap.replace(project, Boolean.TRUE);
                }
            }
        }
        return returnMap;
    }

    public Map<ProjectInterface, Integer> assignFunding(Map<ProjectInterface, Boolean> projectMapBool) {

        Map<ProjectInterface, Integer> projectsFitForFunding = new HashMap<>();

        // working with only projects fit for funding
        for (ProjectInterface project : projectMapBool.keySet()) {
            if (projectMapBool.get(project)) {
                projectsFitForFunding.put(project, 0);
            }
        }

        int fundedProjectsAmount;
        int size = projectsFitForFunding.size();


        if (size == 1) {
            fundedProjectsAmount = 1;
        } else {
            fundedProjectsAmount = size / 2;
        }

        int counter = 0;
        int projectFunding = 0;

        if (size != 0) {
            projectFunding = this.totalBudget / fundedProjectsAmount;
        }

        for (ProjectInterface project: projectsFitForFunding.keySet()) {
            if (counter < fundedProjectsAmount) {
                projectsFitForFunding.replace(project, projectFunding);
            }
            counter++;
        }

        return projectsFitForFunding;
    }
}
