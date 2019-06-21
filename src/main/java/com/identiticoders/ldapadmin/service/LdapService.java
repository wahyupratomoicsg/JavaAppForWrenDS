/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.identiticoders.ldapadmin.service;

import com.identiticoders.ldapadmin.entity.User;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.naming.NamingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import com.unboundid.ldap.sdk.AddRequest;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.DNEntrySource;
import com.unboundid.ldap.sdk.DeleteRequest;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.EntrySourceException;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.ModifyDNRequest;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.controls.SubtreeDeleteRequestControl;
import com.unboundid.ldap.sdk.persist.LDAPPersistException;
import com.unboundid.ldap.sdk.persist.LDAPPersister;
import com.unboundid.ldif.LDIFException;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LdapService {

    private String host;
    private String portString;
    private int port;
    private String psw;
    private int OPERATION_TIMEOUT_MILLIS = 1000;

    static {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    public LdapService() {

        // String confPath = "..\\conf";
        // String name = confPath + "\\conf.properties";
        String configFile = "conf.properties";
        File file = new File(configFile);
        try {
            file.createNewFile(); // if file already exists will do nothing 
        } catch (IOException ex) {
            Logger.getLogger(LdapService.class.getName()).log(Level.SEVERE, null, ex);
        }
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(configFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Properties p = new Properties();
        try {
            p.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        host = p.getProperty("host");
        System.out.println("host read from propery file: " + host);

        portString = p.getProperty("port");
        System.out.println("port read from propery file: " + portString);
        port = Integer.parseInt(portString);

        psw = p.getProperty("password");
        System.out.println("password read from propery file: " + psw);

    }

    public void create(String entryDN, String cn, byte[] p12Cert,
            byte[] publicKey, byte[] privateKey) {

        byte[] encodedPrivateCert = Base64.encodeBase64(privateKey);
        byte[] encodedP12Cert = Base64.encodeBase64(p12Cert);

        String[] ldifLines = {"dn: " + "cn=" + cn + "," + entryDN,
            "objectClass: top",
            "objectClass: person",
            "objectClass: organizationalPerson",
            "objectClass: inetOrgPerson", "changetype: add", "cn: " + cn,
            "sn: " + cn, "mail: " + cn, "keyAlgorithm: RSA",
            // "privateKey: " + new String(encodedp12Cert),
            "privateKeyFormat: PKCS#8",
            // "publicKey: " + new String(publicKey),
            "publicKeyFormat: X.509", // "userCertificate: " + new String(publicKey),
    };

        LDAPConnectionOptions connectionOptions = new LDAPConnectionOptions();
        connectionOptions.setAbandonOnTimeout(true);
        connectionOptions.setConnectTimeoutMillis(OPERATION_TIMEOUT_MILLIS);

        int result;
        LDAPResult ldapResult = null;
        try {

            // Connect to the server.
            LDAPConnection ldapConnection = new LDAPConnection(
                    connectionOptions, host, port);
            try {

                // Create the AddRequest object using the LDIF lines.
                AddRequest addRequest = new AddRequest(ldifLines);

                addRequest.addAttribute("privateKey", encodedPrivateCert);
                addRequest.addAttribute("publicKey", publicKey);
                addRequest.addAttribute("userCertificate", publicKey);
                addRequest.addAttribute("userPKCS12", encodedP12Cert);

                // Transmit the AddRequest to the server.
                ldapResult = ldapConnection.add(addRequest);

                System.out.println(ldapResult);

            } catch (final LDIFException e) {
                System.err.println(e);
            } finally {
                ldapConnection.close();

                // Convert the result code to an integer for use in the exit
                // method.
                result = ldapResult == null ? 1 : ldapResult.getResultCode()
                        .intValue();
            }
        } catch (final LDAPException e) {
            System.err.println(e);
            result = 1;
        }

        // System.exit(result);
    }

//    public void createWithPersistObject(String entryDN, String cn,
//            byte[] p12Cert, byte[] publicKey, byte[] privateKey) {
//
//        byte[] encodedPrivateCert = Base64.encodeBase64(privateKey);
//        byte[] encodedP12Cert = Base64.encodeBase64(p12Cert);
//
//        LDAPPersister<User> persister = null;
//        try {
//            persister = LDAPPersister.getInstance(User.class);
//        } catch (LDAPPersistException e2) {
//            // TODO Auto-generated catch block
//            e2.printStackTrace();
//        }
//
//        // Create a new MyObject instance and add it to the directory. We can
//        // use
//        // a parent DN of null to indicate that it should use the default
//        // defined
//        // in the @LDAPObject annotation.
//        LDAPConnection ldapConnection = null;
//        try {
//            ldapConnection = getLdapConnection();
//        } catch (LDAPException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
//
//        User user = new User();
//        user.setDn("cn=" + cn + "," + entryDN);
//        user.setCn(cn);
//        user.setSn(cn);
//        user.setMail(cn);
//        user.setPrivateKey(encodedPrivateCert);
//        user.setPublicKey(publicKey);
//        user.setUserCertificate(publicKey);
//        user.setUserPKCS12(encodedP12Cert);
//
//        LDAPResult ldapResult = null;
//        int result;
//        try {
//            ldapResult = persister.add(user, ldapConnection, null);
//
//            System.out.println(ldapResult);
//
//        } catch (Exception e) {
//            System.err.println(e);
//        } finally {
//            ldapConnection.close();
//
//            // Convert the result code to an integer for use in the exit method.
//            result = ldapResult == null ? 1 : ldapResult.getResultCode()
//                    .intValue();
//        }
//
//        // System.exit(result);
//    }
    
    public void createLdapForWrenDS(String entryDN, String cn, String mail, String description) {

        String[] ldifLines = {"dn: " + "cn=" + cn + "," + entryDN,
            "objectClass: top",
            "objectClass: person",
            "objectClass: organizationalPerson",
            "objectClass: inetOrgPerson",
            "cn: " + cn,
            "sn: " + cn, 
            "mail: " + mail,
            "description: " + description,
            "employeeType: " + "enabled"
            //"changetype: add",
            //"keyAlgorithm: RSA",
            // "privateKey: " + new String(encodedp12Cert),
            //"privateKeyFormat: PKCS#8",
            // "publicKey: " + new String(publicKey),
            //"publicKeyFormat: X.509", // "userCertificate: " + new String(publicKey),
        };

        LDAPConnectionOptions connectionOptions = new LDAPConnectionOptions();
        connectionOptions.setAbandonOnTimeout(true);
        connectionOptions.setConnectTimeoutMillis(OPERATION_TIMEOUT_MILLIS);

        int result;
        LDAPResult ldapResult = null;
        try {
            // Connect to the server.
            LDAPConnection ldapConnection = null;
            ldapConnection = getAuthLdapConnection(psw);
            try {

                // Create the AddRequest object using the LDIF lines.
                AddRequest addRequest = new AddRequest(ldifLines);

//                addRequest.addAttribute("privateKey", encodedPrivateCert);
//                addRequest.addAttribute("publicKey", publicKey);
//                addRequest.addAttribute("userCertificate", publicKey);
//                addRequest.addAttribute("userPKCS12", encodedP12Cert);

                // Transmit the AddRequest to the server.
                ldapResult = ldapConnection.add(addRequest);

                System.out.println(ldapResult);

            } catch (final LDIFException e) {
                System.err.println(e);
            } finally {
                ldapConnection.close();

                // Convert the result code to an integer for use in the exit
                // method.
                result = ldapResult == null ? 1 : ldapResult.getResultCode()
                        .intValue();
            }
        } catch (final LDAPException e) {
            System.err.println(e);
            result = 1;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void search(String dn, String filter)
            throws NamingException {

        LDAPConnection ldapConnection = null;
        try {
            ldapConnection = getAuthLdapConnection(psw);
        } catch (LDAPException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        if (StringUtils.isNotEmpty(filter)) {
            SearchRequest searchRequest = null;
            try {
                searchRequest = new SearchRequest(dn, SearchScope.SUB, filter);
            } catch (LDAPException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            System.out
                    .println("========================================================");
            System.out.println("Search Result: ");

            try {
                SearchResult searchResult = ldapConnection
                        .search(searchRequest);

                for (SearchResultEntry entry : searchResult.getSearchEntries()) {
                    // String name = entry.getAttributeValue("cn");
                    // String mail = entry.getAttributeValue("mail");
                    /*
                    * StringBuilder buffer = new StringBuilder();
                    * 
                    * entry.toString(buffer);
                    * System.out.println(buffer.toString());
                     */
                    System.out.println();
                    System.out.println("Entry DN: " + entry.getDN());
                    List<Attribute> attributes = new ArrayList(
                            entry.getAttributes());
                    System.out
                            .println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                    System.out.println("Entry content: ");

                    for (Attribute att : attributes) {

                        System.out.print(att.getName() + ": ");

                        String[] values = att.getValues();

                        for (int i = 0; i < values.length - 1; i++) {
                            System.out.print(values[i]);
                            System.out.print(",");
                        }
                        System.out.print(values[values.length - 1]);
                        System.out.println();
                    }
                }
                System.out
                        .println("========================================================");
                System.out.println("Search end.");
            } catch (LDAPSearchException lse) {
                System.err.println("The search failed.");
            }
        } else {

            Entry groupEntry = null;
            try {
                groupEntry = ldapConnection.getEntry(dn);
            } catch (LDAPException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            List<Attribute> attributes = new ArrayList(
                    groupEntry.getAttributes());
            System.out
                    .println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            System.out.println("Entry content: ");

            for (Attribute att : attributes) {

                System.out.print(att.getName() + ": ");

                String[] values = att.getValues();

                for (int i = 0; i < values.length - 1; i++) {
                    System.out.print(values[i]);
                    System.out.print(",");
                }
                System.out.print(values[values.length - 1]);

                System.out.println();

            }

            System.out.println("End Search.");

            String[] memberValues = groupEntry.getAttributeValues("member");

            if (memberValues != null) {

                DNEntrySource entrySource = new DNEntrySource(ldapConnection,
                        memberValues, "cn");

                while (true) {
                    try {
                        Entry memberEntry = entrySource.nextEntry();
                        if (memberEntry == null) {
                            break;
                        }

                        System.out.println("Retrieved member entry:  "
                                + memberEntry.getAttributeValue("cn"));
                    } catch (EntrySourceException ex) {
                        Logger.getLogger(LdapService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

//    public void update(String psw, String entryDN, String cn, String newDn,
//            String newCn, byte[] p12Cert, byte[] publicKey, byte[] privateKey) {
//
//        LDAPConnection ldapConnection = null;
//        String fullDn = "cn=" + cn + "," + entryDN;
//
//        if (StringUtils.isNotEmpty(newDn) || StringUtils.isNotEmpty(newCn)) {
//
//            ModifyDNRequest modifyDNRequest = null;
//
//            if (StringUtils.isEmpty(newDn)) {
//                newDn = entryDN;
//                modifyDNRequest = new ModifyDNRequest(fullDn, "cn=" + newCn,
//                        true);
//            }
//            if (StringUtils.isEmpty(newCn)) {
//                newCn = cn;
//                modifyDNRequest = new ModifyDNRequest(fullDn, "cn=" + newCn,
//                        true, newDn);
//            }
//            if (StringUtils.isNotEmpty(newDn) && StringUtils.isNotEmpty(newCn)) {
//
//                modifyDNRequest = new ModifyDNRequest(fullDn, "cn=" + newCn,
//                        true, newDn);
//            }
//
//            fullDn = "cn=" + newCn + "," + newDn;
//
//            try {
//                ldapConnection = null;
//                try {
//                    ldapConnection = getAuthLdapConnection(psw);
//                } catch (LDAPException e1) {
//                    // TODO Auto-generated catch block
//                    e1.printStackTrace();
//                }
//                modifyDNRequest.setDeleteOldRDN(true);
//                LDAPResult modifyDNResult = ldapConnection
//                        .modifyDN(modifyDNRequest);
//
//                System.out.println("The entry was renamed successfully.");
//            } catch (LDAPException le) {
//                le.printStackTrace();
//                System.err.println("The modify DN operation failed.");
//            }
//        }
//
//        if (StringUtils.isNotEmpty(newCn) || p12Cert != null
//                || publicKey != null || privateKey != null) {
//
//            LDAPPersister<User> persister = null;
//            try {
//                persister = LDAPPersister.getInstance(User.class);
//            } catch (LDAPPersistException e2) {
//                // TODO Auto-generated catch block
//                e2.printStackTrace();
//            }
//
//            // Create a new MyObject instance and add it to the directory. We
//            // can
//            // use
//            // a parent DN of null to indicate that it should use the default
//            // defined
//            // in the @LDAPObject annotation.
//            try {
//                ldapConnection = getAuthLdapConnection(psw);
//            } catch (LDAPException e1) {
//                // TODO Auto-generated catch block
//                e1.printStackTrace();
//            }
//
//            User user = new User();
//            user.setDn(fullDn);
//            if (StringUtils.isEmpty(newCn)) {
//                user.setCn(cn);
//                user.setSn(cn);
//                user.setMail(cn);
//            } else {
//                user.setCn(newCn);
//                user.setSn(newCn);
//                user.setMail(newCn);
//            }
//
//            if (p12Cert != null) {
//                byte[] encodedP12Cert = Base64.encodeBase64(p12Cert);
//                user.setUserPKCS12(encodedP12Cert);
//            }
//            if (privateKey != null) {
//                byte[] encodedPrivateCert = Base64.encodeBase64(privateKey);
//                user.setPrivateKey(encodedPrivateCert);
//            }
//
//            if (publicKey != null) {
//                user.setPublicKey(publicKey);
//                user.setUserCertificate(publicKey);
//            }
//
//            LDAPResult ldapResult = null;
//            int result;
//            try {
//                ldapResult = persister
//                        .modify(user, ldapConnection, null, false);
//
//                System.out.println(ldapResult);
//
//            } catch (Exception e) {
//                System.err.println(e);
//            } finally {
//                ldapConnection.close();
//
//                // Convert the result code to an integer for use in the exit
//                // method.
//                result = ldapResult == null ? 1 : ldapResult.getResultCode()
//                        .intValue();
//            }
//        }
//
//        // System.exit(result);
//    }

    public void updateLdapForWrenDS(String entryDN, String cn, 
            String newDn, String newCn, String email, String description) {

        LDAPConnection ldapConnection = null;
        String fullDn = "cn=" + cn + "," + entryDN;

        if (StringUtils.isNotEmpty(newDn) || StringUtils.isNotEmpty(newCn)) {

            ModifyDNRequest modifyDNRequest = null;

            if (StringUtils.isEmpty(newDn)) {
                newDn = entryDN;
                modifyDNRequest = new ModifyDNRequest(fullDn, "cn=" + newCn,
                        true);
            }
            if (StringUtils.isEmpty(newCn)) {
                newCn = cn;
                modifyDNRequest = new ModifyDNRequest(fullDn, "cn=" + newCn,
                        true, newDn);
            }
            if (StringUtils.isNotEmpty(newDn) && StringUtils.isNotEmpty(newCn)) {

                modifyDNRequest = new ModifyDNRequest(fullDn, "cn=" + newCn,
                        true, newDn);
            }

            fullDn = "cn=" + newCn + "," + newDn;

            try {
                ldapConnection = null;
                try {
                    ldapConnection = getAuthLdapConnection(psw);
                } catch (LDAPException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                modifyDNRequest.setDeleteOldRDN(true);
                LDAPResult modifyDNResult = ldapConnection
                        .modifyDN(modifyDNRequest);

                System.out.println("The entry was renamed successfully.");
            } catch (LDAPException le) {
                le.printStackTrace();
                System.err.println("The modify DN operation failed.");
            }
        }

        LDAPPersister<User> persister = null;
        try {
            persister = LDAPPersister.getInstance(User.class);
        } catch (LDAPPersistException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        // Create a new MyObject instance and add it to the directory. We
        // can use
        // a parent DN of null to indicate that it should use the default
        // defined
        // in the @LDAPObject annotation.
        try {
            ldapConnection = getAuthLdapConnection(psw);
        } catch (LDAPException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        User user = new User();
        user.setDn(fullDn);
        if (!StringUtils.isEmpty(newCn)) {
            user.setCn(newCn);
            user.setSn(newCn);
        } else {
            user.setCn(cn);
            user.setSn(cn);
        }
        
        if (!StringUtils.isEmpty(email)) {
            user.setMail(email);
        } 
        
        if (!StringUtils.isEmpty(description)) {
            user.setDescription(description);
        } 

        LDAPResult ldapResult = null;
        int result;
        try {
            ldapResult = persister
                    .modify(user, ldapConnection, null, false);

            System.out.println(ldapResult);

        } catch (Exception e) {
            System.err.println(e);
        } finally {
            ldapConnection.close();

            // Convert the result code to an integer for use in the exit
            // method.
            result = ldapResult == null ? 1 : ldapResult.getResultCode()
                    .intValue();
        }
        // System.exit(result);
    }
    
    public void enableDisableUserForWrenDS(String entryDN, String cn, 
            boolean enaStatus) {

        LDAPConnection ldapConnection = null;
        String fullDn = "cn=" + cn + "," + entryDN;

        LDAPPersister<User> persister = null;
        try {
            persister = LDAPPersister.getInstance(User.class);
        } catch (LDAPPersistException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        // Create a new MyObject instance and add it to the directory. We
        // can use
        // a parent DN of null to indicate that it should use the default
        // defined
        // in the @LDAPObject annotation.
        try {
            ldapConnection = getAuthLdapConnection(psw);
        } catch (LDAPException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        User user = new User();
        user.setDn(fullDn);
        
        user.setEmployeeType(enaStatus?"enabled":"disabled");

        LDAPResult ldapResult = null;
        int result;
        try {
            ldapResult = persister
                    .modify(user, ldapConnection, null, false);

            System.out.println(ldapResult);

        } catch (Exception e) {
            System.err.println(e);
        } finally {
            ldapConnection.close();

            // Convert the result code to an integer for use in the exit
            // method.
            result = ldapResult == null ? 1 : ldapResult.getResultCode()
                    .intValue();
        }
        // System.exit(result);
    }
    
    public void deleteOneLevel(String psw, String entryDN) {

        LDAPConnection ldapConnection = null;
        try {
            ldapConnection = getAuthLdapConnection(psw);
        } catch (LDAPException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        DeleteRequest deleteRequest = new DeleteRequest(entryDN);
        deleteRequest.addControl(new SubtreeDeleteRequestControl());

        LDAPResult ldapResult = null;
        int result;
        try {
            ldapResult = ldapConnection.delete(deleteRequest);

            System.out.println("The entry was successfully deleted.");
        } catch (LDAPException le) {
            le.printStackTrace();
            System.err.println("The delete operation failed.");
        } finally {
            ldapConnection.close();

            // Convert the result code to an integer for use in the exit method.
            result = ldapResult == null ? 1 : ldapResult.getResultCode()
                    .intValue();
        }

        // System.exit(result);
    }

    public void delete(String entryDN) throws LDAPException {

        LDAPConnection ldapConnection = null;
        try {
            ldapConnection = getAuthLdapConnection(psw);
        } catch (LDAPException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        deleteSubEntry(entryDN, ldapConnection);

        return;
    }

    public void deleteSubEntry(String entry, LDAPConnection ldapConnection)
            throws LDAPException {

        // System.out.println("deleteSubEntry '"+entry+"' start.");
        SearchRequest searchRequest = null;
        String filter = "objectClass=*";

        searchRequest = new SearchRequest(entry, SearchScope.ONE, filter);

        SearchResult searchResult = null;

        // System.out.println("search sub-entry for entry '"+entry+"' start.");
        searchResult = ldapConnection.search(searchRequest);

        if (searchResult.getEntryCount() == 0) {
            // System.out.println("No sub entry.");
            deleteEntry(entry, ldapConnection);
        } else {
            // System.out.println("Sub entries exist.");
            for (SearchResultEntry entryResult : searchResult
                    .getSearchEntries()) {

                if (!entryResult.getDN().equalsIgnoreCase(entry)) {
                    // System.out.println("Get sub entry: " +
                    // entryResult.getDN());
                    deleteSubEntry(entryResult.getDN(), ldapConnection);
                }

            }
            // System.out.println("Start delete parent entry " + entry);
            deleteEntry(entry, ldapConnection);
        }

        // return;
    }

    public void deleteEntry(String entry, LDAPConnection ldapConnection) {

        DeleteRequest deleteRequest = new DeleteRequest(entry);

        LDAPResult ldapResult = null;
        int result;
        try {
            ldapResult = ldapConnection.delete(deleteRequest);

            System.out.println("The entry '" + entry
                    + "' was successfully deleted.");
        } catch (LDAPException le) {
            le.printStackTrace();
            System.err.println("The delete operation for entry '" + entry
                    + "' failed.");
        } finally {

            // Convert the result code to an integer for use in the exit method.
            result = ldapResult == null ? 1 : ldapResult.getResultCode()
                    .intValue();
        }
    }

    public LDAPConnection getLdapConnection() throws LDAPException {

        LDAPConnectionOptions connectionOptions = new LDAPConnectionOptions();
        connectionOptions.setAbandonOnTimeout(true);
        connectionOptions.setConnectTimeoutMillis(OPERATION_TIMEOUT_MILLIS);

        LDAPConnection ldapConnection = new LDAPConnection(connectionOptions,
                host, port);

        return ldapConnection;
    }

    public LDAPConnection getAuthLdapConnection(String psw)
            throws LDAPException {

        LDAPConnectionOptions connectionOptions = new LDAPConnectionOptions();
        connectionOptions.setAbandonOnTimeout(true);
        connectionOptions.setConnectTimeoutMillis(OPERATION_TIMEOUT_MILLIS);

//       LDAPConnection ldapConnection = new LDAPConnection(connectionOptions,
//               host, port, "uid=admin,ou=system", psw);
        LDAPConnection ldapConnection = new LDAPConnection(connectionOptions,
                host, port, "cn=Directory Manager", psw);

        return ldapConnection;
    }

    public byte[] readDataFromFile(String path) {

        InputStream is = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            is = new BufferedInputStream(new FileInputStream(path));
            byte[] b = new byte[1024];
            int n;
            while ((n = is.read(b)) != -1) {
                out.write(b, 0, n);
            }

        } catch (Exception e) {
            // e.printStackTrace();
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return out.toByteArray();
    }

    private byte[] InputStreamToByte(InputStream is) throws IOException {

        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int ch;
        while ((ch = is.read()) != -1) {
            bytestream.write(ch);
        }
        byte imgdata[] = bytestream.toByteArray();
        bytestream.close();
        return imgdata;

    }
}
