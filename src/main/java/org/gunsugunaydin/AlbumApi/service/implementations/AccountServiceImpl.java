package org.gunsugunaydin.AlbumApi.service.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.gunsugunaydin.AlbumApi.model.Account;
import org.gunsugunaydin.AlbumApi.payload.auth.AccountDTO;
import org.gunsugunaydin.AlbumApi.payload.auth.AuthoritiesDTO;
import org.gunsugunaydin.AlbumApi.payload.auth.PasswordDTO;
import org.gunsugunaydin.AlbumApi.repository.AccountRepository;
import org.gunsugunaydin.AlbumApi.service.interfaces.IAccountService;
import org.gunsugunaydin.AlbumApi.util.constants.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
public class AccountServiceImpl implements IAccountService, UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Account save(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        if (account.getAuthorities() == null) {
            account.setAuthorities(Authority.USER.toString());
        }
        return accountRepository.save(account);
        
    }

    @Override
    @Transactional
    public Account addNewUser(AccountDTO accountDTO) {
        Account account = new Account();
        account.setEmail(accountDTO.getEmail());
        account.setPassword(accountDTO.getPassword());
        return save(account);
    }

    @Override
    @Transactional
    public Account configureAuthorities(AuthoritiesDTO authoritiesDTO, Account account) {
        account.setAuthorities(authoritiesDTO.getAuthorites());
        return save(account);
    }

    @Override
    @Transactional
    public Account updatePassword(PasswordDTO passwordDTO, Account account) {
        account.setPassword(passwordDTO.getPassword());
        return save(account);
    }

    @Override
    public List<Account> findAll() {
       return accountRepository.findAll();

    }

    @Override
    public Optional<Account> findByEmail(String email) {
        return accountRepository.findByEmail(email);
        
    }

    @Override
    public Optional<Account> findById(long id) {
        return accountRepository.findById(id);
        
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        accountRepository.deleteById(id);
    }

    //Load a user by username(email), used for authentication.
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       Optional<Account> optionalAccount =  accountRepository.findByEmail(email);
       if (!optionalAccount.isPresent()) {
            throw new UsernameNotFoundException("Account not found");
       }
       Account account = optionalAccount.get();

       List<GrantedAuthority> grantedAuthority = new ArrayList<>();
       grantedAuthority.add(new SimpleGrantedAuthority(account.getAuthorities()));
       return new User(account.getEmail(), account.getPassword(), grantedAuthority);
    }
}
