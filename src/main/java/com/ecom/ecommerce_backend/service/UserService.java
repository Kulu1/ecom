package com.ecom.ecommerce_backend.service;

import com.ecom.ecommerce_backend.api.model.LoginBody;
import com.ecom.ecommerce_backend.api.model.RegistrationBody;
import com.ecom.ecommerce_backend.exception.EmailFailureException;
import com.ecom.ecommerce_backend.exception.UserAlreadyExistsException;
import com.ecom.ecommerce_backend.exception.UserNotVerifiedException;
import com.ecom.ecommerce_backend.model.LocalUser;
import com.ecom.ecommerce_backend.model.VerificationToken;
import com.ecom.ecommerce_backend.model.dao.LocalUserDAO;
import com.ecom.ecommerce_backend.model.dao.VerificationTokenDAO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private LocalUserDAO localUserDAO;
    private EncryptionService encryptionService;
    private JWTService jwtService;


    private EmailService emailService;

    private VerificationTokenDAO verificationTokenDAO;

    public UserService(LocalUserDAO localUserDAO, VerificationTokenDAO verificationTokenDAO, EmailService emailService, JWTService jwtService, EncryptionService encryptionService) {
        this.localUserDAO = localUserDAO;
        this.verificationTokenDAO = verificationTokenDAO;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.encryptionService = encryptionService;
    }

    public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistsException, EmailFailureException {
        if (localUserDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()
                || localUserDAO.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException();
        }
        LocalUser user = new LocalUser();
        user.setEmail(registrationBody.getEmail());
        user.setUsername(registrationBody.getUsername());
        user.setFirst_name(registrationBody.getFirst_name());
        user.setLast_name(registrationBody.getLast_name());
        user.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));
        VerificationToken verificationToken = createVerificationToken(user);
        emailService.sendVerificationEmail(verificationToken);
        return localUserDAO.save(user);
    }

    private VerificationToken createVerificationToken(LocalUser user){
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(jwtService.generateVerificationJWT(user));
        verificationToken.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
        verificationToken.setUser(user);;
        user.getVerificationTokens().add(verificationToken);
        return verificationToken;
    }

    public String loginUser(LoginBody loginBody) throws UserNotVerifiedException{
        Optional<LocalUser> opUser = localUserDAO.findByUsernameIgnoreCase(loginBody.getUsername());
        if(opUser.isPresent()){
            LocalUser user = opUser.get();
            if(encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())){
                if(user.isEmailVerified()){
                    return jwtService.generateJWT(user);
                } else{
                    List<VerificationToken> verificationTokens = user.getVerificationTokens();

                    boolean resend = verificationTokens.size() == 0 ||
                            verificationTokens.get(0).getCreatedTimestamp().before(new Timestamp(System.currentTimeMillis() - (60 * 60 * 1000)));
                    if(resend){
                        VerificationToken verificationToken = createVerificationToken(user);
                        verificationTokenDAO.save(verificationToken);
                        emailService.sendVerificationEmail(verificationToken);
                    }
                    throw new UserNotVerifiedException(resend);
                }

            }
        }
        return null;
    }

    @Transactional
    public boolean verifyUser(String token){
        Optional<VerificationToken> opToken = verificationTokenDAO.findByToken(token);
        if(opToken.isPresent()){
            VerificationToken verificationToken = opToken.get();
            LocalUser user = verificationToken.getUser();

            if(!user.isEmailVerified()){
                user.setEmailVerified(true);
                localUserDAO.save(user);
                verificationTokenDAO.deleteByUser(user);
                return true;
            }
        }
        return false;
    }
}
