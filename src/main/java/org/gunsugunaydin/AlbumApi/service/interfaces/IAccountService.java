package org.gunsugunaydin.AlbumApi.service.interfaces;

import java.util.List;
import java.util.Optional;
import org.gunsugunaydin.AlbumApi.model.Account;
import org.gunsugunaydin.AlbumApi.payload.auth.AccountDTO;
import org.gunsugunaydin.AlbumApi.payload.auth.AuthoritiesDTO;
import org.gunsugunaydin.AlbumApi.payload.auth.PasswordDTO;

public interface IAccountService {
    
    public Account save(Account account);
    public Account addNewUser(AccountDTO accountDTO);
    public Account configureAuthorities(AuthoritiesDTO authoritiesDTO, Account account);
    public Account updatePassword(PasswordDTO passwordDTO, Account account);
    public List<Account> findAll();
    public Optional<Account> findByEmail(String email);
    public Optional<Account> findById(long id);
    public void deleteById(long id);
}
