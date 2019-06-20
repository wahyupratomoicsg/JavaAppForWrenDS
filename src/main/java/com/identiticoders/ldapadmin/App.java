/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.identiticoders.ldapadmin;

import com.identiticoders.ldapadmin.service.LdapService;
import com.unboundid.ldap.sdk.LDAPException;
import java.util.Scanner;

import javax.naming.NamingException;

import org.apache.commons.lang.StringUtils;

/**
 *
 */
public class App {

    public static void main(String[] args) {
        LdapService service = new LdapService();
        manu(service);
    }

    public static void manu(LdapService service) {
        System.out.println("----------------------------------------");
        System.out.println("----------------MODE--------------------");
        System.out.println("1: create");
        System.out.println("2: retrieve");
        System.out.println("3: update");
        System.out.println("4: delete");

        Scanner input = new Scanner(System.in);
        System.out.print("Key in the selection : ");

        String selectionString = input.next();

        boolean isValid = StringUtils.isNumeric(selectionString);
        if (isValid) {
            int option = Integer.parseInt(selectionString);
            if (option == 1) {

                System.out.print("Please input the entry dn: ");
                Scanner inputScanner = new Scanner(System.in);
                String dn = inputScanner.next();
                //String dn = "ou=users,ou=system";
                System.out.println("entry dn: " + dn);

                System.out.print("Please input the entry cn: ");
                inputScanner = new Scanner(System.in);
                String cn = inputScanner.next();

                System.out.println("entry cn: " + cn);

                System.out.print("Please input the p12 cert location: ");
                byte[] p12certData = getCertDataFromPath(service);

                System.out.println("p12 cert data read: " + new String(p12certData));

                System.out.print("Please input the public cert location: ");
                byte[] publicCertData = getCertDataFromPath(service);

                System.out.println("public cert data read: " + new String(publicCertData));

                System.out.print("Please input the private cert location: ");
                byte[] privateCertData = getCertDataFromPath(service);

                System.out.println("private cert data read: " + new String(privateCertData));

                service.createWithPersistObject(dn, cn, p12certData, publicCertData, privateCertData);
            } else if (option == 2) {

                //System.out.print("Please input the entry dn: ");
                Scanner inputScanner = new Scanner(System.in);
                //String dn = inputScanner.next();
                //String dn = "ou=system";

                String dn = "ou=People,dc=example,dc=com";
                System.out.println("entry dn: " + dn);

                System.out.print("Please input the filter(if do not have, input n): ");
                inputScanner = new Scanner(System.in);
                String filter = inputScanner.next();

                if (filter.equalsIgnoreCase("n")) {
                    filter = null;
                } else {
                    System.out.println("entry filter: " + filter);
                }

                try {
                    service.search(dn, filter);
                } catch (NamingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else if (option == 3) {

                System.out.print("Please input the entry dn: ");
                Scanner inputScanner = new Scanner(System.in);
                String dn = inputScanner.next();
                //String dn = "ou=users,ou=system";
                System.out.println("entry dn: " + dn);

                System.out.print("Please input the password: ");
                inputScanner = new Scanner(System.in);
                String psw = inputScanner.next();

                System.out.println("password: " + psw);

                System.out.print("Please input the entry cn: ");
                inputScanner = new Scanner(System.in);
                String cn = inputScanner.next();

                System.out.println("entry cn: " + cn);

                System.out.print("Please input the new entry dn(if do not want to update, input n): ");
                inputScanner = new Scanner(System.in);
                String newDn = inputScanner.next();
                //String newDn = "ou=system";
                if (newDn.equalsIgnoreCase("n")) {
                    newDn = null;
                } else {
                    System.out.println("New entry dn: " + newDn);
                }

                System.out.print("Please input the new cn(if do not want to update, input n): ");
                inputScanner = new Scanner(System.in);
                String newCn = inputScanner.next();

                if (newCn.equalsIgnoreCase("n")) {
                    newCn = null;
                } else {
                    System.out.println("New cn: " + newCn);
                }

                System.out.println("For below options, if no update, please input n or any other invalid path");

                System.out.print("Please input the p12 cert location: ");
                inputScanner = new Scanner(System.in);
                String certLocation = inputScanner.next();
                byte[] p12certData = getCertData(service, certLocation);
                if (p12certData != null) {
                    System.out.println("p12 cert data read: " + new String(p12certData));
                }

                System.out.print("Please input the public cert location: ");
                inputScanner = new Scanner(System.in);
                certLocation = inputScanner.next();
                byte[] publicCertData = getCertData(service, certLocation);

                if (publicCertData != null) {
                    System.out.println("public cert data read: " + new String(publicCertData));
                }

                System.out.print("Please input the private cert location: ");
                inputScanner = new Scanner(System.in);
                certLocation = inputScanner.next();
                byte[] privateCertData = getCertData(service, certLocation);

                if (privateCertData != null) {
                    System.out.println("private cert data read: " + new String(privateCertData));
                }

                service.update(psw, dn, cn, newDn, newCn, p12certData, publicCertData, privateCertData);

            } else if (option == 4) {
                System.out.println("WARN: DELETE will delete all the sub-entries too, if exist.");
                System.out.print("Please input the entry dn: ");
                Scanner inputScanner = new Scanner(System.in);
                String dn = inputScanner.next();

                System.out.println("entry dn: " + dn);

                try {
                    service.delete(dn);
                } catch (LDAPException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else {
                System.out.println("Invalid option: " + selectionString);
                manu(service);
            }
        } else {
            System.out.println("Invalid option: " + selectionString);
            manu(service);
        }

        manu(service);
    }

    public static byte[] getCertDataFromPath(LdapService service) {

        Scanner inputScanner = new Scanner(System.in);
        String certLocation = inputScanner.next();

        byte[] data = null;
        try {
            data = service.readDataFromFile(certLocation);

        } catch (Exception e) {
            e.printStackTrace();
            data = null;
        }
        if (data == null) {
            System.out.println("Invalid cert path: " + certLocation);
            System.out.print("Please enter again: ");
            return getCertDataFromPath(service);
        }

        return data;
    }

    static byte[] getCertData(LdapService service, String certLocation) {

        byte[] data = null;
        try {
            data = service.readDataFromFile(certLocation);
        } catch (Exception e) {
            e.printStackTrace();
            data = null;
        }
        return data;
    }

}
