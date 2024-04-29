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
    private Map<ProjectInterface, Integer> projectFundingMap;    // mapa projektov na urcenie fundingu
    private Map<PersonInterface, Integer> applicantMap;    // mapa aplikantov na vyratanie zavazku

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
        this.state = GrantState.STARTED;
    }

    @Override
    public void evaluateProjects() {
        this.state = GrantState.EVALUATING;

        this.projectFundingMap = new LinkedHashMap<>();
        this.applicantMap = new HashMap<>();

        // inicializacia map
        for (ProjectInterface project : this.registeredProjects) {
            projectFundingMap.put(project, 0); // defaultne bez fundingu
            Set<PersonInterface> projectParticipants = project.getAllParticipants();

            for (PersonInterface participant : projectParticipants) {
                applicantMap.put(participant, 0);
            }
        }

        // OVERVIEW
        // vysetrovanie kazdeho aplikanta v ramci velkosti zavazku v ramci beziacich grantov agentury
        // musime pozriet granty z PROJECT_DURATION_IN_YEARS rokov, pozriet vsetky schvalene projekty, vyfiltrovat
        // ktore este trvaju, nacitat vsetkych ludi z tohto projektu a vypocitat kolko uz cerpaju zo zavazkov


        // nacitat projekty z grantov za poslednych PROJECT_DURATION_IN_YEARS rokov
        Set<ProjectInterface> allPreviousProjects = getAllPreviousProjectsFromAgency(this.agency, this.year);

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
        projectFundingMap = checkSolvingCapacity(projectFundingMap, applicantMap);

        // v tomto bode mame vyfiltrovane projekty - zostali nam iba projekty fit for funding
        // urcovanie fundingu projektom
        projectFundingMap = assignFunding(projectFundingMap);

//        this.registeredFundedProjects = new HashSet<>(projectFundingMap.keySet());
    }

    @Override
    public void closeGrant() {

        // notifikacia kazdeho projektu, po roku, priradenie fundingu
        for (ProjectInterface project : this.projectFundingMap.keySet()) {
            int duration = project.getEndingYear() - project.getStartingYear();
            int funding = projectFundingMap.get(project) / duration;

//            projectFundingMap.replace(project, funding); // update map for getter // TODO getBudgetForProject = total?

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
        this.projectFundingMap = new HashMap<>();
        this.applicantMap = new HashMap<>();
    }

    public Set<ProjectInterface> getAllPreviousProjectsFromAgency(AgencyInterface agency, int year) {

        Set<ProjectInterface> returnSet = new HashSet<>();
        for (int i = year-1; i > (year - Constants.PROJECT_DURATION_IN_YEARS); i--) {
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

    public Map<ProjectInterface, Integer> checkSolvingCapacity(Map<ProjectInterface, Integer> originalProjectMap,
                                                                         Map<PersonInterface, Integer> people) {
        // pozerame, ci mozu byt projekty uznane, ak ano, projektu priradime TRUE
        LinkedHashMap<ProjectInterface, Integer> returnMap = new LinkedHashMap<>(originalProjectMap);

        for (ProjectInterface project : returnMap.keySet() ) {
            Set<PersonInterface> applicants = project.getAllParticipants();
            OrganizationInterface org = project.getApplicant();

            for (PersonInterface person : applicants) {

                if (people.get(person) + org.getEmploymentForEmployee(person) <= Constants.MAX_EMPLOYMENT_PER_AGENCY) {
                    // fit for funding
                    returnMap.replace(project, 1);  // TODO nepouzivat 1, visi to v budget a je to nepekne
                }
            }
        }
        return returnMap;
    }

    public Map<ProjectInterface, Integer> assignFunding(Map<ProjectInterface, Integer> projectMapInput) {

        Map<ProjectInterface, Integer> projectMap = new HashMap<>(projectMapInput);

        // working with only projects fit for funding
        int size = 0;
        for (ProjectInterface project : projectMap.keySet()) {
            if (projectMap.get(project) != 0) {
                size++;
            }
        }

        int fundedProjectsAmount;

        if (size == 1) {
            fundedProjectsAmount = 1;
        } else {
            fundedProjectsAmount = size / 2;
        }

        int counter = 0;
        int projectFunding = this.totalBudget / fundedProjectsAmount;

        for (ProjectInterface project : projectMap.keySet() ) {
            if (projectMap.get(project) == 0) {
                continue;
            }
            if (counter < fundedProjectsAmount) {
                // clovek moze dostat dva projekty ktore sa kapacitne vylucuju
                projectMap.replace(project, projectFunding);
                counter++;
            }
        }
        return projectMap;
    }
}
