package sk.stuba.fei.uim.oop;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import sk.stuba.fei.uim.oop.entity.grant.*;
import sk.stuba.fei.uim.oop.entity.organization.Company;
import sk.stuba.fei.uim.oop.entity.organization.OrganizationInterface;
import sk.stuba.fei.uim.oop.entity.people.Person;
import sk.stuba.fei.uim.oop.entity.people.PersonInterface;
import sk.stuba.fei.uim.oop.factory.DataFactory;
import sk.stuba.fei.uim.oop.utility.Constants;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class JantoTest {

    private LinkedList<AgencyInterface> agencies = new LinkedList<>();
    private LinkedList<GrantInterface> grants = new LinkedList<>();
    private LinkedList<OrganizationInterface> organizations = new LinkedList<>();
    private LinkedList<ProjectInterface> projects = new LinkedList<>();
    private LinkedList<PersonInterface> persons = new LinkedList<>();

    @BeforeEach
    void setUp() {


        int budget = 1000;
        int duration = 4;
        int startingYear = 2020;
        Constants.PROJECT_DURATION_IN_YEARS = 4;

        // grants
        Grant grant1 = new Grant();
        grant1.setYear(startingYear);
        grant1.setBudget(budget);

        grants.add(grant1);

        // persons
        PersonInterface kascak = new Person();
        PersonInterface fico = new Person();
        PersonInterface kalinak = new Person();

        kascak.setName("Kascak");
        fico.setName("Fico");
        kalinak.setName("Kalinak");

        // agency
        agencies = DataFactory.getAgencies(2);
        agencies.get(0).setName("Nadacia Pellegrini");
        agencies.get(1).setName("penis");

        agencies.get(0).addGrant(grant1, grant1.getYear());
        grant1.setAgency(agencies.get(0));

        // Company
        Company penta = new Company();
        penta.setName("Penta");
        penta.addEmployee(kascak, 4);
        penta.addEmployee(kalinak, 3);

        Company jnt = new Company();
        jnt.setName("J&T");
        jnt.addEmployee(fico, 3);
        jnt.addEmployee(kalinak, 2);

        // Projects
        ProjectInterface P1 = new Project();
        ProjectInterface P2 = new Project();
        ProjectInterface P3 = new Project();

        P1.setApplicant(penta);
        P1.setStartingYear(startingYear);
        P1.addParticipant(kascak);
        P1.setProjectName("P1");

        P2.setApplicant(penta);
        P2.setStartingYear(startingYear);
        P2.addParticipant(kascak);
        P2.addParticipant(fico);
        P2.setProjectName("P2");

        P3.setApplicant(jnt);
        P3.setStartingYear(startingYear);
        P3.addParticipant(kalinak);
        P3.setProjectName("P3");

        projects.add(P1);
        projects.add(P2);
        projects.add(P3);

        // Start
        grant1.callForProjects();

        // Add to grant
        grant1.registerProject(P1);
        grant1.registerProject(P2);
        grant1.registerProject(P3);

        // Evaluate
        grant1.evaluateProjects();
        grant1.closeGrant();
    }

    @Test
    void basicTest() {
        int startingYear = grants.get(0).getYear();

        assertEquals(0, projects.get(1).getTotalBudget());
        assertEquals(0, projects.get(2).getTotalBudget());
        assertEquals(1000, projects.get(0).getTotalBudget());
        assertEquals(250, projects.get(0).getBudgetForYear(startingYear));
        assertEquals(250, projects.get(0).getBudgetForYear(startingYear+1));
        assertEquals(250, projects.get(0).getBudgetForYear(startingYear+2));
        assertEquals(250, projects.get(0).getBudgetForYear(startingYear+3));
    }
}
