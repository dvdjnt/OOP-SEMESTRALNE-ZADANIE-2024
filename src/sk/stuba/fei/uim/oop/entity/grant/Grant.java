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

    @Override
    public String getIdentifier() {
        return this.id;
    }

    @Override
    public void setIdentifier(String identifier) {
        this.id = identifier;   // TODO hash daco
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
        return this.projectFundingMap.get(project);
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
        // TODO zabezpecit aby sa nedal znova otvorit
        //  konkretne cez if projectFundingMap.isEmpty()
        //
        this.state = GrantState.STARTED;
    }

    @Override
    public void evaluateProjects() {
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

        // filtracia na projekty ktore este trvaju
        Set<ProjectInterface> interferingProjects = filterProjects(allPreviousProjects, this.year);

        // z interfering projektov naplnime mapu aplikantov (vycerpanych zavazkov)
        // z kazdeho projektu pozrieme vsetkych ludi, ak su vysetrovani, zistime im zavazok
        applicantMap = addEmploymentToMapFromProjects(interferingProjects, applicantMap);

        // mame mapu vysetrovanych aplikantov kde su spravne hodnoty vycerpanych zavazkov z projektov,
        // ktore interferuju s grantom tento rok.

        // teraz ideme projekt po projekte a pozerame, ci mozu byt projekty uznane
        // ak ano, projektu priradime TRUE
        // neprijatym projektom zostava FALSE v mape
        projectFundingBool = checkSolvingCapacity(projectFundingBool, applicantMap);

        // v tomto bode mame vyfiltrovane projekty - zostali nam iba projekty fit for funding
        // urcovanie fundingu projektom
        projectFundingMap = assignFunding(projectFundingBool);
        // TODO return je mapa s funding values

//        this.registeredFundedProjects = new HashSet<>(projectFundingMap.keySet());
    }

    @Override
    public void closeGrant() {

        // notifikacia kazdeho projektu, po roku, priradenie fundingu
        for (ProjectInterface project : this.projectFundingMap.keySet()) {
            int duration = project.getEndingYear() - project.getStartingYear();
            int funding = projectFundingMap.get(project) / duration;

//            projectFundingMap.replace(project, funding); // update map for getter // TODO floor lebo 58.9 ako int = 58

            for (int i = project.getStartingYear(); i < project.getEndingYear(); i++) {
                project.setBudgetForYear(i,funding);
                this.remainingBudget -= funding;
            }
        }
        this.state = GrantState.CLOSED;
    }

    public Grant() {
//        this.state = GrantState.UNDEFINED
        this.registeredProjects = new HashSet<>();
        this.projectFundingBool = new HashMap<>();
        this.projectFundingMap = new HashMap<>();
        this.applicantMap = new HashMap<>();
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
        // filter given projects, return only those still active and funded in given year
        Set<ProjectInterface> returnSet = new HashSet<>();

        for (ProjectInterface project : projects) {
            if (project.getBudgetForYear(year) == 0) {    // funded projects
                continue;
            }
            if (project.getEndingYear() >= year) {  // still running
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
                    returnMap.replace(project, Boolean.TRUE);  // TODO nepouzivat 1, visi to v budget a je to nepekne
                }
            }
        }
        return returnMap;
    }

    public Map<ProjectInterface, Integer> assignFunding(Map<ProjectInterface, Boolean> projectMap) {

        Map<ProjectInterface, Integer> projectsFitForFunding = new HashMap<>();


        // working with only projects fit for funding
        for (ProjectInterface project : projectMap.keySet()) {
            if (projectMap.get(project)) {
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
        int projectFunding = this.totalBudget / fundedProjectsAmount;

        for (ProjectInterface project: projectsFitForFunding.keySet()) {
            if (counter < fundedProjectsAmount) {
                projectsFitForFunding.replace(project, projectFunding);
            }
            counter++;
        }

//        for (ProjectInterface project : projectsFitForFunding.keySet() ) {
//            if (projectsFitForFunding.get(project) == 0) {
//                continue;
//            }
//            if (counter < fundedProjectsAmount) {
//                // clovek moze dostat dva projekty ktore sa kapacitne vylucuju
//                projectsFitForFunding.replace(project, projectFunding);
//                counter++;
//            }
//        }
        return projectsFitForFunding;
    }
}
