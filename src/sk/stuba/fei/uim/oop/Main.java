package sk.stuba.fei.uim.oop;

import sk.stuba.fei.uim.oop.entity.grant.Agency;
import sk.stuba.fei.uim.oop.entity.grant.Grant;
import sk.stuba.fei.uim.oop.entity.grant.Project;
import sk.stuba.fei.uim.oop.entity.grant.ProjectInterface;
import sk.stuba.fei.uim.oop.entity.organization.Company;
import sk.stuba.fei.uim.oop.entity.organization.OrganizationInterface;
import sk.stuba.fei.uim.oop.entity.organization.University;
import sk.stuba.fei.uim.oop.entity.people.Person;

public class Main {

    public static void main(String[] args) {

        // TODO komenty
        // TODO vymazat nepotrebne premenne

        Agency ag = new Agency();

        Grant grant = new Grant();
        grant.setYear(2020);
        grant.callForProjects();
        grant.setAgency(ag);

        OrganizationInterface STU = new University();
        STU.setName("STU");

        Project P1 = new Project();
        Project P2 = new Project();

        Person negr = new Person();
        negr.setName("Negr");
        negr.addEmployer(STU);
        STU.addEmployee(negr, 2);

        P1.setProjectName("Negr");
        P2.setProjectName("Bílý");

        P2.setApplicant(STU);
        P2.setStartingYear(2020);

        P2.addParticipant(negr);

        grant.registerProject(P1);
        grant.registerProject(P2);

        grant.evaluateProjects();

        grant.closeGrant();
    }
}
