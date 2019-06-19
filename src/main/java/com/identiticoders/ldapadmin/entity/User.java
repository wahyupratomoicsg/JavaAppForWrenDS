/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.identiticoders.ldapadmin.entity;

import com.unboundid.ldap.sdk.persist.FilterUsage;
import com.unboundid.ldap.sdk.persist.LDAPDNField;
import com.unboundid.ldap.sdk.persist.LDAPField;
import com.unboundid.ldap.sdk.persist.LDAPObject;

@LDAPObject(structuralClass = "inetOrgPerson", auxiliaryClass = "tlsKeyInfo", defaultParentDN = "ou=users,ou=system")
public class User {

   @LDAPDNField
   private String dn;
  
   // The field used for RDN attribute myStringAttr.
   @LDAPField(attribute = "cn", inRDN = true, filterUsage = FilterUsage.ALWAYS_ALLOWED, requiredForEncode = true)
   private String cn;

   @LDAPField(attribute = "sn")
   private String sn;
  
   @LDAPField(attribute = "mail")
   private String mail;

   @LDAPField(attribute = "keyAlgorithm")
   private String keyAlgorithm = "RSA";

   @LDAPField(attribute = "privateKeyFormat")
   private String privateKeyFormat = "PKCS#8";

   @LDAPField(attribute = "publicKeyFormat")
   private String publicKeyFormat = "X.509";

   @LDAPField(attribute = "privateKey")
   private byte[] privateKey;

   @LDAPField(attribute = "publicKey")
   private byte[] publicKey;

   @LDAPField(attribute = "userCertificate")
   private byte[] userCertificate;

   @LDAPField(attribute = "userPKCS12")
   private byte[] userPKCS12;

  
   public String getDn() {
       return dn;
   }

   public void setDn(String dn) {
       this.dn = dn;
   }
  
   public String getCn() {
       return cn;
   }

   public void setCn(String cn) {
       this.cn = cn;
   }

   public String getSn() {
       return sn;
   }

   public void setSn(String sn) {
       this.sn = sn;
   }

   public String getMail() {
       return mail;
   }

   public void setMail(String mail) {
       this.mail = mail;
   }

   public String getKeyAlgorithm() {
       return keyAlgorithm;
   }

   public void setKeyAlgorithm(String keyAlgorithm) {
       this.keyAlgorithm = keyAlgorithm;
   }

   public String getPrivateKeyFormat() {
       return privateKeyFormat;
   }

   public void setPrivateKeyFormat(String privateKeyFormat) {
       this.privateKeyFormat = privateKeyFormat;
   }

   public String getPublicKeyFormat() {
       return publicKeyFormat;
   }

   public void setPublicKeyFormat(String publicKeyFormat) {
       this.publicKeyFormat = publicKeyFormat;
   }

   public byte[] getPrivateKey() {
       return privateKey;
   }

   public void setPrivateKey(byte[] privateKey) {
       this.privateKey = privateKey;
   }

   public byte[] getPublicKey() {
       return publicKey;
   }

   public void setPublicKey(byte[] publicKey) {
       this.publicKey = publicKey;
   }

   public byte[] getUserCertificate() {
       return userCertificate;
   }

   public void setUserCertificate(byte[] userCertificate) {
       this.userCertificate = userCertificate;
   }

   public byte[] getUserPKCS12() {
       return userPKCS12;
   }

   public void setUserPKCS12(byte[] userPKCS12) {
       this.userPKCS12 = userPKCS12;
   }
}
