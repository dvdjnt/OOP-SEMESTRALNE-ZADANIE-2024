package sk.stuba.fei.uim.oop;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sk.stuba.fei.uim.oop.entity.grant.Agency;
import sk.stuba.fei.uim.oop.entity.grant.Grant;
import sk.stuba.fei.uim.oop.entity.grant.Project;
import sk.stuba.fei.uim.oop.entity.grant.ProjectInterface;
import sk.stuba.fei.uim.oop.entity.organization.Company;
import sk.stuba.fei.uim.oop.entity.people.Person;
import sk.stuba.fei.uim.oop.entity.people.PersonInterface;
import sk.stuba.fei.uim.oop.utility.Constants;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class JantoTest {

    @Test
    void test2() {
        // Const
        int budget = 1000;
        int duration = 4;
        int startingYear = 2020;
        Constants.PROJECT_DURATION_IN_YEARS = 4;

        // Persons
        PersonInterface kascak = new Person();
        PersonInterface fico = new Person();
        PersonInterface kalinak = new Person();

        kascak.setName("Kascak");
        fico.setName("Fico");
        kalinak.setName("Kalinak");

        // Grant
        Grant grant1 = new Grant();
        grant1.setYear(startingYear);
        grant1.setBudget(budget);

        // Agency
        Agency pelle = new Agency();
        pelle.setName("Nadacia Pellegrini");
        pelle.addGrant(grant1, grant1.getYear());
        grant1.setAgency(pelle);

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

        // Start
        grant1.callForProjects();

        // Add to grant
        grant1.registerProject(P1);
        grant1.registerProject(P2);
        grant1.registerProject(P3);

        // Evaluate
        grant1.evaluateProjects();
        grant1.closeGrant();

        assertEquals(0, P2.getTotalBudget());
        assertEquals(0, P3.getTotalBudget());
        assertEquals(1000, P1.getTotalBudget());
        assertEquals(250, P1.getBudgetForYear(startingYear));
        assertEquals(250, P1.getBudgetForYear(startingYear+1));
        assertEquals(250, P1.getBudgetForYear(startingYear+2));
        assertEquals(250, P1.getBudgetForYear(startingYear+3));
    }


    @Test
    void test1() {



    }
}
