package sk.stuba.fei.uim.oop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.stuba.fei.uim.oop.entity.grant.*;
import sk.stuba.fei.uim.oop.entity.organization.Company;
import sk.stuba.fei.uim.oop.entity.organization.OrganizationInterface;
import sk.stuba.fei.uim.oop.entity.organization.University;
import sk.stuba.fei.uim.oop.entity.people.Person;
import sk.stuba.fei.uim.oop.entity.people.PersonInterface;
import sk.stuba.fei.uim.oop.factory.DataFactory;
import sk.stuba.fei.uim.oop.utility.Constants;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class AssignmentInspiredTest {

    private LinkedList<AgencyInterface> agencies;
    private LinkedList<GrantInterface> grants;
    private LinkedList<OrganizationInterface> organizations;
    private LinkedList<ProjectInterface> projects;
    private LinkedList<PersonInterface> persons;

    @BeforeEach
    void setUp() {
        Constants.MAX_EMPLOYMENT_PER_AGENCY = 5;
        Constants.PROJECT_DURATION_IN_YEARS = 4;
        Constants.COMPANY_INIT_OWN_RESOURCES = 1000000;
        agencies = DataFactory.getAgencies(2);
        agencies.get(0).setName("VEGA");
        agencies.get(1).setName("APVV");

        grants = DataFactory.getGrants(4);
        List<Integer> grantBudgets = Arrays.asList(100000, 8000, 100000, 8000);
        List<AgencyInterface> grantAgencies = Arrays.asList(agencies.get(1), agencies.get(0), agencies.get(1), agencies.get(0));
        List<Integer> grantYears = Arrays.asList(2022, 2023, 2024, 2024);
        for (int i = 0; i < grants.size(); i++) {
            GrantInterface grant = grants.get(i);
            grant.setIdentifier(String.valueOf(i + 1));
            grant.setAgency(grantAgencies.get(i));
            grantAgencies.get(i).addGrant(grant, grantYears.get(i));
            grant.setBudget(grantBudgets.get(i));
            grant.setYear(grantYears.get(i));
        }

        List<String> organizationNames = Arrays.asList("STU", "UK", "Eset");
        organizations = new LinkedList<>();
        organizations.addAll(DataFactory.getUniversities(2));
        organizations.addAll(DataFactory.getCompanies(1));
        for (int i = 0; i < organizations.size(); i++) {
            organizations.get(i).setName(organizationNames.get(i));
        }

        List<String> personNames = Arrays.asList("Peter", "Jozef", "Anna", "Karol");
        persons = DataFactory.getPersons(personNames.size());
        for (int i = 0; i < persons.size(); i++) {
            PersonInterface person = persons.get(i);
            person.setName(personNames.get(i));
            person.setAddress("Address " + (i + 1));
        }
        // STU Peter, Jozef
        organizations.get(0).addEmployee(persons.get(0), 2);
        persons.get(0).addEmployer(organizations.get(0));
        organizations.get(0).addEmployee(persons.get(1), 3);
        persons.get(1).addEmployer(organizations.get(0));
        // UK Anna, Karol
        organizations.get(1).addEmployee(persons.get(2), 3);
        persons.get(2).addEmployer(organizations.get(1));
        organizations.get(1).addEmployee(persons.get(3), 1);
        persons.get(3).addEmployer(organizations.get(1));
        // Eset Peter, Anna, Karol
        organizations.get(2).addEmployee(persons.get(0), 4);
        persons.get(0).addEmployer(organizations.get(2));
        organizations.get(2).addEmployee(persons.get(2), 2);
        persons.get(2).addEmployer(organizations.get(2));
        organizations.get(2).addEmployee(persons.get(3), 1);
        persons.get(3).addEmployer(organizations.get(2));

        List<OrganizationInterface> projectOrganizations = Arrays.asList(organizations.get(0), organizations.get(1), organizations.get(2), organizations.get(2), organizations.get(0), organizations.get(1), organizations.get(1));
        List<Integer> projectYears = Arrays.asList(2022, 2023, 2024, 2024, 2024, 2024, 2024);

        projects = DataFactory.getProjects(7);
        for (int i = 0; i < projects.size(); i++) {
            ProjectInterface proj = projects.get(i);
            proj.setProjectName("P" + (i + 1));
            OrganizationInterface applicant = projectOrganizations.get(i);
            proj.setApplicant(applicant);
            applicant.registerProjectInOrganization(projects.get(i));
            proj.setStartingYear(projectYears.get(i));
        }
        projects.get(0).addParticipant(persons.get(0)); // Peter
        projects.get(0).addParticipant(persons.get(1)); // Jozef
        projects.get(1).addParticipant(persons.get(2)); // Anna
        projects.get(1).addParticipant(persons.get(3)); // Karol
        projects.get(2).addParticipant(persons.get(0)); // Peter
        projects.get(3).addParticipant(persons.get(2)); // Anna
        projects.get(3).addParticipant(persons.get(3)); // Karol
        projects.get(4).addParticipant(persons.get(0)); // Peter
        projects.get(4).addParticipant(persons.get(1)); // Jozef
        projects.get(5).addParticipant(persons.get(2)); // Anna
        projects.get(6).addParticipant(persons.get(3)); // Karol
    }

    @Test
    void testAllProjectsPassed() {

        ProjectInterface P1 = projects.get(0);
        GrantInterface grant1 = grants.get(0);
        grant1.callForProjects();
        grant1.registerProject(P1);
        grant1.evaluateProjects();
        grant1.closeGrant();
        // the registered project must obtain the full grant budget
        assertEquals(100000, grant1.getBudgetForProject(P1));

        ProjectInterface P2 = projects.get(1);
        GrantInterface grant2 = grants.get(1);
        grant2.callForProjects();
        grant2.registerProject(P2);
        grant2.evaluateProjects();
        grant2.closeGrant();
        // the registered project must obtain the full grant budget
        assertEquals(8000, grant2.getBudgetForProject(P2));

        ProjectInterface P3 =  projects.get(2);
        GrantInterface grant3 = grants.get(2);
        System.out.println(P3.getTotalBudget());
        grant3.callForProjects();
        grant3.registerProject(P3);
        grant3.evaluateProjects();
        grant3.closeGrant();
        System.out.println(P3.getTotalBudget());
        // the registered project must obtain 0 because Peter is overlapping
        assertEquals(0, grant3.getBudgetForProject(P3));

        ProjectInterface P4 = projects.get(3);
        ProjectInterface P5 = projects.get(4);
        ProjectInterface P6 = projects.get(5);
        ProjectInterface P7 = projects.get(6);

        GrantInterface grant4 = grants.get(3);

        grant4.callForProjects();
        grant4.registerProject(P4);
        grant4.registerProject(P5);
        grant4.registerProject(P6);
        grant4.registerProject(P7);
        grant4.evaluateProjects();
        grant4.closeGrant();

        assertEquals(8000, grant4.getBudgetForProject(P4));
        assertEquals(0, grant4.getBudgetForProject(P5));
        assertEquals(0, grant4.getBudgetForProject(P6));
        assertEquals(0, grant4.getBudgetForProject(P7));

        assertEquals(16000, organizations.get(2).getProjectBudget(P4));

    }

//    @Test
    void personUniqueName(){
        PersonInterface p1 = new Person();
        PersonInterface p2 = new Person();
        ProjectInterface pro1 = new Project();
        OrganizationInterface STU = new University();

        p1.setName("Jozef");
        p2.setName("Jozef");
        STU.setName("STU");
        pro1.setProjectName("pro1");
        pro1.setApplicant(STU);

        p1.addEmployer(STU);
        p2.addEmployer(STU);
        STU.addEmployee(p1, 2);
        STU.addEmployee(p2, 3);
        pro1.addParticipant(p2);
        pro1.addParticipant(p1);

        assertEquals(1, STU.getEmployees().size());
        assertEquals(1, pro1.getAllParticipants().size());
        assertEquals(3, STU.getEmploymentForEmployee(p1));
    }


    @Test
    void grantState() {
        GrantInterface grant = new Grant();
        grant.setBudget(10);
        AgencyInterface ag = new Agency();
        ag.addGrant(grant, 2002);
        grant.setAgency(ag);
        List<GrantState> states = new ArrayList<>();
        states.add(GrantState.STARTED);
        states.add(GrantState.EVALUATING);
        states.add(GrantState.CLOSED);

        assertFalse(Arrays.asList(states).contains(grant.getState()));
        grant.callForProjects();
        assertTrue(grant.getState()==states.get(0));
        grant.closeGrant();
//        assertTrue(grant.getState()==states.get(0));
        grant.evaluateProjects();
        assertTrue(grant.getState()==states.get(1));
        grant.callForProjects();
        assertTrue(grant.getState()==states.get(1));
        grant.closeGrant();
        assertTrue(grant.getState()==states.get(2));
        grant.callForProjects();
        assertTrue(grant.getState()==states.get(2));
        grant.evaluateProjects();
        assertTrue(grant.getState()==states.get(2));
        assertTrue(grant.getBudget()==10);

    }

    @Test
    void getRemainingBudget()
    {
        PersonInterface p1 = new Person();
        PersonInterface p2 = new Person();
        ProjectInterface pro1 = new Project();
        ProjectInterface pro2 = new Project();
        ProjectInterface pro3 = new Project();
        ProjectInterface pro4 = new Project();
        OrganizationInterface STU = new University();

        p1.setName("Jozef");
        p2.setName("Peter");
        STU.setName("STU");
        pro1.setProjectName("pro1");
        pro1.setApplicant(STU);
        pro1.setStartingYear(2002);
        pro2.setProjectName("pro2");
        pro2.setApplicant(STU);
        pro2.setStartingYear(2002);
        pro3.setProjectName("pro3");
        pro3.setApplicant(STU);
        pro3.setStartingYear(2002);
        pro4.setProjectName("pro4");
        pro4.setApplicant(STU);
        pro4.setStartingYear(2002);

        p1.addEmployer(STU);
        p2.addEmployer(STU);
        STU.addEmployee(p1, 1);
        STU.addEmployee(p2, 1);
        pro1.addParticipant(p2);
        pro1.addParticipant(p1);
        pro2.addParticipant(p2);
        pro2.addParticipant(p1);
        pro3.addParticipant(p2);
        pro3.addParticipant(p1);
        pro4.addParticipant(p2);
        pro4.addParticipant(p1);

        GrantInterface gran = new Grant();
        gran.setAgency(agencies.get(0));
        gran.setBudget(333);
        gran.setYear(2002);

        gran.callForProjects();
        gran.registerProject(pro1);
        gran.registerProject(pro2);
        gran.registerProject(pro3);
        gran.registerProject(pro4);
        gran.evaluateProjects();
        gran.closeGrant();
        // when budgetForProject/years will sometimes give float, but we round it down to the nearest integers
        assertEquals(164, gran.getBudgetForProject(pro1));  // 164
        assertEquals(164, gran.getBudgetForProject(pro2));
        assertEquals(5, gran.getRemainingBudget()); // 5
        assertEquals(333, gran.getBudget());
    }

//    @Test
    void projectUniqueName(){
        ProjectInterface pro1 = new Project();
        ProjectInterface pro2 = new Project();
        OrganizationInterface STU = new University();

        STU.setName("STU");
        pro1.setProjectName("pro1");
        pro1.setApplicant(STU);
        pro2.setProjectName("pro1");
        pro2.setApplicant(STU);

        STU.registerProjectInOrganization(pro1);
        STU.registerProjectInOrganization(pro2);

        assertEquals(1, STU.getAllProjects().size());
    }

//    @Test
    void organizationUniqueName(){
        PersonInterface p1 = new Person();
        OrganizationInterface VUB = new Company();
        OrganizationInterface STU = new University();
        OrganizationInterface VUT = new University();

        STU.setName("STU");
        VUB.setName("STU");
        VUT.setName("STU");

        p1.addEmployer(STU);
        p1.addEmployer(VUB);
        p1.addEmployer(VUT);
        assertEquals(1, p1.getEmployers().size());
    }

//    @Test
    void agencyUniqueName(){
        Set<AgencyInterface> p1 = new HashSet<>();
        AgencyInterface VEGA = new Agency();
        AgencyInterface APVV = new Agency();
        AgencyInterface FORTUNA = new Agency();

        VEGA.setName("VEGA");
        APVV.setName("VEGA");
        FORTUNA.setName("VEGA");

        p1.add(VEGA);
        p1.add(APVV);
        p1.add(FORTUNA);
        assertEquals(1, p1.size());
    }

//    @Test
    void grantsUniqueName(){
        GrantInterface g1 = new Grant();
        GrantInterface g2 = new Grant();
        GrantInterface g3 = new Grant();
        GrantInterface g4 = new Grant();
        AgencyInterface agent = new Agency();

        g1.setIdentifier("nahovno zadanie");
        g1.setYear(2022);
        g2.setIdentifier("nahovno zadanie");
        g2.setYear(2023);
        g3.setIdentifier("nahovno zadanie");
        g3.setYear(2024);
        g4.setIdentifier("nahovno zadanie");
        g4.setYear(2025);

        agent.setName("AGENT 007");
        agent.addGrant(g1, 2022);
        agent.addGrant(g2, 2023);
        agent.addGrant(g3, 2024);
        agent.addGrant(g4, 2025);
        assertEquals(1, agent.getAllGrants().size());
    }

    @Test
    void wasteCompanyResources() {
        Constants.PROJECT_DURATION_IN_YEARS = 2;
        Constants.COMPANY_INIT_OWN_RESOURCES = 5;
        List<OrganizationInterface> org = DataFactory.getCompanies(1);
        org.get(0).setName("VUB");

        ProjectInterface P10 =  new Project();
        P10.setApplicant(org.get(0));
        P10.setProjectName("P3");
        org.get(0).registerProjectInOrganization(P10);
        PersonInterface Peter = new Person();
        Peter.setName("Peter");
        Peter.addEmployer(org.get(0));
        org.get(0).addEmployee(Peter, 2);
        P10.addParticipant(Peter);
        P10.setStartingYear(2024);
        P10.setApplicant(org.get(0));
        assertEquals("VUB", P10.getApplicant().getName());
        GrantInterface grant2 = grants.get(2);
        grant2.callForProjects();
        grant2.registerProject(P10);
        grant2.evaluateProjects();
        grant2.closeGrant();
        // the registered project must obtain the full grant budget
        // the grant doesn't know about the company dotation
        assertEquals(100000, grant2.getBudgetForProject(P10));
        assertEquals(100005, P10.getApplicant().getProjectBudget(P10));
    }
}
