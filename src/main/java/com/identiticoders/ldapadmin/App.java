/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.identiticoders.ldapadmin;

import com.identiticoders.ldapadmin.service.LdapService;
import com.unboundid.ldap.sdk.LDAPException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;

import org.apache.commons.lang.StringUtils;

/**
 *
 */
public class App {

    public static void main(String[] args) {
        LdapService service = new LdapService();
        menu(service);
    }

    public static void menu(LdapService service) {
        System.out.println("----------------------------------------");
        System.out.println("----------------MODE--------------------");
        System.out.println("1: create");
        System.out.println("2: retrieve");
        System.out.println("3: update");
        System.out.println("4: delete");
        System.out.println("5: enable/disable");

        Scanner input = new Scanner(System.in);
        System.out.print("Key in the selection : ");

        String selectionString = input.next();

        boolean isValid = StringUtils.isNumeric(selectionString);
        if (isValid) {
            int option = Integer.parseInt(selectionString);
            switch (option) {
                case 1:
                    {
                        System.out.print("Please input the entry dn: ");
                        Scanner inputScanner = new Scanner(System.in);
                        String dn = inputScanner.next();
                        //String dn = "ou=users,ou=system";
                        System.out.println("entry dn: " + dn);
                        
                        System.out.print("Please input the entry cn: ");
                        inputScanner = new Scanner(System.in);
                        String cn = inputScanner.next();
                        System.out.println("entry cn: " + cn);
                        
                        System.out.print("Please input email: ");
                        inputScanner = new Scanner(System.in);
                        String mail = inputScanner.next();
                        System.out.println("entry mail: " + mail);
                        
                        System.out.print("Please input description: ");
                        BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
                        String desc = null;
                        try {
                            desc = buffer.readLine();
                        } catch (IOException ex) {
                            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        System.out.println("entry desc: " + desc);
                        
                        service.createLdapForWrenDS(dn, cn, mail, desc);
                        break;
                    }
                case 2:
                    {
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
                        }       try {
                            service.search(dn, filter);
                        } catch (NamingException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }       
                        break;
                    }
                case 3:
                    {
                        System.out.print("Please input the entry dn: ");
                        Scanner inputScanner = new Scanner(System.in);
                        String dn = inputScanner.next();
                        //String dn = "ou=users,ou=system";
                        System.out.println("entry dn: " + dn);
                        
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
                        
                        System.out.print("Please input the new email(if do not want to update, input n): ");
                        inputScanner = new Scanner(System.in);
                        String email = inputScanner.next();
                        if (email.equalsIgnoreCase("n")) {
                            email = null;
                        } else {
                            System.out.println("New email: " + email);
                        }   
                        
                        System.out.print("Please input the new description(if do not want to update, input n): ");
                        BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
                        String desc = null;
                        try {
                            desc = buffer.readLine();
                        } catch (IOException ex) {
                            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (desc.equalsIgnoreCase("n")) {
                            desc = null;
                        } else {
                            System.out.println("New desc: " + desc);
                        }   
                        
                        service.updateLdapForWrenDS(dn, cn, newDn, newCn, email, desc);
                        break;
                    }
                case 4:
                    {
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
                        break;
                    }
                case 5:
                    {
                        System.out.print("Please input the entry dn: ");
                        Scanner inputScanner = new Scanner(System.in);
                        String dn = inputScanner.next();
                        System.out.println("entry dn: " + dn);
                        
                        System.out.print("Please input the entry cn: ");
                        inputScanner = new Scanner(System.in);
                        String cn = inputScanner.next();
                        System.out.println("entry cn: " + cn);
                        
                        System.out.print("Enable y/n? [y] : ");
                        inputScanner = new Scanner(System.in);
                        String ena = inputScanner.next();
                        System.out.println("entry enable: " + ena);
                        
                        boolean enabled = true;
                        if (ena.equalsIgnoreCase("n")) {
                            enabled = false;
                        } 
                        service.enableDisableUserForWrenDS(dn, cn, enabled);
                        break;
                    }
                default:
                    System.out.println("Invalid option: " + selectionString);
                    menu(service);
                    break;
            }
        } else {
            System.out.println("Invalid option: " + selectionString);
            menu(service);
        }

        menu(service);
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
