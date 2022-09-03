package org.kumoricon.registration.inlinereg;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.encoders.Base64;
import org.kumoricon.registration.helpers.FieldCleaner;
import org.kumoricon.registration.inlinereg.model.InLineRegistrationAttendee;
import org.kumoricon.registration.inlinereg.model.InLineRegistrationFile;
import org.kumoricon.registration.inlinereg.model.InLineRegistrationOrder;
import org.kumoricon.registration.inlinereg.model.InLineRegistrationRecord;
import org.kumoricon.registration.model.ImportService;
import org.kumoricon.registration.model.inlineregistration.InLineRegRepository;
import org.kumoricon.registration.model.inlineregistration.InLineRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.time.LocalDate;
import java.util.UUID;


@Service
public class InLineRegistrationImportService extends ImportService {
    private static final Logger log = LoggerFactory.getLogger(InLineRegistrationImportService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final InLineRegRepository inLineRegRepository;
    private boolean initialized = false;
    private PrivateKey privateKey;

    public InLineRegistrationImportService(
            @Value("${inLineRegistration.onlineImportPath}") String importInputPath,
            @Value("${inLineRegistration.onlineImportGlob}") String importGlob,
            @Value("${inLineRegistration.onlineDLQPath}") String importDLQPath,
            @Value("${inLineRegistration.privateKey}") String privateKeyFilename,
            InLineRegRepository inLineRegRepository) {
        Security.setProperty("crypto.policy", "unlimited");
        Security.addProvider(new BouncyCastleProvider());

        this.inLineRegRepository = inLineRegRepository;

        onlineImportInputPath = importInputPath;
        this.onlineImportGlob = importGlob;
        onlineDLQPath = importDLQPath;

        log.info("Reading private key from {}", privateKeyFilename);
        try {
            this.privateKey = readPrivateKey(new File(privateKeyFilename));
            this.initialized = true;
        } catch (IOException ex) {
            log.warn("Error loading private key, In Line Registration import is disabled: {}",  ex.getMessage());
        }
    }

    protected void processImportFile(InLineRegistrationFile fileData) {
        int attendeeCount = 0;
        int successCount = 0;
        int orderSuccessCount = 0;
        for (InLineRegistrationRecord record : fileData.getData()) {
            try {
                String decrypted = decrypt(record.getData());
                InLineRegistrationOrder order = objectMapper.readValue(decrypted, InLineRegistrationOrder.class);
                for (InLineRegistrationAttendee a : order.getAttendees()) {
                    attendeeCount += 1;
                    try {
                        InLineRegistration ilr = new InLineRegistration();
                        ilr.setUuid(UUID.fromString(a.getUuid().strip()));
                        ilr.setOrderUuid(UUID.fromString(order.getOrderUuid()));
                        ilr.setFirstName(FieldCleaner.cleanName(a.getFirstName()));
                        ilr.setLastName(FieldCleaner.cleanName(a.getLastName()));
                        ilr.setLegalFirstName(FieldCleaner.cleanName(a.getFirstNameOnId()));
                        ilr.setLegalLastName(FieldCleaner.cleanName(a.getLastNameOnId()));
                        ilr.setNameIsLegalName(a.getNameOnIdSame());
                        ilr.setPreferredPronoun(a.getPronouns().strip());
                        ilr.setZip(a.getPostal().strip());
                        ilr.setCountry(a.getCountry().strip());
                        ilr.setPhoneNumber(FieldCleaner.cleanPhoneNumber(a.getPhone()));
                        ilr.setEmail(a.getEmail().strip());
                        ilr.setBirthDate(LocalDate.of(a.getBirthYear(), a.getBirthMonth(), a.getBirthDay()));
                        ilr.setEmergencyContactFullName(FieldCleaner.cleanName(a.getEmergencyName()));
                        ilr.setEmergencyContactPhone(FieldCleaner.cleanPhoneNumber(a.getEmergencyPhone()));
                        ilr.setParentIsEmergencyContact(!a.getParentContactSeparate());
                        ilr.setParentFullName(FieldCleaner.cleanName(a.getParentName()));
                        ilr.setParentPhone(FieldCleaner.cleanPhoneNumber(a.getParentPhone()));
                        ilr.setConfirmationCode(record.getConfirmationCode().strip());
                        ilr.setMembershipType(a.getMembershipType().strip());
                        inLineRegRepository.upsert(ilr);
                        successCount += 1;
                    } catch (Exception ex) {
                        log.error("Error importing in line registration {} confirmation code {}: {}",
                                a.getUuid(), record.getConfirmationCode(), ex.getMessage());
                    }
                }
                orderSuccessCount += 1;
            } catch (Exception e) {
                log.error("Error decrypting {} confirmation code {}: {}",  record.getId(), record.getConfirmationCode(), e.getMessage());
            }
        }
        log.info("Imported {}/{} orders and {}/{} attendees", orderSuccessCount, fileData.getData().size(), successCount, attendeeCount);
    }

    protected void importFile(Path filepath) throws IOException {
        InLineRegistrationFile importFile = objectMapper.readValue(filepath.toFile(), InLineRegistrationFile.class);
        processImportFile(importFile);
    }



    private RSAPrivateKey readPrivateKey(File file) throws IOException {
        try (FileReader keyReader = new FileReader(file)) {
            PEMParser pemParser = new PEMParser(keyReader);
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(pemParser.readObject());
            return (RSAPrivateKey) converter.getPrivateKey(privateKeyInfo);
        }
    }

    /**
     * Splits apart a message from the public site and decrypts the JSON body
     * @param input String with three Base64 encoded parts: encrypted key:iv:encrypted body
     * @return JSON message body as string
     */
    private String decrypt(String input) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidKeyException, InvalidAlgorithmParameterException {
        if (initialized) {
            String[] parts = input.split(":");
            if (parts.length < 3) {
                throw new RuntimeException("Invalid input data");
            }
            byte[] key = decryptKey(Base64.decode(parts[0]));
            byte[] iv = Base64.decode(parts[1]);
            return decryptBody(Base64.decode(parts[2]), key, iv);
        } else {
            return null;
        }
    }


    private byte[] decryptKey(byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/NONE/OAEPPadding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    private String decryptBody(byte[] encryptedBody, byte[] key, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv, 0, cipher.getBlockSize()));
        byte[] output = cipher.doFinal(encryptedBody);
        return new String(output);
    }
}
